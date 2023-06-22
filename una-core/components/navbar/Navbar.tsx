'use client';

import { AiOutlineMore } from "react-icons/ai";
import { GiBattleGear } from "react-icons/gi"
import { BsCalendarWeek } from "react-icons/bs"
import Container from "../Container";
import Logo from "./Logo";
import UserMenu from "./UserMenu";
import { SafeUser } from "@/app/types";
import { useState } from "react";
import NavbarItem from "./NavbarItem";
import { useRouter } from "next/navigation";

interface NavbarProps {
  currentUser?: SafeUser | null;
}

const Navbar: React.FC<NavbarProps> = ({
  currentUser,
}) => {

  const [isOpen, setIsOpen] = useState(false)

  const router = useRouter();

  return (
    <div className={`
                  flex
                  flex-col
                  bg-neutral-800 
                  z-10 
                  shadow-lg 
                  fixed
                  inset-y-0
                  left-0
                  transition
                  box-content
                  ${isOpen ? "sm:w-80" : "sm:w-20"}
                  min-h-screen
                  place-content-start
                `}
                onMouseLeave={() => setIsOpen(false)}
    >
      <div className="py-4 border-b-[1px] border-neutral-700 self-center">
        <Container>
          <div className="border-amber-500 border-1">
          
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

      <UserMenu currentUser={currentUser} isSideBarOpen={isOpen} />

      <NavbarItem onClick={ () => router.push('/events') } label={"Calendar"}> 
        <BsCalendarWeek size={24}/> 
      </NavbarItem>

      <NavbarItem onClick={ () => router.push('/roster') } label={"Roster"}> 
        <GiBattleGear size={24}/> 
      </NavbarItem>

      <NavbarItem onClick={function (): void {
        throw new Error("Function not implemented.");
      } } label={"New Function"}> 
        <AiOutlineMore size={24}/> 
      </NavbarItem>

      <NavbarItem onClick={function (): void {
        throw new Error("Function not implemented.");
      } } label={"New Function"}> 
        <AiOutlineMore size={24}/> 
      </NavbarItem>

    </div>
  );
};

export default Navbar;
