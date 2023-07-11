import EventCardComponent from "@/components/events/EventCardComponent";

export default function Events() {
    const events = getEventStubs();
    return (
        <div className="flex flex-col gap-2">
            {events.map((event) => (
                <EventCardComponent event={event}/>
            ))}
        </div>

    );
}

function getEventStubs() {
    return [{
        id: "1234",
        name: "Aberrus Mythic",
        backgroundUrl: "",
        roster: 20,
        accepted: 19,
        date: new Date()
    },
    {
        id: "1234",
        name: "Social Raid - Aberrus Heroic",
        backgroundUrl: "",
        roster: 30,
        accepted: 19,
        date: new Date()
    }]
}