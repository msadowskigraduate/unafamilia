'use client';

import React, { useCallback, useState } from "react";
import Avatar from "./Avatar";
import MenuItem from "./MenuItem";
import { signOut } from "next-auth/react";
import { SafeUser } from "@/app/types";

interface UserMenuProps {
    currentUser?: SafeUser | null
}

const UserMenu: React.FC<UserMenuProps> = ({
    currentUser
}) => {
    const [isOpen, setIsOpen] = useState(false);

    const toggleOpen = useCallback(() => {
        setIsOpen((value) => !value);
    }, []);

    return (
        <div className="relative">
            {currentUser != null && (
                <div className="flex flex-col align-middle items-center gap-3">
                    <div
                        onClick={toggleOpen}
                        className="
                            p-4
                            md:py-1
                            md:px-2
                            flex
                            flex-row
                            items-center
                            gap-3
                            hover:shadow-md
                            hover:border-amber-500
                            transition
                            cursor-pointer
                        "
                    >
                        <div className="
                            sm:hidden
                            text-amber-400
                        "
                        >{currentUser.name}</div>
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
                        w-[40vw]
                        md:w-3/4
                        bg-neutral-600
                        overflow-hidden
                        left-0
                        top-12
                        text-sm
                    "
                >
                    <div className="flex flex-col cursor-pointer">
                        <MenuItem
                            onClick={() => { signOut() }}
                            label="Sign Out"
                        />
                    </div>
                </div>
            )}
        </div>
    );
}

export default UserMenu;