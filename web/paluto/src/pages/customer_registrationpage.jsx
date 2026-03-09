import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios'; // Ensure you've run: npm install axios

const CustomerRegister = () => {
    const navigate = useNavigate();
    
    // 1. State to manage form data
    const [formData, setFormData] = useState({
        firstname: '',
        lastname: '',
        address: '',
        email: '',
        password: '',
        confirmPassword: ''
    });

    // 2. State for feedback (Errors/Loading)
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // 3. Submission Logic
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        // --- Basic Validation ---
        if (formData.password !== formData.confirmPassword) {
            setError("Passwords do not match!");
            return;
        }

        setIsLoading(true);

        try {
            // Match your Spring Boot endpoint
            const response = await axios.post('http://localhost:8080/api/customer/register', {
                firstname: formData.firstname,
                lastname: formData.lastname,
                address: formData.address,
                email: formData.email,
                password: formData.password
            });

            if (response.data.success) {
                alert("Registration Successful!");
                navigate('/'); // Redirect to landing page to sign in
            }
        } catch (err) {
            // Handle Backend Errors (e.g., Email already exists)
            const backendError = err.response?.data?.error?.message || "Registration failed. Try again.";
            setError(backendError);
        } finally {
            setIsLoading(false);
        }
    };

    const styles = {
        container: { display: 'flex', height: '100vh', width: '100vw', fontFamily: 'Arial, sans-serif', overflow: 'hidden' },
        leftSide: { 
            flex: 1, 
            backgroundColor: '#ecb92a', 
            display: 'flex', 
            flexDirection: 'column', 
            padding: '40px',
            position: 'relative'
        },
        logo: { fontSize: '32px', fontWeight: '900', color: '#000', marginBottom: '50px', cursor: 'pointer' },
        foodImg: { 
            width: '85%', 
            margin: 'auto', 
            borderRadius: '50%',
            boxShadow: '0 20px 40px rgba(0,0,0,0.4)' 
        },
        rightSide: { 
            flex: 1, 
            backgroundColor: '#fff', 
            display: 'flex', 
            flexDirection: 'column', 
            justifyContent: 'center', 
            padding: '0 80px' 
        },
        title: { fontSize: '42px', fontWeight: 'bold', marginBottom: '10px' },
        errorTxt: { color: '#c40303', fontWeight: 'bold', marginBottom: '15px' },
        inputGroup: { display: 'flex', gap: '20px' },
        input: { 
            width: '100%', 
            padding: '16px', 
            margin: '10px 0', 
            borderRadius: '12px', 
            border: '1.5px solid #000', 
            fontSize: '16px',
            outline: 'none'
        },
        loginLink: { 
            textAlign: 'right', 
            marginTop: '15px', 
            fontSize: '14px', 
            fontWeight: '600', 
            cursor: 'pointer' 
        },
        nextBtn: { 
            backgroundColor: isLoading ? '#555' : '#0A0A1F', 
            color: '#fff', 
            padding: '16px 80px', 
            borderRadius: '15px', 
            fontWeight: 'bold', 
            fontSize: '18px',
            border: 'none',
            alignSelf: 'flex-end', 
            marginTop: '40px', 
            cursor: isLoading ? 'not-allowed' : 'pointer' 
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.leftSide}>
                <div style={styles.logo} onClick={() => navigate('/')}>PALUTO</div>
                <img src="/noodles-bowl.png" alt="Paluto Food" style={styles.foodImg} />
            </div>

            <div style={styles.rightSide}>
                <h1 style={styles.title}>Join Us</h1>
                
                {/* Error Feedback */}
                {error && <div style={styles.errorTxt}>{error}</div>}
                
                <form onSubmit={handleSubmit} style={{display: 'flex', flexDirection: 'column'}}>
                    <div style={styles.inputGroup}>
                        <input name="firstname" type="text" placeholder="first name" style={styles.input} onChange={handleChange} required />
                        <input name="lastname" type="text" placeholder="last name" style={styles.input} onChange={handleChange} required />
                    </div>
                    
                    <input name="address" type="text" placeholder="home address" style={styles.input} onChange={handleChange} required />
                    <input name="email" type="email" placeholder="email" style={styles.input} onChange={handleChange} required />
                    <input name="password" type="password" placeholder="password" style={styles.input} onChange={handleChange} required />
                    <input name="confirmPassword" type="password" placeholder="confirm password" style={styles.input} onChange={handleChange} required />
                    
                    <div style={styles.loginLink} onClick={() => navigate('/')}>
                        Already have an account?
                    </div>
                    
                    <button type="submit" style={styles.nextBtn} disabled={isLoading}>
                        {isLoading ? "sending..." : "sign in"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default CustomerRegister;