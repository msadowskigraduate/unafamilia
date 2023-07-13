import { EventData } from "@/app/types";
import EventCardComponent from "@/components/events/EventCardComponent";
import NewEventCardComponent from "@/components/events/NewEventCardComponent";

export default function Events() {
    const events = getEventStubs();
    return (
        <div className="
            p-4
            flex
            flex-col
        ">
            <h2 className="
                text-left
                text-2xl
                font-semibold
            ">Upcoming Events</h2>
            <div className="
                flex
                flex-row
                flex-nowrap
                gap-2
                p-4  
            ">
                <NewEventCardComponent />
                {events.map((event: EventData) => (
                    <EventCardComponent event={event} />
                ))}
            </div>
        </div>

    );
}

function getEventStubs() {
    let e: EventData = {
        id: "1234",
        name: "Aberrus Mythic",
        backgroundUrl: "https://64.media.tumblr.com/a9be1675a6218d1e55711bd6b6ca52cd/8a3515ff4d9f24ce-b2/s640x960/41d55bae15c9786d485912d0b32144cbd97865ea.jpg",
        roster: 20,
        accepted: 19,
        date: new Date()
    }

    let p: EventData = {
        id: "1234",
        name: "Social Raid - Aberrus Heroic",
        backgroundUrl: "https://fr.roccat.com/cdn/shop/articles/unnamed_f871487a-771d-48ac-930b-d4ed53f622d7.jpg?v=1685109788",
        roster: 30,
        accepted: 19,
        date: new Date()
    }
    return [e, p]
}