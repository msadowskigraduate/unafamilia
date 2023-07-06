import Login from "@/components/login/Login";

export const metadata = {
    title: "Una Familia - Login",
    description: "Mythic Raiding PvE Guild",
};

export default async function LoginLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return (
        <div
            className="
                flex
                items-center
                justify-center
                h-screen
                w-screen
                "
        >
            {children}
        </div>
    );
}