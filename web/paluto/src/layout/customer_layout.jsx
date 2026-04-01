import { Outlet } from "react-router-dom";
import PrivateNavbar from "../components/private_navbar";

export default function CustomerLayout() {
    return (
        <div>
            <PrivateNavbar />
            <main>
                <Outlet />
            </main>
        </div>
    );
}