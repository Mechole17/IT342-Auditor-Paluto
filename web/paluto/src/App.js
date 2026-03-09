import LandingPage from './pages/landingpage';
import CustomerRegister from './pages/customer_registrationpage';
import CustomerHomePage from './pages/customer_homepage';
import CookRegister from './pages/cook_registrationpage';
import CookHomePage from './pages/cook_homepage';
import AuthProvider from './context/AuthContext.jsx';
import AdminHomePage from './pages/admin_homepage';
import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';

function App() {
  return (
    <div>
    <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/customer/register" element={<CustomerRegister />} />
            <Route path="/customer/home" element={<CustomerHomePage />} />
            <Route path="/cook/register" element={<CookRegister />} />  
            <Route path="/cook/home" element={<CookHomePage />} />
            <Route path="/admin/home" element={<AdminHomePage />} />
          </Routes>
        </BrowserRouter>
    </AuthProvider>
    </div>
  );
}

export default App;
