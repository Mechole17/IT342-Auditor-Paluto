// src/context/AuthContext.js
import React, { createContext, useState, useEffect, useContext } from 'react';

const AuthContext = createContext(null);

export default function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [loading, setLoading] = useState(true);

    // AuthContext.js
    useEffect(() => {
        try {
            const savedToken = localStorage.getItem('token');
            const savedUser = localStorage.getItem('userData');
            if (savedToken && savedUser) {
                setToken(savedToken);
                setUser(JSON.parse(savedUser));
            }
        } catch {
            localStorage.removeItem('userData');
            localStorage.removeItem('token');
        } finally {
            setLoading(false);  // ← always runs once on mount
        }
    }, []);  // ← empty array, runs ONCE only

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