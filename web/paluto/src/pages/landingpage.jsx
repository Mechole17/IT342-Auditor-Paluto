import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios'; // Ensure you've run: npm install axios

const LandingPage = () => {
    const [isLoginOpen, setIsLoginOpen] = useState(false);
    const navigate = useNavigate();

    // --- NEW: Auth State ---
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const styles = {
        container: { height: '100vh', width: '100vw', margin: 0, padding: 0, overflow: 'hidden', fontFamily: 'Arial, sans-serif' },
        navbar: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '15px 50px', backgroundColor: '#fff' },
        navLinks: { display: 'flex', gap: '25px', alignItems: 'center' },
        orangeBadge: { backgroundColor: '#FF8A00', color: '#fff', padding: '5px 20px', borderRadius: '15px', fontWeight: 'bold', fontSize: '14px', border: 'none' },
        navyBtn: { backgroundColor: '#0A0A1F', color: '#fff', padding: '10px 35px', borderRadius: '20px', fontWeight: 'bold', border: 'none', cursor: 'pointer' },
        hero: { 
            height: 'calc(100vh - 75px)', 
            background: 'linear-gradient(to right, #FFB800 50%, #FFD600 100%)', 
            display: 'flex', alignItems: 'center', padding: '0 80px', position: 'relative' 
        },
        heroText: { color: '#fff', fontSize: '64px', fontWeight: 'bold', maxWidth: '600px', lineHeight: '1.1' },
        ctaContainer: { display: 'flex', gap: '20px', marginTop: '40px' },
        redBtn: { backgroundColor: '#E60000', color: '#fff', padding: '18px 45px', borderRadius: '15px', border: 'none', fontWeight: 'bold', cursor: 'pointer' },
        lightBtn: { backgroundColor: '#E5E5E5', color: '#000', padding: '18px 45px', borderRadius: '15px', border: 'none', fontWeight: 'bold', cursor: 'pointer' },
        foodImage: { position: 'absolute', right: '50px', width: '500px', height: '500px', objectFit: 'contain' },
        
        // Modal
        overlay: { position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.4)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 },
        modal: { background: '#fff', padding: '40px', borderRadius: '30px', width: '450px', textAlign: 'center', position: 'relative' },
        input: { width: '100%', padding: '15px', margin: '10px 0', borderRadius: '10px', border: '1px solid #ccc', boxSizing: 'border-box', outline: 'none' },
        errorTxt: { color: '#E60000', fontSize: '13px', margin: '5px 0', fontWeight: 'bold' }
    };

    // --- NEW: Login Handler ---
    const handleLogin = async (e) => {
        e.preventDefault();
        setError(null);

        // Validation: Basic check
        if (!email || !password) {
            setError("Email and Password are required.");
            return;
        }

        setIsLoading(true);
        try {
            // Adjust this URL to match your Spring Boot port/address
            const response = await axios.post('http://localhost:8080/api/auth/login', {
                email: email,
                password: password
            });

            if (response.data.success) {
                const { accessToken, user } = response.data.data;

                // 1. Store the token for future API calls
                localStorage.setItem('token', accessToken);
                localStorage.setItem('userData', JSON.stringify(user));

                // 2. Redirect based on role from your SDD
                if (user.role === 'CUSTOMER') {
                    navigate('/customer/home');
                } else if (user.role === 'COOK') {
                    navigate('/cook/home');
                }
            }
        } catch (err) {
            // Handle 401 Unauthorized or Connection errors
            const errorMessage = err.response?.data?.error?.message || "Invalid credentials or server error.";
            setError(errorMessage);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div style={styles.container}>
            <nav style={styles.navbar}>
                <div style={{fontSize: '32px', fontWeight: 'bold'}}>PALUTO</div>
                <div style={styles.navLinks}>
                    <button style={styles.orangeBadge}>Home</button>
                    <span style={{color: '#666'}}>Cooks</span>
                    <span style={{color: '#666'}}>Bookings</span>
                </div>
                <button style={styles.navyBtn} onClick={() => setIsLoginOpen(true)}>sign in</button>
            </nav>

            <div style={styles.hero}>
                <div style={{zIndex: 2}}>
                    <h1 style={styles.heroText}>Professional<br/>Home cooking,<br/>Delivered at your<br/>table.</h1>
                    <div style={styles.ctaContainer}>
                        <button style={styles.redBtn} onClick={() => navigate('/customer/register')}>Find a cook</button>
                        <button style={styles.lightBtn} onClick={() => navigate('/cook/register')}>Join as a cook</button>
                    </div>
                </div>
                <img src="https://i.imgur.com/your_ramen_image.png" alt="Ramen" style={styles.foodImage} />
            </div>

            {isLoginOpen && (
                <div style={styles.overlay} onClick={() => { setIsLoginOpen(false); setError(null); }}>
                    <div style={styles.modal} onClick={e => e.stopPropagation()}>
                        <button style={{position:'absolute', right: '20px', top: '20px', border:'none', background:'none', fontSize:'20px', cursor:'pointer'}} onClick={() => setIsLoginOpen(false)}>✕</button>
                        
                        <h2 style={{fontSize: '28px', marginBottom: '30px'}}>Welcome Back!</h2>
                        
                        {/* Error Message Display */}
                        {error && <div style={styles.errorTxt}>{error}</div>}

                        <form onSubmit={handleLogin}>
                            <input 
                                type="email" 
                                placeholder="email" 
                                style={styles.input} 
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                            <input 
                                type="password" 
                                placeholder="password" 
                                style={styles.input} 
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                            />
                            
                            <div style={{textAlign: 'right', fontSize: '12px', marginBottom: '20px', cursor: 'pointer'}}>forgot password</div>
                            
                            <button 
                                type="submit" 
                                disabled={isLoading}
                                style={{
                                    ...styles.navyBtn, 
                                    width: '60%', 
                                    padding: '15px', 
                                    fontSize: '18px',
                                    opacity: isLoading ? 0.7 : 1,
                                    cursor: isLoading ? 'not-allowed' : 'pointer'
                                }}
                            >
                                {isLoading ? "Checking..." : "sign in"}
                            </button>
                        </form>

                        <div style={{margin: '20px 0'}}>or sign in with</div>
                        <img src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg" alt="google" style={{width:'40px', cursor:'pointer'}} />
                    </div>
                </div>
            )}
        </div>
    );
};

export default LandingPage;