import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const CookRegister = () => {
    const navigate = useNavigate();
    const [step, setStep] = useState(1);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    
    // State matching your CookRegistrationRequest DTO
    const [formData, setFormData] = useState({
        firstname: '',
        lastname: '',
        address: '',
        email: '',
        password: '',
        confirmPassword: '',
        // Cook specific fields
        hourlyRate: '',
        yearsExperience: '',
        bio: ''
    });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // Logical Validation for Step 1
    const handleNext = () => {
        if (!formData.email || !formData.password || !formData.firstname) {
            setError("Please fill in all required fields.");
            return;
        }
        if (formData.password !== formData.confirmPassword) {
            setError("Passwords do not match.");
            return;
        }
        setError(null);
        setStep(2);
    };

    // Final Submission to Spring Boot
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            // Note: We send the whole object, Spring Boot DTO will ignore 'confirmPassword'
            const response = await axios.post('http://localhost:8080/api/cook/register', formData);
            
            if (response.data.success) {
                alert("Registration Successful! Redirecting to login...");
                navigate('/'); // Redirect to landing to trigger the Login Modal
            }
        } catch (err) {
            setError(err.response?.data?.error?.message || "Server Error: Could not register cook.");
        } finally {
            setLoading(false);
        }
    };

    // --- Dynamic Inline Styles ---
    const styles = {
        container: { display: 'flex', height: '100vh', width: '100vw', fontFamily: 'Arial, sans-serif' },
        leftSide: { flex: 1, backgroundColor: '#d10b04', display: 'flex', flexDirection: 'column', padding: '40px', position: 'relative' },
        logo: { fontSize: '32px', fontWeight: '900', color: '#000', marginBottom: '50px' },
        foodImg: { width: '85%', margin: 'auto', borderRadius: '50%', boxShadow: '0 20px 40px rgba(0,0,0,0.2)' },
        rightSide: { flex: 1, backgroundColor: '#fff', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '0 80px' },
        input: { width: '100%', padding: '16px', margin: '10px 0', borderRadius: '12px', border: '1.5px solid #000', fontSize: '16px', outline: 'none' },
        button: { backgroundColor: '#0A0A1F', color: '#fff', padding: '16px 80px', borderRadius: '15px', fontWeight: 'bold', fontSize: '18px', border: 'none', alignSelf: 'flex-end', marginTop: '40px', cursor: 'pointer', opacity: loading ? 0.7 : 1 },
        errorMsg: { color: 'red', fontWeight: 'bold', marginBottom: '10px' }
    };

    return (
        <div style={styles.container}>
            <div style={styles.leftSide}>
                <div style={styles.logo}>PALUTO</div>
                {/* Image should represent a professional cook or kitchen */}
                <img src="/cook-avatar-round.png" alt="Cook Registration" style={styles.foodImg} />
            </div>

            <div style={styles.rightSide}>
                <h1 style={{fontSize: '42px', fontWeight: 'bold'}}>{step === 1 ? "Join us!" : "Profile Details"}</h1>
                <p style={{color: '#666', marginBottom: '20px'}}>Step {step} of 2</p>

                {error && <div style={styles.errorMsg}>{error}</div>}
                
                <form onSubmit={step === 2 ? handleSubmit : (e) => e.preventDefault()}>
                    {step === 1 ? (
                        /* STEP 1: ACCOUNT DATA */
                        <>
                            <div style={{ display: 'flex', gap: '20px' }}>
                                <input name="firstname" placeholder="first name" style={styles.input} onChange={handleChange} required />
                                <input name="lastname" placeholder="last name" style={styles.input} onChange={handleChange} required />
                            </div>
                            <input name="address" placeholder="home address" style={styles.input} onChange={handleChange} required />
                            <input name="email" type="email" placeholder="email" style={styles.input} onChange={handleChange} required />
                            <input name="password" type="password" placeholder="password" style={styles.input} onChange={handleChange} required />
                            <input name="confirmPassword" type="password" placeholder="confirm password" style={styles.input} onChange={handleChange} required />
                            
                            <button type="button" style={styles.button} onClick={handleNext}>next</button>
                        </>
                    ) : (
                        /* STEP 2: PROFESSIONAL DATA */
                        <>
                            <input name="hourlyRate" type="number" placeholder="hourly rate (₱)" style={styles.input} onChange={handleChange} required />
                            <input name="yearsExperience" type="number" placeholder="years of experience" style={styles.input} onChange={handleChange} required />
                            <textarea 
                                name="bio" 
                                placeholder="tell us about your cooking style (bio)" 
                                style={{ ...styles.input, height: '120px', resize: 'none' }} 
                                onChange={handleChange} 
                                required
                            />
                            
                            <button type="submit" style={styles.button} disabled={loading}>
                                {loading ? "Registering..." : "sign in"}
                            </button>
                        </>
                    )}
                </form>
            </div>
        </div>
    );
};

export default CookRegister;