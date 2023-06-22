"use client";

import React, { useCallback, useState } from "react";
import Avatar from "./Avatar";
import MenuItem from "./MenuItem";
import { signOut } from "next-auth/react";
import { SafeUser } from "@/app/types";

interface UserMenuProps {
  currentUser?: SafeUser | null;
  isSideBarOpen: boolean;
}

const UserMenu: React.FC<UserMenuProps> = ({ currentUser, isSideBarOpen }) => {
  const [isOpen, setIsOpen] = useState(false);

  const toggleOpen = useCallback(() => {
    setIsOpen((value) => !value);
  }, []);

  return (
    <div className="relative">
      {currentUser != null && (
        <div>
          <div
            onClick={toggleOpen}
            className="
                        flex
                        flex-col
                        items-center
                        hover:bg-amber-500
                        hover:text-neutral-800
                        transition
                        cursor-pointer
                        gap-3
                        py-3
                    "
          >
            <div className={`${isSideBarOpen ? "" : "sm:hidden"} hover:font-semibold`}>
              {currentUser.name}
            </div>

            <div className="md:block">
              <Avatar />
            </div>
          </div>
        </div>
      )}

      {isOpen && (
        <div
          className="
                        absolute
                        rounder-xl
                        shadow-md
                        min-w-[25%]
                        w-full
                        bg-neutral-500
                        overflow-hidden
                        text-sm
                        z-11
                    "
                    onMouseLeave={toggleOpen}
        >
          <div className="flex flex-col cursor-pointer">
            <MenuItem onClick={() => {console.log("GO TO PROFILE")}} label="Profile" />
            <MenuItem onClick={() => { signOut() }}
              label="Sign Out"
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default UserMenu;
