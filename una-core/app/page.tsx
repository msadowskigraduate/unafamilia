import Login from "@/components/login/Login";
import Navbar from "@/components/navbar/Navbar";
import getCurrentUser from "./actions/getCurrentUser";

export default async function Home() {

  const currentUser = await getCurrentUser();

  return (
    <div>
      <Navbar currentUser={currentUser} />

      <div className="
            flex
            items-center
            justify-center
            h-screen
            w-screen
            bg-[url('https://wow.zamimg.com/uploads/screenshots/normal/1074416.jpg')]
            bg-center
          "
      >
        {currentUser == null && (
          <Login />
        )}

      </div>

    </div>
  );
}