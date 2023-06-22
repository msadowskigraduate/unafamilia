'use client';

import React from "react";

interface NavbarItemProps {
    onClick: () => void;
    label: string;
    children: React.ReactNode
}

const NavbarItem: React.FC<NavbarItemProps> = ({
    onClick,
    label,
    children,
}) => {
    return (
        <div
            onClick={onClick}
            className="
                px-4
                py-4
                hover:bg-amber-500
                hover:text-neutral-800
                hover:font-semibold
                transition
                flex
                flex-row
                items-center
                cursor-pointer
                justify-evenly
            "
        >
            {children}
            <span className="hidden md:block">{label}</span>
        </div>
    );
}

export default NavbarItem;