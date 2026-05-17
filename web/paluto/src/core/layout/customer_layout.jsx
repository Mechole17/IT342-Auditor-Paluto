import { Outlet } from "react-router-dom";
import PrivateNavbar from "../components/private_navbar";

export default function CustomerLayout() {
    return (
        <div style={styles.layout}>
            <PrivateNavbar />
            <main style={styles.main}>
                <Outlet />
            </main>
        </div>
    );
}

const styles = {
    layout: {
        display: 'flex',
        flexDirection: 'column',
        height: '100vh',
        overflow: 'hidden',
    },
    main: {
        flex: 1,
        overflowY: 'auto',
    },
};