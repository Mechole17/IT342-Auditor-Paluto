import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NAV_LINKS = {
    CUSTOMER: [
        { label: 'Home', to: '/customer/home', style: 'orangeBadge' },
        { label: 'Cooks', to: '/cooks', style: 'link' },
        { label: 'Bookings', to: '/bookings', style: 'link' },
    ],
    COOK: [
        { label: 'Dashboard', to: '/cook', style: 'redBadge' },
        { label: 'Orders', to: '/cook/orders', style: 'link' },
        { label: 'Menu', to: '/cook/menu', style: 'link' },
    ],
    ADMIN: [
        { label: 'Dashboard', to: '/admin', style: 'orangeBadge' },
        { label: 'Users', to: '/admin/users', style: 'link' },
    ],
};

export default function PrivateNavbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const links = NAV_LINKS[user?.role] ?? [];

    return (
        <nav style={navStyles.navbar}>
            <div style={navStyles.logoContainer} onClick={() => navigate('/')}>
                PALUTO
                <div style={navStyles.navLinks}>
                    {links.map(({ label, to, style }) => (
                        <span
                            key={to}
                            style={navStyles[style]}
                            onClick={() => navigate(to)}
                        >
                            {label}
                        </span>
                    ))}
                </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <span style={navStyles.welcomeTxt}>Hi, {user?.firstname}!</span>
                <button style={navStyles.lightBtn} onClick={handleLogout}>Logout</button>
            </div>
        </nav>
    );
}

const navStyles = {
    navbar: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '15px 50px', backgroundColor: '#fff', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' },
    logoContainer: { fontSize: '32px', fontWeight: '900', display: 'flex', alignItems: 'center', cursor: 'pointer' },
    navLinks: { display: 'flex', gap: '25px', alignItems: 'center', fontSize: '16px', fontWeight: '300', marginLeft: '50px' },
    link: { color: '#666', cursor: 'pointer' },
    orangeBadge: { backgroundColor: '#FF8A00', color: '#fff', padding: '5px 20px', borderRadius: '15px', border: 'none', cursor: 'pointer' },
    redBadge:    { backgroundColor: '#E53935', color: '#fff', padding: '5px 20px', borderRadius: '15px', border: 'none', cursor: 'pointer' },
    lightBtn: { backgroundColor: '#f0f0f0', border: 'none', padding: '8px 15px', borderRadius: '10px', cursor: 'pointer' },
    welcomeTxt: { fontSize: '14px', fontWeight: '600' }
};