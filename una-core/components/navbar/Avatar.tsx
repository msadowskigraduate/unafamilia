'use client';

import Image from "next/image";

const Avatar = () => {
    return ( 
        <Image
            className="rounded-full"
            height="42"
            width="42"
            alt="Avatar"
            src="/images/placeholder-avatar.png"
        />
     );
}
 
export default Avatar;