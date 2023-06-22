'use client';

import React from "react";

interface MenuItemProps {
    onClick: () => void;
    label: string;
}

const MenuItem: React.FC<MenuItemProps> = ({
    onClick,
    label
}) => {
    return (
        <div
            onClick={onClick}
            className="
                px-4
                py-3
                hover:bg-amber-400
                hover:text-neutral-800
                transition
                font-semibold
            "
        >
            {label}
        </div>
    );
}

export default MenuItem;