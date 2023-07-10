import getCurrentUser from "./actions/getCurrentUser";
import { redirect } from "next/navigation";

export default async function Home() {
  const currentUser = await getCurrentUser();

  if(currentUser == null) redirect("/login")

  redirect("/dashboard")
  return (
    <div>
      Ooops!
    </div>
  );
}
