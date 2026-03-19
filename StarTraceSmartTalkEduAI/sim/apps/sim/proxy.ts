import { createLogger } from '@sim/logger'
import { getSessionCookie } from 'better-auth/cookies'
import { type NextRequest, NextResponse } from 'next/server'
import { isAuthDisabled, isHosted } from './lib/core/config/feature-flags'
import { generateRuntimeCSP, getEmbedCSPPolicy } from './lib/core/security/csp'

const logger = createLogger('Proxy')

const SUSPICIOUS_UA_PATTERNS = [
  /^\s*$/, // Empty user agents
  /\.\./, // Path traversal attempt
  /<\s*script/i, // Potential XSS payloads
  /^\(\)\s*{/, // Command execution attempt
  /\b(sqlmap|nikto|gobuster|dirb|nmap)\b/i, // Known scanning tools
] as const

/**
 * Handles authentication-based redirects for root paths
 */
function handleRootPathRedirects(
  request: NextRequest,
  hasActiveSession: boolean
): NextResponse | null {
  const url = request.nextUrl

  if (url.pathname !== '/') {
    return null
  }

  // Allow public access to the homepage (/). Do not redirect from root.
  // Other auth/redirect behavior (e.g. /workspace -> /login) is handled elsewhere.
  return null
}

/**
 * Handles invitation link redirects for unauthenticated users
 */
function handleInvitationRedirects(
  request: NextRequest,
  hasActiveSession: boolean
): NextResponse | null {
  if (!request.nextUrl.pathname.startsWith('/invite/')) {
    return null
  }

  if (
    !hasActiveSession &&
    !request.nextUrl.pathname.endsWith('/login') &&
    !request.nextUrl.pathname.endsWith('/signup') &&
    !request.nextUrl.search.includes('callbackUrl')
  ) {
    const token = request.nextUrl.searchParams.get('token')
    const inviteId = request.nextUrl.pathname.split('/').pop()
    const callbackParam = encodeURIComponent(`/invite/${inviteId}${token ? `?token=${token}` : ''}`)
    return NextResponse.redirect(
      new URL(`/login?callbackUrl=${callbackParam}&invite_flow=true`, request.url)
    )
  }
  return NextResponse.next()
}

/**
 * Handles workspace invitation API endpoint access
 */
function handleWorkspaceInvitationAPI(
  request: NextRequest,
  hasActiveSession: boolean
): NextResponse | null {
  if (!request.nextUrl.pathname.startsWith('/api/workspaces/invitations')) {
    return null
  }

  if (request.nextUrl.pathname.includes('/accept') && !hasActiveSession) {
    const token = request.nextUrl.searchParams.get('token')
    if (token) {
      return NextResponse.redirect(new URL(`/invite/${token}?token=${token}`, request.url))
    }
  }
  return NextResponse.next()
}

/**
 * Handles security filtering for suspicious user agents
 */
function handleSecurityFiltering(request: NextRequest): NextResponse | null {
  const userAgent = request.headers.get('user-agent') || ''
  const isWebhookEndpoint = request.nextUrl.pathname.startsWith('/api/webhooks/trigger/')
  const isSuspicious = SUSPICIOUS_UA_PATTERNS.some((pattern) => pattern.test(userAgent))

  // Block suspicious requests, but exempt webhook endpoints from User-Agent validation
  if (isSuspicious && !isWebhookEndpoint) {
    logger.warn('Blocked suspicious request', {
      userAgent,
      ip: request.headers.get('x-forwarded-for') || 'unknown',
      url: request.url,
      method: request.method,
      pattern: SUSPICIOUS_UA_PATTERNS.find((pattern) => pattern.test(userAgent))?.toString(),
    })

    return new NextResponse(null, {
      status: 403,
      statusText: 'Forbidden',
      headers: {
        'Content-Type': 'text/plain',
        'X-Content-Type-Options': 'nosniff',
        'Content-Security-Policy': "default-src 'none'",
        'Cache-Control': 'no-store, no-cache, must-revalidate, proxy-revalidate',
        Pragma: 'no-cache',
        Expires: '0',
      },
    })
  }

  return null
}

export async function proxy(request: NextRequest) {
  const url = request.nextUrl

  const sessionCookie = getSessionCookie(request)
  const hasActiveSession = isAuthDisabled || !!sessionCookie

  // Debug logging for development
  if (process.env.NODE_ENV === 'development') {
    logger.info('Proxy request', { pathname: url.pathname, hasActiveSession })
  }

  // Priority 1: Handle root path - allow direct access for iframe embedding
  if (url.pathname === '/') {
    const response = NextResponse.next()
    // Use embed CSP policy to allow iframe embedding for EDUAI frontend
    response.headers.set('Content-Security-Policy', getEmbedCSPPolicy())
    return response
  }
  
  // Handle other root path redirects (legacy)
  const redirect = handleRootPathRedirects(request, hasActiveSession)
  if (redirect) return redirect

  // Priority 2: Handle /workspace - allow direct access for iframe embedding
  // Don't redirect to /university-platform when accessed from iframe
  if (url.pathname === '/workspace') {
    if (!hasActiveSession) {
      return NextResponse.redirect(new URL('/login', request.url))
    }
    const response = NextResponse.next()
    // Use embed CSP policy to allow iframe embedding for EDUAI frontend
    response.headers.set('Content-Security-Policy', getEmbedCSPPolicy())
    return response
  }

  // Priority 3: Handle login/signup redirects
  if (url.pathname === '/login' || url.pathname === '/signup') {
    if (hasActiveSession) {
      // Redirect authenticated users to university platform instead of workspace
      return NextResponse.redirect(new URL('/university-platform', request.url))
    }
    const response = NextResponse.next()
    // Use embed CSP policy to allow iframe embedding for EDUAI frontend
    response.headers.set('Content-Security-Policy', getEmbedCSPPolicy())
    return response
  }

  // Priority 4: Allow public access to specific paths
  if (url.pathname.startsWith('/chat/')) {
    return NextResponse.next()
  }

  // Allow public access to embed pages (for EDUAI iframe embedding)
  if (url.pathname.startsWith('/embed/')) {
    return NextResponse.next()
  }

  // Allow public access to template pages for SEO
  if (url.pathname.startsWith('/templates')) {
    return NextResponse.next()
  }

  // Allow public access to university platform pages
  if (url.pathname.startsWith('/university-platform')) {
    if (process.env.NODE_ENV === 'development') {
      logger.info('Allowing access to university-platform', { pathname: url.pathname })
    }
    const response = NextResponse.next()
    // Use embed CSP policy to allow iframe embedding for EDUAI frontend
    response.headers.set('Content-Security-Policy', getEmbedCSPPolicy())
    return response
  }

  // Allow access to test routes in development
  if (process.env.NODE_ENV === 'development' && url.pathname.startsWith('/test-route')) {
    logger.info('Allowing access to test-route', { pathname: url.pathname })
    return NextResponse.next()
  }

  // Priority 5: Handle other workspace paths
  if (url.pathname.startsWith('/workspace')) {
    // Allow public access to workspace template pages - they handle their own redirects
    if (url.pathname.match(/^\/workspace\/[^/]+\/templates/)) {
      const response = NextResponse.next()
      response.headers.set('Content-Security-Policy', getEmbedCSPPolicy())
      return response
    }

    if (!hasActiveSession) {
      return NextResponse.redirect(new URL('/login', request.url))
    }
    const response = NextResponse.next()
    // Use embed CSP policy to allow iframe embedding for EDUAI frontend
    response.headers.set('Content-Security-Policy', getEmbedCSPPolicy())
    return response
  }

  const invitationRedirect = handleInvitationRedirects(request, hasActiveSession)
  if (invitationRedirect) return invitationRedirect

  const workspaceInvitationRedirect = handleWorkspaceInvitationAPI(request, hasActiveSession)
  if (workspaceInvitationRedirect) return workspaceInvitationRedirect

  const securityBlock = handleSecurityFiltering(request)
  if (securityBlock) return securityBlock

  const response = NextResponse.next()
  response.headers.set('Vary', 'User-Agent')

  // Set CSP for routes that need runtime CSP
  // Use embed CSP for workspace routes, root path, and university-platform to allow iframe embedding
  if (
    url.pathname.startsWith('/workspace') ||
    url.pathname === '/' ||
    url.pathname.startsWith('/university-platform')
  ) {
    response.headers.set('Content-Security-Policy', getEmbedCSPPolicy())
  } else if (url.pathname.startsWith('/chat')) {
    response.headers.set('Content-Security-Policy', generateRuntimeCSP())
  }

  return response
}

export const config = {
  matcher: [
    '/', // Root path for self-hosted redirect logic
    '/terms', // Whitelabel terms redirect
    '/privacy', // Whitelabel privacy redirect
    '/w', // Legacy /w redirect
    '/w/:path*', // Legacy /w/* redirects
    '/workspace/:path*', // New workspace routes
    '/embed/:path*', // Embed routes for iframe embedding
    '/login',
    '/signup',
    '/invite/:path*', // Match invitation routes
    // Catch-all for other pages, excluding static assets and public directories
    '/((?!_next/static|_next/image|favicon.ico|logo/|static/|footer/|social/|enterprise/|favicon/|twitter/|robots.txt|sitemap.xml).*)',
  ],
}
