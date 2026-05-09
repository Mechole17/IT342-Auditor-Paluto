import { Outlet } from "react-router-dom";
import PublicNavbar from "../components/public_navbar";

export default function PublicLayout() {
    return (
        <div>
            <PublicNavbar />
            <main>
                <Outlet />
            </main>
        </div>
    );
}