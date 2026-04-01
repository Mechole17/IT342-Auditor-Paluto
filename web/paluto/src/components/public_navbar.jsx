import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';  // ← add useLocation
import LoginModal from '../modals/loginmodal.jsx';

const NAV_LINKS = [
    { label: 'Home', to: '/' },
    { label: 'Cooks', to: '/cooks' },
];

export default function PublicNavbar() {
    const navigate = useNavigate();
    const location = useLocation();  // ← add this
    const [isLoginOpen, setIsLoginOpen] = useState(false);

    return (
        <>
            <nav style={navStyles.navbar}>
                <div style={navStyles.leftSection}>
                    <div style={navStyles.logoContainer} onClick={() => navigate('/')}>
                        PALUTO
                    </div>
                    <div style={navStyles.navLinks}>
                        {NAV_LINKS.map(({ label, to }) => {
                            const isActive = location.pathname === to;
                            return (
                                <span
                                    key={to}
                                    style={isActive ? navStyles.orangeBadge : navStyles.link}
                                    onClick={() => navigate(to)}
                                >
                                    {label}
                                </span>
                            );
                        })}
                    </div>
                </div>

                <button style={navStyles.navyBtn} onClick={() => setIsLoginOpen(true)}>
                    sign in
                </button>
            </nav>

            {isLoginOpen && <LoginModal onClose={() => setIsLoginOpen(false)} />}
        </>
    );
}

const navStyles = {
    navbar: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '15px 50px', backgroundColor: '#fff', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' },
    leftSection: { display: 'flex', alignItems: 'center', gap: '0px' },
    logoContainer: { fontSize: '32px', fontWeight: '900', cursor: 'pointer' },
    navLinks: { display: 'flex', gap: '25px', alignItems: 'center', fontSize: '16px', fontWeight: '300', marginLeft: '50px' },
    link: { color: '#666', cursor: 'pointer' },
    orangeBadge: { backgroundColor: '#FF8A00', color: '#fff', padding: '5px 20px', borderRadius: '15px', border: 'none', cursor: 'pointer', display: 'inline-flex',
    alignItems: 'center', },
    navyBtn: { backgroundColor: '#0A0A1F', color: '#fff', padding: '10px 35px', borderRadius: '20px', border: 'none', cursor: 'pointer' },
};