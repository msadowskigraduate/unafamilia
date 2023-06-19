'use client';

import { User } from "next-auth";
import Container from "../Container";
import Logo from "./Logo";
import UserMenu from "./UserMenu";
import { SafeUser } from "@/app/types";

interface NavbarProps {
  currentUser?: SafeUser | null;
}

const Navbar: React.FC<NavbarProps> = ({
  currentUser,
}) => {
  return (
    <div className="fixed w-full bg-neutral-800 z-10 shadow-sm">
      <div className="py-4 border-b-[1px] border-neutral-700">
        <Container>
          <div
            className="
                    flex
                    flex-row
                    items-center
                    justify-between
                    gap-3
                    md:gap-0
                    "
          >
            <Logo />
            <UserMenu currentUser={currentUser}/>
          </div>
        </Container>
      </div>
    </div>
  );
};

export default Navbar;
