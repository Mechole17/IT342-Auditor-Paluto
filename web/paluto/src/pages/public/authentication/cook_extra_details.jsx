import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../../../context/AuthContext';

export default function CookExtraDetails() {
    const { state } = useLocation();
    const { login } = useAuth();
    const navigate = useNavigate();
    
    const [formData, setFormData] = useState({
        address: '',
        hourly_rate: '',
        years_xp: '',
        bio: ''
    });

    const [errors, setErrors] = useState({});
    const [loading, setLoading] = useState(false);

    const validateForm = () => {
        const newErrors = {};
        
        if (!formData.address.trim()) newErrors.address = "Address is required.";
        
        const rate = parseFloat(formData.hourly_rate);
        if (isNaN(rate) || rate <= 0) {
            newErrors.hourly_rate = "Please enter a valid rate greater than 0.";
        }

        const xp = parseInt(formData.years_xp);
        if (isNaN(xp) || xp < 0) {
            newErrors.years_xp = "Experience cannot be negative.";
        }

        if (formData.bio.trim().length < 20) {
            newErrors.bio = "Bio must be at least 20 characters.";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validateForm()) return;

        setLoading(true);
        const payload = { ...state, ...formData, role: 'COOK' };

        try {
            const res = await axios.post('http://localhost:8080/api/auth/register-oauth-final', payload);
            
            // DEBUG: Look at this in your browser console (F12)
            console.log("Full Backend Response:", res.data);

            // Match your ResponseUtility structure
            if (res.data.success || res.data.isSuccess) { 
                // Pull the DTO from the 'data' field of the wrapper
                const { accessToken, user } = res.data.data;
                
                if (accessToken) {
                    alert("Cook Profile completed! Welcome to PALUTO.");
                    login(user, accessToken);
                    navigate('/cook');
                } else {
                    console.error("Token missing inside res.data.data");
                }
            }
        } catch (err) { 
            console.error("Submission error:", err);
            setErrors({ server: err.response?.data?.message || "Error saving profile" });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.formBox}>
            <h2 style={styles.header}>Professional Cook Details</h2>
            <form onSubmit={handleSubmit} noValidate>
                {/* ADDRESS */}
                <div style={styles.field}>
                    <input 
                        style={{...styles.input, borderColor: errors.address ? '#ff4d4f' : '#ccc'}} 
                        placeholder="Full Address" 
                        onChange={e => setFormData({...formData, address: e.target.value})} 
                        disabled={loading}
                        required
                    />
                    {errors.address && <span style={styles.errorText}>{errors.address}</span>}
                </div>

                {/* HOURLY RATE */}
                <div style={styles.field}>
                    <input 
                        style={{...styles.input, borderColor: errors.hourly_rate ? '#ff4d4f' : '#ccc'}} 
                        type="number" 
                        placeholder="Hourly Rate (PHP)" 
                        onChange={e => setFormData({...formData, hourly_rate: e.target.value})} 
                        disabled={loading}
                        required
                    />
                    {errors.hourly_rate && <span style={styles.errorText}>{errors.hourly_rate}</span>}
                </div>

                {/* YEARS XP */}
                <div style={styles.field}>
                    <input 
                        style={{...styles.input, borderColor: errors.years_xp ? '#ff4d4f' : '#ccc'}} 
                        type="number" 
                        placeholder="Years of Experience" 
                        onChange={e => setFormData({...formData, years_xp: e.target.value})} 
                        disabled={loading}
                        required
                    />
                    {errors.years_xp && <span style={styles.errorText}>{errors.years_xp}</span>}
                </div>

                {/* BIO */}
                <div style={styles.field}>
                    <textarea 
                        style={{...styles.input, height: '100px', borderColor: errors.bio ? '#ff4d4f' : '#ccc'}} 
                        placeholder="Your Culinary Bio" 
                        onChange={e => setFormData({...formData, bio: e.target.value})} 
                        disabled={loading}
                        required
                    />
                    {errors.bio && <span style={styles.errorText}>{errors.bio}</span>}
                </div>

                {errors.server && <p style={styles.serverError}>{errors.server}</p>}

                <button 
                    type="submit" 
                    style={loading ? styles.btnDisabled : styles.btn}
                    disabled={loading}
                >
                    {loading ? 'Activating Account...' : 'Activate Cook Account'}
                </button>
            </form>
        </div>
    );
}

const styles = {
    formBox: { maxWidth: '500px', margin: '50px auto', padding: '30px', border: '1px solid #ddd', borderRadius: '15px', backgroundColor: '#fff' },
    header: { color: '#0A0A1F', marginBottom: '20px' },
    field: { marginBottom: '15px', position: 'relative' },
    input: { width: '100%', padding: '12px', borderRadius: '8px', border: '1px solid', boxSizing: 'border-box', outline: 'none' },
    errorText: { color: '#ff4d4f', fontSize: '12px', marginTop: '4px', display: 'block', fontWeight: '500' },
    serverError: { color: '#ff4d4f', textAlign: 'center', backgroundColor: '#fff2f0', padding: '10px', borderRadius: '5px', border: '1px solid #ffccc7' },
    btn: { width: '100%', padding: '15px', backgroundColor: '#0A0A1F', color: '#fff', border: 'none', borderRadius: '10px', cursor: 'pointer', fontSize: '16px', fontWeight: '600', transition: '0.3s' },
    btnDisabled: { width: '100%', padding: '15px', backgroundColor: '#666', color: '#fff', border: 'none', borderRadius: '10px', cursor: 'not-allowed' }
};