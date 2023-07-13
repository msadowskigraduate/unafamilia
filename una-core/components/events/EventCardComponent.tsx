'use client'

import { EventData } from "@/app/types";

interface EventCardComponentProps {
    event: EventData
}
 
const EventCardComponent: React.FC<EventCardComponentProps> = (
    event
) => {
    return ( 
        <div className={`
                        flex
                        border-amber-500
                        border-4
                        bg-cover
                        bg-center
                        bg-no-repeat
                        flex-col
                        xl:w-48
                        xl:h-72
                        md:w-36
                        md:h-48
                        grid-rows-3
        `}
        style={{ backgroundImage: "url(" + event.event.backgroundUrl + ")" }}
        >

            <div className="
                text-xl
                font-semibold
            ">{event.event.name}</div>

            <div></div>
        </div>
    );
}
 
export default EventCardComponent;