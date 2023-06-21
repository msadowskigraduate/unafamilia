'use client';

import { User } from "next-auth";
import Container from "../Container";
import Logo from "./Logo";
import UserMenu from "./UserMenu";
import { SafeUser } from "@/app/types";
import { useState } from "react";

interface NavbarProps {
  currentUser?: SafeUser | null;
}

const Navbar: React.FC<NavbarProps> = ({
  currentUser,
}) => {

  const [isOpen, setIsOpen] = useState(false)

  return (
    <div className="h-full bg-neutral-800 z-10 shadow-sm fixed  transition">
      <div className="py-4 border-b-[1px] border-neutral-700">
        <Container>
          <div
            className="
                    flex
                    flex-col
                    items-center
                    justify-between
                    sm:justify-center
                    gap-3
                    md:gap-0
                    "
          >
            <Logo />
          </div>
        </Container>
      </div>
      <UserMenu currentUser={currentUser}/>
    </div>
  );
};

export default Navbar;
