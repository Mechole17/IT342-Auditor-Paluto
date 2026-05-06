import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Ramen from '../../asset/ramen.png';
import axios from 'axios';
import { useAuth } from '../../context/AuthContext';

export default function CookRegister() {
    const {login} = useAuth();
    const navigate = useNavigate();
    const [step, setStep] = useState(1);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    
    const [formData, setFormData] = useState({
        firstname: '',
        lastname: '',
        address: '',
        email: '',
        password: '',
        confirmPassword: '',
        hourly_rate: '',
        years_xp: '',
        bio: ''
    });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // --- ARCHITECT'S VALIDATION LOGIC ---
    const validateStepOne = () => {
        const { email, password, confirmPassword, firstname, lastname, address } = formData;
        
        if (!email || !password || !firstname || !lastname || !address) {
            return "Please fill in all required fields.";
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) return "Please enter a valid email address.";

        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        if (!passwordRegex.test(password)) {
            return "Password must be at least 8 characters with uppercase, lowercase, number, and special character.";
        }

        if (password !== confirmPassword) {
            return "Passwords do not match.";
        }
        
        return null;
    };

    const handleNext = () => {
        const validationError = validateStepOne();
        
        if (validationError) {
            // Clear passwords HERE, not inside validateStepOne
            setFormData(prev => ({ ...prev, password: '', confirmPassword: '' }));
            setError(validationError);
            return;
        }

        setError(null);
        setStep(2);
    };

    const handleSubmit = async (e) => {
        
        // Final numeric check (Min 0)
        if (formData.hourly_rate < 0 || formData.years_xp < 0) {
            setError("Hourly rate and experience cannot be negative.");
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const response = await axios.post('http://localhost:8080/api/cook/register', formData);
            if (response.data.success) {
                // 1. Extract the data (token and user object)
                const { accessToken, user } = response.data.data;

                // 2. Trigger the Global Login (this saves to localStorage & state)
                login(user, accessToken);

                // 3. Redirect directly to their dashboard (Role-Aware)
                if (user.role === 'COOK') navigate('/cook');
                console.log(response);
                alert("Account created successfully! Welcome to PALUTO! You are now logged in as a cook.");
            }
        } catch (err) {
            if (err.response && err.response.data) {
                const apiResponse = err.response.data;

                // 2. Access the nested message: apiResponse.error.message
                // This will grab "The email is already in use" from your service
                const errorMessage = apiResponse.error?.message || "Registration failed.";
                
                setError(errorMessage);
                setStep(1); // Go back to step 1 to fix issues
                // Architect's Tip: Clear sensitive fields on error, but keep the email 
                // so the user can see what they typed wrong.
                setFormData(prev => ({
                    ...prev,
                    password: '',
                    confirmPassword: ''
                }));
                
            } else {
                // 3. Fallback for network issues (server down, etc.)
                setError("Unable to connect to the server.");
            }
        } finally {
            setLoading(false);
        }
    };

    const styles = {
        container: { display: 'flex', height: '100vh', width: '100vw', fontFamily: 'Arial, sans-serif' },
        leftSide: { flex: 1, backgroundColor: '#d10b04', display: 'flex', flexDirection: 'column', padding: '40px', position: 'relative' },
        logo: { fontSize: '32px', fontWeight: '900', color: '#000', marginBottom: '50px', cursor: 'pointer' },
        foodImg: { width: '85%', margin: 'auto' },
        rightSide: { flex: 1, backgroundColor: '#fff', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '0 80px' },
        input: { width: '100%', padding: '12px', margin: '8px 0', borderRadius: '12px', border: '1.5px solid #7b7a7a', fontSize: '15px', outline: 'none', boxSizing: 'border-box' },
        btnContainer: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '30px' },
        button: { backgroundColor: '#0A0A1F', color: '#fff', padding: '16px 50px', borderRadius: '15px', fontWeight: 'bold', fontSize: '18px', border: 'none', cursor: 'pointer' },
        backLink: { color: '#666', cursor: 'pointer', fontWeight: 'bold', textDecoration: 'underline' },
        footerLink: { marginTop: '20px', fontSize: '14px', color: '#333', cursor: 'pointer', textAlign: 'center' },
        errorMsg: { color: '#d10b04', fontWeight: 'bold', marginBottom: '10px', fontSize: '14px' }
    };

    return (
        <div style={styles.container}>
            <div style={styles.leftSide}>
                <div style={styles.logo} onClick={() => navigate('/')}>PALUTO</div>
                <img src={Ramen} alt="Cook Registration" style={styles.foodImg} />
            </div>

            <div style={styles.rightSide}>
                <h1 style={{fontSize: '42px', fontWeight: 'bold'}}>{step === 1 ? "Join as a Cook" : "Profile Details"}</h1>
                <p style={{color: '#666', marginBottom: '20px'}}>Step {step} of 2</p>

                {error && <div style={styles.errorMsg}>{error}</div>}
                
                <form onSubmit={(e) => e.preventDefault()}>
                    {step === 1 ? (
                        <>
                            <div style={{ display: 'flex', gap: '15px' }}>
                                <input name="firstname" placeholder="first name" style={styles.input} onChange={handleChange} value={formData.firstname} required />
                                <input name="lastname" placeholder="last name" style={styles.input} onChange={handleChange} value={formData.lastname} required />
                            </div>
                            <input name="address" placeholder="home address" style={styles.input} onChange={handleChange} value={formData.address} required />
                            <input name="email" type="email" placeholder="email" style={styles.input} onChange={handleChange} value={formData.email} required />
                            <input name="password" type="password" placeholder="password" style={styles.input} onChange={handleChange} value={formData.password} required />
                            <input name="confirmPassword" type="password" placeholder="confirm password" style={styles.input} onChange={handleChange} value={formData.confirmPassword} required />
                            
                            <div style={styles.btnContainer}>
                                <span style={styles.backLink} onClick={() => navigate('/')}>Cancel</span>
                                <button type="button" style={styles.button} onClick={handleNext}>Next</button>
                            </div>
                        </>
                    ) : (
                        <>
                            <label style={{fontSize: '14px', color: '#666'}}>Hourly Rate (₱)</label>
                            <input name="hourly_rate" type="number" min="0" placeholder="0.00" style={styles.input} onChange={handleChange} value={formData.hourly_rate} required />
                            
                            <label style={{fontSize: '14px', color: '#666'}}>Years of Experience</label>
                            <input name="years_xp" type="number" min="0" placeholder="0" style={styles.input} onChange={handleChange} value={formData.years_xp} required />
                            
                            <textarea 
                                name="bio" 
                                placeholder="Tell us about your culinary expertise..." 
                                style={{ ...styles.input, height: '100px', resize: 'none' }} 
                                onChange={handleChange} 
                                value={formData.bio}
                                required
                            />
                            
                            <div style={styles.btnContainer}>
                                <span style={styles.backLink} onClick={() => setStep(1)}>Back</span>
                                <button type="button" style={{...styles.button, opacity: loading ? 0.7 : 1}} onClick={handleSubmit} disabled={loading}>
                                    {loading ? "Signing up..." : "Complete"}
                                </button>
                            </div>
                        </>
                    )}
                </form>
            </div>
        </div>
    );
}