export const metadata = {
    title: "Una Familia - Welcome shit nugget!",
    description: "Mythic Raiding PvE Guild",
};

export default async function DashboardLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return (
        <div className="
          flex
          flex-row
          sm:flex-col
          sm:gap-0
          h-screen
          w-screen
        ">
            {children}
        </div>
    );
}