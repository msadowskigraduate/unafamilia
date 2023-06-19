import { User } from "next-auth";

export type SafeUser = Omit<User, "id" | "name"> & {
    name: string
}