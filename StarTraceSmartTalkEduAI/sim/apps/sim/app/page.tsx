import Link from 'next/link'
import { ArrowRight, Sparkles, Zap, Shield, Code } from 'lucide-react'
import { Nav } from '@/app/(landing)/components'
import { soehne } from '@/app/_styles/fonts/soehne/soehne'

export default function HomePage() {
  return (
    <div className='min-h-screen bg-background'>
      <Nav />
      
      <main className='flex flex-col items-center justify-center px-4 py-16 sm:px-8 md:px-16'>
        {/* Hero Section */}
        <section className='flex w-full max-w-6xl flex-col items-center justify-center text-center'>
          <div className='mb-8 inline-flex items-center gap-2 rounded-full border border-border bg-muted/50 px-4 py-2 text-sm text-muted-foreground'>
            <Sparkles className='h-4 w-4' />
            <span>欢迎来到 Sim Studio hello world</span>
          </div>
          
          <h1 className={`${soehne.className} mb-6 text-4xl font-semibold tracking-tight sm:text-5xl md:text-6xl lg:text-7xl`}>
            构建强大的
            <br />
            <span className='bg-gradient-to-r from-purple-600 to-blue-600 bg-clip-text text-transparent'>
              AI 工作流
            </span>
          </h1>
          
          <p className='mb-8 max-w-2xl text-lg text-muted-foreground sm:text-xl'>
            使用 Sim Studio 轻松创建、部署和管理 AI 代理工作流。
            连接各种服务，自动化您的业务流程。
          </p>
          
          <div className='flex flex-col gap-4 sm:flex-row'>
            <Link
              href='/signup'
              className='group inline-flex items-center justify-center gap-2 rounded-lg bg-gradient-to-r from-purple-600 to-blue-600 px-8 py-3 text-base font-medium text-white shadow-lg transition-all hover:shadow-xl hover:scale-105'
            >
              开始使用
              <ArrowRight className='h-4 w-4 transition-transform group-hover:translate-x-1' />
            </Link>
            <Link
              href='/login'
              className='inline-flex items-center justify-center rounded-lg border border-border bg-background px-8 py-3 text-base font-medium transition-colors hover:bg-muted'
            >
              登录
            </Link>
          </div>
        </section>

        {/* Features Section */}
        <section className='mt-24 w-full max-w-6xl'>
          <h2 className={`${soehne.className} mb-12 text-center text-3xl font-semibold sm:text-4xl`}>
            核心功能
          </h2>
          
          <div className='grid gap-8 sm:grid-cols-2 lg:grid-cols-3'>
            <FeatureCard
              icon={<Zap className='h-6 w-6' />}
              title='快速构建'
              description='使用直观的可视化界面快速构建 AI 工作流，无需编写复杂代码。'
            />
            <FeatureCard
              icon={<Code className='h-6 w-6' />}
              title='灵活集成'
              description='连接 Slack、Gmail、Notion 等数百种服务，打造无缝的工作流体验。'
            />
            <FeatureCard
              icon={<Shield className='h-6 w-6' />}
              title='安全可靠'
              description='企业级安全保障，确保您的数据和流程安全无忧。'
            />
          </div>
        </section>

        {/* CTA Section */}
        <section className='mt-24 w-full max-w-4xl rounded-2xl border border-border bg-muted/30 p-12 text-center'>
          <h2 className={`${soehne.className} mb-4 text-3xl font-semibold sm:text-4xl`}>
            准备开始了吗？
          </h2>
          <p className='mb-8 text-lg text-muted-foreground'>
            立即注册，开始构建您的第一个 AI 工作流
          </p>
          <Link
            href='/signup'
            className='group inline-flex items-center gap-2 rounded-lg bg-gradient-to-r from-purple-600 to-blue-600 px-8 py-3 text-base font-medium text-white shadow-lg transition-all hover:shadow-xl hover:scale-105'
          >
            免费开始
            <ArrowRight className='h-4 w-4 transition-transform group-hover:translate-x-1' />
          </Link>
        </section>
      </main>
    </div>
  )
}

function FeatureCard({
  icon,
  title,
  description,
}: {
  icon: React.ReactNode
  title: string
  description: string
}) {
  return (
    <div className='group rounded-lg border border-border bg-card p-6 transition-all hover:shadow-lg hover:scale-105'>
      <div className='mb-4 inline-flex items-center justify-center rounded-lg bg-muted p-3 text-primary'>
        {icon}
      </div>
      <h3 className={`${soehne.className} mb-2 text-xl font-semibold`}>{title}</h3>
      <p className='text-muted-foreground'>{description}</p>
    </div>
  )
}
