import logo from './logo.svg';
import LandingPage from './pages/landingpage';
import CustomerRegister from './pages/customer_registrationpage';
import CustomerHomePage from './pages/customer_homepage';
import CookRegister from './pages/cook_registrationpage';
import CookHomePage from './pages/cook_homepage';
import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';

function App() {
  return (
    <div className="App">
        <BrowserRouter>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/customer/register" element={<CustomerRegister />} />
        <Route path="/customer/home" element={<CustomerHomePage />} />
        <Route path="/cook/register" element={<CookRegister />} />  
        <Route path="/cook/home" element={<CookHomePage />} />
      </Routes>
    </BrowserRouter>
    </div>
  );
}

export default App;
