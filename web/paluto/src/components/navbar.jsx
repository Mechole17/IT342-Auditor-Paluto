// src/components/Navbar.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import LoginModal from '../modals/loginmodal.jsx';

export default function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [isLoginOpen, setIsLoginOpen] = useState(false);

    return (
        <>
            <nav style={navStyles.navbar}>
                <div style={navStyles.logoContainer} onClick={() => navigate('/')}>
                    PALUTO
                    <div style={navStyles.navLinks}>
                        <button style={navStyles.orangeBadge} onClick={() => navigate('/')}>Home</button>
                        <span style={navStyles.link} onClick={() => navigate('/cooks')}>Cooks</span>
                        <span style={navStyles.link} onClick={() => navigate('/bookings')}>Bookings</span>
                    </div>
                </div>
                <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                    {!user ? (
                        <button style={navStyles.navyBtn} onClick={() => setIsLoginOpen(true)}>sign in</button>
                    ) : (
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                            <span style={navStyles.welcomeTxt}>Hi, {user.firstName}!</span>
                            <button style={navStyles.lightBtn} onClick={logout}>Logout</button>
                        </div>
                    )}
                </div>
            </nav>

            {isLoginOpen && <LoginModal onClose={() => setIsLoginOpen(false)} />}
        </>
    );
}

const navStyles = {
    navbar: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '15px 50px', backgroundColor: '#fff', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' },
    logoContainer: { fontSize: '32px', fontWeight: '900', display: 'flex', alignItems: 'center', cursor: 'pointer' },
    navLinks: { display: 'flex', gap: '25px', alignItems: 'center', fontSize: '16px', fontWeight: '300', marginLeft: '50px' },
    link: { color: '#666', cursor: 'pointer' },
    orangeBadge: { backgroundColor: '#FF8A00', color: '#fff', padding: '5px 20px', borderRadius: '15px', border: 'none', cursor: 'pointer' },
    navyBtn: { backgroundColor: '#0A0A1F', color: '#fff', padding: '10px 35px', borderRadius: '20px', border: 'none', cursor: 'pointer' },
    lightBtn: { backgroundColor: '#f0f0f0', border: 'none', padding: '8px 15px', borderRadius: '10px', cursor: 'pointer' },
    welcomeTxt: { fontSize: '14px', fontWeight: '600' }
};