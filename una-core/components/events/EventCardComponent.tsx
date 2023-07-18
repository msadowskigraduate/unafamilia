'use client'

import { EventData } from "@/app/types";
import { GiEvilMinion } from "react-icons/gi";

interface EventCardComponentProps {
    event: EventData
}
 
const EventCardComponent: React.FC<EventCardComponentProps> = (
    event
) => {
    return ( 
        <div className={`
                        grid
                        border-amber-500
                        border-4
                        bg-cover
                        bg-center
                        bg-no-repeat
                        flex-col
                        gap-3
                        content-around
                        p-4
                        min-h-full
                        h-64
                        w-48
        `}
        style={{ backgroundImage: "url(" + event.event.backgroundUrl + ")" }}
        >

            <div className="
                text-xl
                font-semibold
                text-center
                text-wrap
            ">
                {event.event.name}
            </div>

            <div className="
                row-start-2
            ">
                {event.event.date.toUTCString()}
            </div>

            <div className="
                flex
                flex-row
                gap-2
                row-start-3
                place-self-end
                items-center
                sm:hidden
            ">
                <GiEvilMinion size={24}/>  
                {event.event.accepted} / {event.event.roster}
            </div>
        </div>
    );
}
 
export default EventCardComponent;