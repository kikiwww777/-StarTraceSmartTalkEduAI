import type { ReactNode } from 'react'
import { season } from '@/app/_styles/fonts/season/season'

/**
 * Minimal layout for iframe-embedded pages.
 *
 * Rationale:
 * The main app layout/metadata generation requires NEXT_PUBLIC_APP_URL in dev.
 * For EDUAI embedding we want a lightweight route that can boot without that env.
 */
export default function EmbedLayout({ children }: { children: ReactNode }) {
  return (
    <html
      lang='en'
      suppressHydrationWarning
      className='light'
      style={{ colorScheme: 'light' }}
    >
      <body
        className={`${season.variable} font-season`}
        suppressHydrationWarning
      >
        {children}
      </body>
    </html>
  )
}


