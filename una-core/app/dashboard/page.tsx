import Navbar from "@/components/navbar/Navbar";
import Container from "@/components/Container";
import getCurrentUser from "../actions/getCurrentUser";
import { Suspense } from "react";
import Loading from "./events/loading";
import Events from "./events/page";
import { redirect } from "next/navigation";

export default async function DashboardHome() {
    const currentUser = await getCurrentUser();

    if(currentUser === null) redirect('/login');

    return (
        <>
            <Navbar currentUser={currentUser} />
            <Container>
                <div
                    className="
                        grid
                        grid-rows-3
                        grid-flow-col
                        h-screen
                    ">
                    <div>
                        <Suspense fallback={<Loading />}>
                            <Events />
                        </Suspense>
                    </div>

                    <div>Dupa</div>
                    <div>Dupa 2</div>
                </div>
            </Container>
        </>
    );
}