import React, { useState, useRef, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const NAV_LINKS = {
    CUSTOMER: [
        { label: 'Home', to: '/customer' },
        { label: 'Cooks', to: '/customer/cooks' },
        { label: 'Bookings', to: '/customer/bookings' },
    ],
    COOK: [
        { label: 'Dashboard', to: '/cook' },
        { label: 'Bookings', to: '/cook/bookings' },
        { label: 'Portfolio', to: '/cook/portfolio' },
    ],
    ADMIN: [
        { label: 'Dashboard', to: '/admin' },
        { label: 'Users', to: '/admin/users' },
        { label: 'Certificates', to: '/admin/cook-certificates' },
    ],
};

const ACTIVE_BADGE = {
    CUSTOMER: 'orangeBadge',
    COOK: 'redBadge',
    ADMIN: 'blackBadge',
};

export default function PrivateNavbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [showDropdown, setShowDropdown] = useState(false);
    const dropdownRef = useRef(null);

    const handleLogout = () => {
        const confirmLogout = window.confirm('Are you sure you want to logout?');
        if (!confirmLogout) return;
        logout();
        navigate('/');
    };

    // Close dropdown when clicking outside
    useEffect(() => {
        const handleClickOutside = (e) => {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
                setShowDropdown(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const getInitials = () => {
        const first = user?.firstName?.[0] || '';
        const last = user?.lastName?.[0] || '';
        return `${first}${last}`.toUpperCase();
    };

    const getProfilePath = () => {
        const role = user?.role?.toLowerCase();
        return `/${role}/profile`;
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
                        const isActive = location.pathname === to;
                        return (
                            <span
                                key={to}
                                style={isActive ? navStyles[activeBadge] : navStyles.link}
                                onClick={() => navigate(to)}
                            >
                                {label}
                            </span>
                        );
                    })}
                </div>
            </div>

            {/* Avatar with dropdown */}
            <div ref={dropdownRef} style={{ position: 'relative' }}>
                <div
                    style={navStyles.avatar}
                    onClick={() => setShowDropdown(prev => !prev)}
                >
                    {getInitials()}
                </div>

                {showDropdown && (
                    <div style={navStyles.dropdown}>
                        <div style={navStyles.dropdownHeader}>
                            <p style={navStyles.dropdownName}>{user?.firstName} {user?.lastName}</p>
                            <p style={navStyles.dropdownRole}>{user?.role}</p>
                        </div>
                        <div style={navStyles.divider} />
                        <div
                            style={navStyles.dropdownItem}
                            onClick={() => { setShowDropdown(false); navigate(getProfilePath()); }}
                        >
                            Profile
                        </div>
                        <div style={navStyles.divider} />
                        <div
                            style={{ ...navStyles.dropdownItem, color: '#d10b04' }}
                            onClick={handleLogout}
                        >
                            Logout
                        </div>
                    </div>
                )}
            </div>
        </nav>
    );
}

const navStyles = {
    navbar: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '15px 50px', backgroundColor: '#fff', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' },
    leftSection: { display: 'flex', alignItems: 'center' },
    logoContainer: { fontSize: '32px', fontWeight: '900', cursor: 'pointer' },
    navLinks: { display: 'flex', gap: '25px', alignItems: 'center', fontSize: '16px', fontWeight: '300', marginLeft: '50px' },
    link: { color: '#666', cursor: 'pointer' },
    orangeBadge: { backgroundColor: '#FF8A00', color: '#fff', padding: '5px 20px', borderRadius: '15px', border: 'none', cursor: 'pointer', display: 'inline-flex', alignItems: 'center' },
    blackBadge: { backgroundColor: '#000000', color: '#fff', padding: '5px 20px', borderRadius: '15px', border: 'none', cursor: 'pointer', display: 'inline-flex', alignItems: 'center' },
    redBadge: { backgroundColor: '#E53935', color: '#fff', padding: '5px 20px', borderRadius: '15px', border: 'none', cursor: 'pointer', display: 'inline-flex', alignItems: 'center' },
    avatar: {
        width: '40px', height: '40px', borderRadius: '50%',
        backgroundColor: '#0A0A1F', color: '#fff',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        fontWeight: '700', fontSize: '14px', cursor: 'pointer',
        userSelect: 'none'
    },
    dropdown: {
        position: 'absolute', right: 0, top: '50px',
        backgroundColor: '#fff', borderRadius: '12px',
        boxShadow: '0 4px 20px rgba(0,0,0,0.12)',
        minWidth: '200px', zIndex: 1000,
        overflow: 'hidden'
    },
    dropdownHeader: { padding: '14px 16px' },
    dropdownName: { margin: 0, fontWeight: '700', fontSize: '14px' },
    dropdownRole: { margin: '2px 0 0', fontSize: '12px', color: '#888', textTransform: 'uppercase' },
    divider: { height: '1px', backgroundColor: '#eee' },
    dropdownItem: {
        padding: '12px 16px', cursor: 'pointer', fontSize: '14px',
        fontWeight: '600', color: '#333',
        transition: 'background 0.2s'
    },
};