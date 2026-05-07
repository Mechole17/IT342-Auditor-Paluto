import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../../context/AuthContext';
import axios from 'axios';

export default function OAuthSuccess() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { login } = useAuth();

    useEffect(() => {
        const token = searchParams.get('token');

        if (token) {
            const fetchUserAndLogin = async () => {
                try {
                    const res = await axios.get('http://localhost:8080/api/auth/me', {
                        headers: { Authorization: `Bearer ${token}` }
                    });

                    const userData = res.data.data.user;
                    login(userData, token);

                    if (userData.role === 'CUSTOMER') {
                        navigate('/customer');
                    } else if (userData.role === 'COOK') {
                        navigate('/cook');
                    } else {
                        navigate('/');
                    }
                } catch (err) {
                    console.error("OAuth Login Error:", err);
                    navigate('/');
                }
            };

            // Adding a tiny delay (e.g., 800ms) can sometimes feel "smoother" 
            // than a flash of a spinner, but for now, we execute immediately.
            fetchUserAndLogin();
        } else {
            navigate('/');
        }
    }, [searchParams, login, navigate]);

    return (
        <div style={styles.container}>
            <div style={styles.spinner}></div>
            <h2 style={styles.text}>Authenticating...</h2>
            <p style={styles.subtext}>Securely connecting to your PALUTO account</p>
            
            {/* Standard CSS Spinner Animation */}
            <style>
                {`
                    @keyframes spin {
                        0% { transform: rotate(0deg); }
                        100% { transform: rotate(360deg); }
                    }
                `}
            </style>
        </div>
    );
}

const styles = {
    container: {
        display: 'flex',
        flexDirection: 'column',
        height: '100vh',
        justifyContent: 'center',
        alignItems: 'center',
        background: '#f4f4f9'
    },
    spinner: {
        width: '50px',
        height: '50px',
        border: '5px solid #ccc',
        borderTop: '5px solid #0A0A1F', // PALUTO Navy Blue
        borderRadius: '50%',
        animation: 'spin 1s linear infinite',
        marginBottom: '20px'
    },
    text: {
        color: '#0A0A1F',
        margin: '0',
        fontSize: '24px'
    },
    subtext: {
        color: '#666',
        marginTop: '10px'
    }
};