import Navbar from "@/components/navbar/Navbar";
import Container from "@/components/Container";
import getCurrentUser from "../actions/getCurrentUser";

export default async function DashboardHome() {
    const currentUser = await getCurrentUser();

    return (
        <>
            <Navbar currentUser={currentUser} />
            <Container><div>Dupa</div></Container>
        </>
    );
}