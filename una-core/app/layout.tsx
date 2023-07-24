import "./globals.css";
import { Nunito } from "next/font/google";

export const metadata = {
  title: "Una Familia - Where men become gods.",
  description: "Mythic Raiding PvE Guild",
};

const font = Nunito({
  subsets: ["latin"],
});

export default async function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body
        className={`
                  ${font.className}
                  text-amber-500
                  bg-left-bottom
                  bg-no-repeat
                  bg-cover
                  bg-[url('https://wow.zamimg.com/uploads/screenshots/normal/1074416.jpg')]
        `}>
        {children}
      </body>
    </html>
  );
}
