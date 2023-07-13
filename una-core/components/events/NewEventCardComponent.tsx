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
            relative
            p-2
            xl:w-48
            xl:h-72
            md:w-36
            md:h-48
            cursor-pointer
            flex
            flex-col
            grid
            place-items-center
            text-amber-500
            hover:invert
        "
        onClick={() => {console.log('New Event!')}}
        >

        <IoMdAddCircleOutline size={48} opacity={0.5}/>
        <div>Create new Event!</div>
        </div>
    );
}
 
export default NewEventCardComponent;