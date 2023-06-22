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
                            py-3
                            px-3
                            flex
                            flex-col
                            items-center
                            gap-3
                            border-2
                            border-neutral-800
                            hover:border-solid
                            hover:border-amber-500
                            transition
                            cursor-pointer
                            my-0
                        "
                    >
                        <div className="
                                sm:hidden
                                text-amber-400
                            ">
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
                        w-[30vw]
                        md:w-3/4
                        bg-neutral-300
                        overflow-hidden
                        text-sm
                    "
                >
                    <div className="flex flex-col cursor-pointer">
                        <MenuItem
                                onClick={() => { }}
                                label="Profile"
                            />
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