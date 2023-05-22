"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";

const Logo = () => {
  return (
    <Image
      alt="Logo"
      className="hidden md:block cursor-pointer rounded-full"
      height="100"
      width="100"
      src="/images/logo.jpg"
    />
  );
};

export default Logo;
