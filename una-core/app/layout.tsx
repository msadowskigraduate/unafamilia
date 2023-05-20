import Navbar from '@/components/navbar/Navbar'
import './globals.css'
import { Inter, Nunito } from 'next/font/google'

const inter = Inter({ subsets: ['latin'] })

export const metadata = {
  title: 'Una Familia',
  description: 'Mythic Raiding PvE Guild',
}

const font = Nunito({
  subsets: ['latin'],
})

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <Navbar />
        {children}
        </body>
    </html>
  )
}
