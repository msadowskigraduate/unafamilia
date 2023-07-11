import "./globals.css";
import { Nunito } from "next/font/google";

export const metadata = {
  title: "Una Familia - Where men become gods.",
  description: "Mythic Raiding PvE Guild",
};

const font = Nunito({
  subsets: ["latin"],
});

export default async function EventLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <>
        {children}
    </>
  );
}