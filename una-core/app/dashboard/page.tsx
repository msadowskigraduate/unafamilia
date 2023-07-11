import Navbar from "@/components/navbar/Navbar";
import Container from "@/components/Container";
import getCurrentUser from "../actions/getCurrentUser";
import { Suspense } from "react";
import Loading from "./events/loading";
import Events from "./events/page";

export default async function DashboardHome() {
    const currentUser = await getCurrentUser();

    return (
        <>
            <Navbar currentUser={currentUser} />
            <Container>
                <div
                    className="
                        flex
                        flex-col
                    ">
                    <Suspense fallback={<Loading />}>
                        <Events />
                    </Suspense>     
                </div>
            </Container>
        </>
    );
}