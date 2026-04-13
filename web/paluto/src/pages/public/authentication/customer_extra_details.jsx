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
            
            // Debugging tip: Always log the response during development
            console.log("Full Response:", res.data);

            // Your ResponseUtility sends a field called 'success'
            if (res.data.success) {
                // Because of ApiResponse.java, the payload is in res.data.data
                const { accessToken, user } = res.data.data; 
                
                login(user, accessToken);
                alert("Profile completed! Welcome to PALUTO.");
                navigate('/customer');
            }
        } catch (err) { 
            console.error("Error details:", err.response?.data);
            alert("Registration failed: " + (err.response?.data?.error?.message || "Check connection")); 
        } finally {
            setIsLoading(false);
        }
    };
    return (
        <div style={styles.overlay}>
            <div style={styles.card}>
                <h2 style={styles.title}>Almost there!</h2>
                <p style={styles.subtitle}>Please provide your service address so cooks can find you.</p>
                
                <form onSubmit={handleSubmit}>
                    <div style={styles.inputGroup}>
                        <label style={styles.label}>Service Address</label>
                        <textarea 
                            style={styles.textarea} 
                            placeholder="Enter your full address (House No., Street, Brgy, City)"
                            value={address} 
                            onChange={e => setAddress(e.target.value)} 
                            required 
                        />
                    </div>
                    
                    <button type="submit" disabled={isLoading} style={styles.submitBtn}>
                        {isLoading ? "Saving..." : "Complete Registration"}
                    </button>
                </form>
            </div>
        </div>
    );
}

const styles = {
    overlay: { 
        minHeight: '100vh', 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        background: '#f4f4f9',
        padding: '20px'
    },
    card: { 
        background: '#fff', 
        padding: '40px', 
        borderRadius: '30px', 
        width: '100%',
        maxWidth: '450px', 
        textAlign: 'center', 
        boxShadow: '0 10px 25px rgba(0,0,0,0.1)' 
    },
    title: { fontSize: '28px', marginBottom: '10px', color: '#0A0A1F' },
    subtitle: { color: '#666', marginBottom: '30px', fontSize: '14px' },
    inputGroup: { textAlign: 'left', marginBottom: '20px' },
    label: { display: 'block', marginBottom: '8px', fontWeight: 'bold', fontSize: '14px' },
    textarea: { 
        width: '100%', 
        padding: '15px', 
        borderRadius: '10px', 
        border: '1px solid #ccc', 
        minHeight: '100px',
        boxSizing: 'border-box',
        fontFamily: 'inherit'
    },
    submitBtn: { 
        backgroundColor: '#0A0A1F', 
        color: '#fff', 
        width: '100%', 
        padding: '15px', 
        borderRadius: '20px', 
        border: 'none', 
        cursor: 'pointer',
        fontSize: '16px',
        fontWeight: 'bold',
        transition: '0.3s'
    }
};