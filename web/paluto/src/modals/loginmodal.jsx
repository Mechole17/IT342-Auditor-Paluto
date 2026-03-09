// src/components/LoginModal.jsx
import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import GoogleLogo from '../asset/google-con.jpg';

export default function LoginModal({ onClose }) {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', { email, password });
            if (response.data.success) {
                const { accessToken, user } = response.data.data;
                login(user, accessToken); // Updates global state
                onClose();
                if (user.role === 'CUSTOMER') {
                    alert("Customer Login Successful! Redirecting...");
                }
                else if (user.role === 'COOK') {
                    alert("Cook Login Successful! Redirecting...");
                }
                else if (user.role === 'ADMIN') {
                    alert("Admin Login Successful! Redirecting...");
                }
               
            }
        } catch (err) {
            setError(err.response?.data?.error?.message || "Invalid credentials.");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div style={modalStyles.overlay} onClick={onClose}>
            <div style={modalStyles.modal} onClick={e => e.stopPropagation()}>
                <button style={modalStyles.closeBtn} onClick={onClose}>✕</button>
                <h2 style={{fontSize: '28px', marginBottom: '30px'}}>Welcome Back!</h2>
                {error && <div style={modalStyles.errorTxt}>{error}</div>}
                <form onSubmit={handleLogin}>
                    <input type="email" placeholder="email" style={modalStyles.input} value={email} onChange={(e) => setEmail(e.target.value)} required />
                    <input type="password" placeholder="password" style={modalStyles.input} value={password} onChange={(e) => setPassword(e.target.value)} required />
                    <button type="submit" disabled={isLoading} style={modalStyles.submitBtn}>
                        {isLoading ? "Checking..." : "sign in"}
                    </button>
                </form>
                <div style={{margin: '20px 0'}}>or sign in with</div>
                <img src={GoogleLogo} alt="google" style={{width:'40px', cursor:'pointer'}} />
            </div>
        </div>
    );
}

const modalStyles = {
    overlay: { position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 },
    modal: { background: '#fff', padding: '40px', borderRadius: '30px', width: '450px', textAlign: 'center', position: 'relative' },
    closeBtn: { position:'absolute', right: '20px', top: '20px', border:'none', background:'none', fontSize:'20px', cursor:'pointer' },
    input: { width: '90%', padding: '15px', margin: '10px 0', borderRadius: '10px', border: '1px solid #ccc' },
    errorTxt: { color: '#E60000', marginBottom: '10px' },
    submitBtn: { backgroundColor: '#0A0A1F', color: '#fff', width: '100%', padding: '15px', borderRadius: '20px', border: 'none', marginTop: '10px', cursor: 'pointer' }
};