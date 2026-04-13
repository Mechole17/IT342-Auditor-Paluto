// src/context/AuthContext.js
import { createContext, useState, useEffect, useContext } from 'react';
import axios from 'axios';

const AuthContext = createContext(null);

export default function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [loading, setLoading] = useState(true);
    

    useEffect(() => {
        const verifySession = async () => {
            const savedToken = localStorage.getItem('token');
            
            if (savedToken) {
                try {
                    // ACTIVE CHECK: Ask backend if this token is still good
                    const res = await axios.get('http://localhost:8080/api/auth/me', {
                        headers: { Authorization: `Bearer ${savedToken}` }
                    });

                    // Standardize the response (handle your ApiResponse wrapper)
                    const data = res.data.success ? res.data.data : res.data;
                    const userData = data.user || data;

                    const userProfile = {
                        id: userData.id,
                        email: userData.email,
                        firstName: userData.firstname,
                        lastName: userData.lastname,
                        role: userData.role,
                    };

                    setUser(userProfile);
                    setToken(savedToken);
                    localStorage.setItem('userData', JSON.stringify(userProfile));
                } catch (err) {
                    console.error("Session verification failed:", err);
                    logout(); // Token was fake, expired, or user was deleted
                }
            }
            setLoading(false);
        };

        verifySession();
    }, []);

    const login = (userData, accessToken) => {
        const user = {
            id: userData.id, // Now this will exist in the response!
            email: userData.email,
            firstName: userData.firstname,
            lastName: userData.lastname,
            role: userData.role,
        };
        
        setUser(user);
        setToken(accessToken);
        localStorage.setItem('token', accessToken);
        localStorage.setItem('userData', JSON.stringify(user)); 
    };

    const logout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem('token');
        localStorage.removeItem('userData');
    };

    return (
        <AuthContext.Provider value={{ user, token, login, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}