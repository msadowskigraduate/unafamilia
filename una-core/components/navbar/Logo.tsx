"use client";

import Image from "next/image";

const Logo = () => {
  return (
    <Image
      alt="Logo"
      className="hidden md:block cursor-pointer rounded-full"
      width={72}
      height={72}
      src="/images/logo.jpg"
    />
  );
};

export default Logo;
                                                  