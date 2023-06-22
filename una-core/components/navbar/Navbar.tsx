'use client';

import { AiOutlineMore } from "react-icons/ai";
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
    <div className={`
                  flex
                  flex-col
                  h-full
                  max-w-lg
                  bg-neutral-800 
                  z-10 
                  shadow-sm 
                  fixed 
                  transition
                  
                `}
    >
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

          <button className="
              hidden
              sm:block
              sm:text-amber-500
              sm:cursor-pointer
              sm:hover:bg-amber-500
              sm:hover:text-neutral-800
              sm:transition
              sm:rounded-full
            "
            onClick={() => setIsOpen(!isOpen)}>
            <AiOutlineMore size={48} />
          </button>
        </Container>
      </div>

      <div className="w-full">
        <UserMenu currentUser={currentUser} />
      </div>
      
    </div>
  );
};

export default Navbar;
