import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../../../context/AuthContext';

export default function CustomerExtraDetails() {
    const { state } = useLocation(); 
    const { login } = useAuth();
    const navigate = useNavigate();
    const [address, setAddress] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        const payload = { ...state, address, role: 'CUSTOMER' };
        
        try {
            const res = await axios.post('http://localhost:8080/api/auth/register-oauth-final', payload);
            
            if (res.data.success) {
                const { accessToken, user } = res.data.data; 
                login(user, accessToken);
                alert("Customer Profile completed! Welcome to PALUTO.");
                navigate('/customer');
            }
        } catch (err) { 
            alert("Registration failed: " + (err.response?.data?.message || "Check connection")); 
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.whiteBox}>
                <h1 style={styles.title}>Almost there!</h1>
                <p style={styles.subtitle}>Please provide your service address so cooks can find you.</p>
                
                <form onSubmit={handleSubmit}>
                    <div style={styles.field}>
                        <input 
                            style={styles.input} 
                            placeholder="Enter your full address (House No., Street, City)"
                            value={address} 
                            onChange={e => setAddress(e.target.value)} 
                            disabled={isLoading}
                            required 
                        />
                    </div>
                    
                    <button 
                        type="submit" 
                        disabled={isLoading} 
                        style={isLoading ? styles.btnDisabled : styles.btn}
                    >
                        {isLoading ? "Saving..." : "Complete Registration"}
                    </button>
                </form>
            </div>
        </div>
    );
}

const styles = {
    container: { 
        height: '100vh', 
        width: '100vw', 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center',
        margin: 0,
        fontFamily: 'Arial, sans-serif'
    },
    whiteBox: {
        backgroundColor: '#fff',
        padding: '60px',
        borderRadius: '20px',
        textAlign: 'center',
        width: '100%',
        maxWidth: '500px',
        border: '1px solid #ddd'
    },
    title: { 
        fontSize: '40px', 
        fontWeight: '900', 
        color: '#0A0A1F', 
        marginBottom: '8px',
    },
    subtitle: {
        fontSize: '16px',
        color: '#888',
        marginBottom: '40px',
        fontWeight: '500'
    },
    field: { 
        marginBottom: '20px', 
        textAlign: 'left' 
    },
    input: { 
        width: '100%', 
        padding: '14px', 
        borderRadius: '12px', 
        border: '1.5px solid #b4afaf', 
        boxSizing: 'border-box', 
        outline: 'none'
    },
    btn: { 
        width: '100%', 
        padding: '16px', 
        backgroundColor: '#06032c', 
        color: '#fff', 
        border: 'none', 
        borderRadius: '12px', 
        cursor: 'pointer', 
        fontSize: '16px', 
        fontWeight: 'bold' 
    },
    btnDisabled: { 
        width: '100%', 
        padding: '16px', 
        backgroundColor: '#ccc', 
        color: '#fff', 
        border: 'none', 
        borderRadius: '12px', 
        cursor: 'not-allowed' 
    }
};