import { User } from "next-auth";

export type SafeUser = Omit<User, "id" | "name"> & {
    name: string
}

export type EventData = {
    id: string,
    name: string,
    backgroundUrl: string,
    roster: number,
    accepted: number,
    date: Date
}