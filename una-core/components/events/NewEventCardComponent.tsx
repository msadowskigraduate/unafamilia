'use client'

import { IoMdAddCircleOutline } from "react-icons/io";
 
const NewEventCardComponent = () => {
    return ( 
        <div className="
            border-dashed
            border-4
            rounded-md
            border-amber-500
            backdrop-blur-md
            p-2
            cursor-pointer
            flex-col
            grid
            h-64
            w-48
            place-items-center
            text-amber-500
            hover:invert
        "
        onClick={() => {console.log('New Event!')}}
        >

        <IoMdAddCircleOutline size={48} opacity={0.5}/>
        <div className="
            text-center
            sm:hidden
        ">
            Create new Event!</div>
        </div>
    );
}
 
export default NewEventCardComponent;