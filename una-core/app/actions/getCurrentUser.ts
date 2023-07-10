import { getServerSession } from "next-auth/next"
import { authOptions } from "../api/auth/[...nextauth]/options";


export async function getSession() {
    return await getServerSession(authOptions)
}

export default async function getCurrentUser() {
    try {
        const session = await getSession();

        if (!session?.user?.name) {
          return null;
        }

        return {
          name: session.user.name
        }

      } catch (error: any) {
        return null;
      }
}