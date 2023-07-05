import Login from "@/components/login/Login";
import Navbar from "@/components/navbar/Navbar";
import getCurrentUser from "./actions/getCurrentUser";
import Container from "@/components/Container";

export default async function Home() {
  const currentUser = await getCurrentUser();

  return (
    <div>
      
      {currentUser == null && (
        <div
          className="
            flex
            items-center
            justify-center
            h-screen
            w-screen
          "
        >
          <Login />
        </div>
      )}

      {currentUser != null && (
        <div className="
          flex
          flex-row
          sm:flex-col
          sm:gap-0
          h-screen
          w-screen
        ">     
          <Navbar currentUser={currentUser} />
          <Container><div>Dupa</div></Container>
        </div>
      )}
    </div>
  );
}
