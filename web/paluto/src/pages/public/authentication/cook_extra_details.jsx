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

    const handleSubmit = async (e) => {
        e.preventDefault();
        const payload = { ...state, ...formData, role: 'COOK' };

        try {
            const res = await axios.post('http://localhost:8080/api/auth/register-oauth-final', payload);
            if (res.data.success) {
                login(res.data.data.user, res.data.data.accessToken);
                navigate('/cook');
            }
        } catch (err) { alert("Error saving profile"); }
    };

    return (
        <div style={styles.formBox}>
            <h2>Professional Cook Details</h2>
            <form onSubmit={handleSubmit}>
                <input style={styles.input} placeholder="Full Address" onChange={e => setFormData({...formData, address: e.target.value})} required />
                <input style={styles.input} type="number" placeholder="Hourly Rate (PHP)" onChange={e => setFormData({...formData, hourly_rate: e.target.value})} required />
                <input style={styles.input} type="number" placeholder="Years of Experience" onChange={e => setFormData({...formData, years_xp: e.target.value})} required />
                <textarea style={styles.input} placeholder="Your Culinary Bio" onChange={e => setFormData({...formData, bio: e.target.value})} required />
                <button type="submit" style={styles.btn}>Activate Cook Account</button>
            </form>
        </div>
    );
}

const styles = {
    formBox: { maxWidth: '500px', margin: '50px auto', padding: '30px', border: '1px solid #ddd', borderRadius: '15px' },
    input: { width: '100%', padding: '12px', margin: '10px 0', borderRadius: '8px', border: '1px solid #ccc' },
    btn: { width: '100%', padding: '15px', backgroundColor: '#0A0A1F', color: '#fff', border: 'none', borderRadius: '10px', cursor: 'pointer' }
};