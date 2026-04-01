import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';  // ← add useLocation
import { useAuth } from '../context/AuthContext';

const NAV_LINKS = {
    CUSTOMER: [
        { label: 'Home', to: '/customer', style: 'orangeBadge' },
        { label: 'Cooks', to: '/customer/cooks', style: 'link' },
        { label: 'Bookings', to: '/customer/bookings', style: 'link' },
    ],
    COOK: [
        { label: 'Dashboard', to: '/cook', style: 'redBadge' },
        { label: 'Bookings', to: '/cook/bookings', style: 'link' },
        { label: 'Portfolio', to: '/cook/portfolio', style: 'link' },
    ],
    ADMIN: [
        { label: 'Dashboard', to: '/admin', style: 'orangeBadge' },
        { label: 'Users', to: '/admin/users', style: 'link' },
    ],
};

// which badge style to use per role
const ACTIVE_BADGE = {
    CUSTOMER: 'orangeBadge',
    COOK: 'redBadge',
    ADMIN: 'orangeBadge',
};

export default function PrivateNavbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();  // ← gets current path

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const links = NAV_LINKS[user?.role] ?? [];
    const activeBadge = ACTIVE_BADGE[user?.role] ?? 'orangeBadge';
    const home = `/${user?.role?.toLowerCase()}`;

    return (
        <nav style={navStyles.navbar}>
            <div style={navStyles.leftSection}>
                <div style={navStyles.logoContainer} onClick={() => navigate(home)}>
                    PALUTO
                </div>
                <div style={navStyles.navLinks}>
                    {links.map(({ label, to }) => {
                        const isActive = location.pathname === to;  // ← check if current path
                        return (
                            <span
                                key={to}
                                style={isActive ? navStyles[activeBadge] : navStyles.link}  // ← active = badge, inactive = link
                                onClick={() => navigate(to)}
                            >
                                {label}
                            </span>
                        );
                    })}
                </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <span style={navStyles.welcomeTxt}>Hi, {user?.firstName}!</span>
                <button style={navStyles.lightBtn} onClick={handleLogout}>Logout</button>
            </div>
        </nav>
    );
}
const navStyles = {
    navbar: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '15px 50px', backgroundColor: '#fff', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' },
    leftSection: { display: 'flex', alignItems: 'center', gap: '0px' },  // ← add this
    logoContainer: { fontSize: '32px', fontWeight: '900', cursor: 'pointer' },  // ← removed flex
    navLinks: { display: 'flex', gap: '25px', alignItems: 'center', fontSize: '16px', fontWeight: '300', marginLeft: '50px' },
    link: { color: '#666', cursor: 'pointer' },
    orangeBadge: { backgroundColor: '#FF8A00', color: '#fff', padding: '5px 20px', borderRadius: '15px', border: 'none', cursor: 'pointer', display: 'inline-flex',
    alignItems: 'center',},
    redBadge:    { backgroundColor: '#E53935', color: '#fff', padding: '5px 20px', borderRadius: '15px', border: 'none', cursor: 'pointer', display: 'inline-flex',
    alignItems: 'center', },
    lightBtn: { backgroundColor: '#f0f0f0', border: 'none', padding: '8px 15px', borderRadius: '10px', cursor: 'pointer' },
    welcomeTxt: { fontSize: '14px', fontWeight: '600' }
};