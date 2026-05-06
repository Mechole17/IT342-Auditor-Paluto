import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Ramen from '../../asset/ramen.png';
import axios from 'axios';
import { useAuth } from '../../context/AuthContext';

export default function CustomerRegister() {
    const {login} = useAuth();
    const navigate = useNavigate();
    
    const [formData, setFormData] = useState({
        firstname: '',
        lastname: '',
        address: '',
        email: '',
        password: '',
        confirmPassword: ''
    });

    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // --- ARCHITECT'S VALIDATION LOGIC ---
    const validateForm = () => {
        const { email, password, confirmPassword } = formData;

        // 1. Email Regex (Standard RFC 5322)
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            return "Please enter a valid email address.";
        }

        // 2. Password Complexity Regex
        // Min 8 chars, 1 Upper, 1 Lower, 1 Number, 1 Special Char
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        if (!passwordRegex.test(password)) {
            return "Password must be at least 8 characters long and include uppercase, lowercase, a number, and a special character.";
        }

        // 3. Confirm Password Match
        if (password !== confirmPassword) {
            return "Passwords do not match!";
        }

        return null; // No errors
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);

        // Run validations
        const validationError = validateForm();
        if (validationError) {
            setError(validationError);
            return;
        }

        setIsLoading(true);

        try {
            const response = await axios.post('http://localhost:8080/api/customer/register', {
                firstname: formData.firstname,
                lastname: formData.lastname,
                address: formData.address,
                email: formData.email,
                password: formData.password
            });

            if (response.data.success) {
                // 1. Extract the data (token and user object)
                const { accessToken, user } = response.data.data;

                // 2. Trigger the Global Login (this saves to localStorage & state)
                login(user, accessToken);

                // 3. Redirect directly to their dashboard (Role-Aware)
                if (user.role === 'CUSTOMER') navigate('/customer');
                console.log(response);
                alert("Account created successfully! Welcome to PALUTO! You are now logged in as a customer.");
            }
        } catch (err) {
            if (err.response && err.response.data) {
                const apiResponse = err.response.data;

                // 2. Access the nested message: apiResponse.error.message
                // This will grab "The email is already in use" from your service
                const errorMessage = apiResponse.error?.message || "Registration failed.";
                
                setError(errorMessage);
                
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
            setIsLoading(false);
        }
    };

    // --- STYLES (Keep your existing styles, just showing the component structure) ---
    const styles = {
        container: { display: 'flex', height: '100vh', width: '100vw', fontFamily: 'Arial, sans-serif', overflow: 'hidden' },
        leftSide: { flex: 1, backgroundColor: '#ecb92a', display: 'flex', flexDirection: 'column', padding: '40px', position: 'relative' },
        logo: { fontSize: '32px', fontWeight: '900', color: '#000', marginBottom: '50px', cursor: 'pointer' },
        foodImg: { width: '85%', margin: 'auto' },
        rightSide: { flex: 1, backgroundColor: '#fff', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '0 80px' },
        title: { fontSize: '42px', fontWeight: 'bold', marginBottom: '10px' },
        errorTxt: { 
            color: '#c40303', 
            fontWeight: 'bold', 
            marginBottom: '15px', 
            fontSize: '13px',
            padding: '10px',
        },
        inputGroup: { display: 'flex', gap: '20px' },
        input: { width: '100%', padding: '12px', margin: '8px 0', borderRadius: '12px', border: '1.5px solid #7b7a7a', fontSize: '15px', outline: 'none', boxSizing: 'border-box' },
        loginLink: { textAlign: 'right', marginTop: '15px', fontSize: '14px', fontWeight: '600', cursor: 'pointer' },
        loginBtn: { backgroundColor: '#0A0A1F', color: '#fff', padding: '16px 80px', borderRadius: '15px', fontWeight: 'bold', fontSize: '18px', border: 'none', alignSelf: 'center', marginTop: '30px', cursor: 'pointer', opacity: isLoading ? 0.7 : 1 }
    };

    return (
        <div style={styles.container}>
            <div style={styles.leftSide}>
                <div style={styles.logo} onClick={() => navigate('/')}>PALUTO</div>
                <img src={Ramen} alt="Paluto Food" style={styles.foodImg} />
            </div>

            <div style={styles.rightSide}>
                <h1 style={styles.title}>Join Us</h1>
                
                {error && <div style={styles.errorTxt}>{error}</div>}
                
                <form onSubmit={handleSubmit} style={{display: 'flex', flexDirection: 'column', width: '80%', alignSelf: 'center'}}>
                    <div style={styles.inputGroup}>
                        <input name="firstname" type="text" placeholder="first name" style={styles.input} value={formData.firstname} onChange={handleChange} required />
                        <input name="lastname" type="text" placeholder="last name" style={styles.input} value={formData.lastname} onChange={handleChange} required />
                    </div>

                    <input name="address" type="text" placeholder="home address" style={styles.input} value={formData.address} onChange={handleChange} required />
                    <input name="email" type="email" placeholder="email" style={styles.input} value={formData.email} onChange={handleChange} required />
                    <input name="password" type="password" placeholder="password" style={styles.input} value={formData.password} onChange={handleChange} required />
                    <input name="confirmPassword" type="password" placeholder="confirm password" style={styles.input} value={formData.confirmPassword} onChange={handleChange} required />
                    
                    <div style={styles.loginLink} onClick={() => navigate('/')}>
                        Already have an account?
                    </div>
                    
                    <button type="submit" style={styles.loginBtn} disabled={isLoading}>
                        {isLoading ? "Signing up..." : "sign in"}
                    </button>
                </form>
            </div>
        </div>
    );
}