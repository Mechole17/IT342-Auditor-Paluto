import LandingPage from './pages/public/landingpage.jsx';
import Cooks from './pages/public/cooks.jsx';

import CustomerRegister from './pages/customer/customer_registrationpage';
import CustomerHomePage from './pages/customer/customer_homepage';
import CustomerCooks from './pages/customer/customer_cooks.jsx';
import CustomerBookings from './pages/customer/customer_bookings.jsx';
import ServiceDetails from './pages/customer/service_detailspage.jsx';
import PaymentPage from './pages/customer/service_paymentpage.jsx';

import CookRegister from './pages/cook/cook_registrationpage.jsx';
import CookHomePage from './pages/cook/cook_dashboard.jsx';
import CookBookings from './pages/cook/cook_bookings.jsx';
import CookPortfolio from './pages/cook/cook_portfolio.jsx';
import SelectRole from './pages/public/authentication/select_role.jsx';
import CustomerExtraDetails from './pages/public/authentication/customer_extra_details.jsx';
import CookExtraDetails from './pages/public/authentication/cook_extra_details.jsx';
import OAuthSuccess from './pages/public/authentication/oauth_success_page.jsx';

import AuthProvider from './context/AuthContext.jsx';
import AdminHomePage from './pages/admin/admin_homepage.jsx';
import ProtectedRoute from './context/protectedRoutes.jsx';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';
import PublicLayout from './layout/public_layout.jsx';
import CookLayout from './layout/cook_layout.jsx';
import CustomerLayout from './layout/customer_layout.jsx';

function App() {
  return (
    <div>
        <BrowserRouter>
          <AuthProvider>
            <Routes>
              {/* No layout — standalone pages */}
              <Route path="/customer/register" element={<CustomerRegister />} />
              <Route path="/cook/register" element={<CookRegister />} />
              <Route path="/select-role" element={<SelectRole />} />
              <Route path="/register-customer-details" element={<CustomerExtraDetails />} />
              <Route path="/register-cook-details" element={<CookExtraDetails />} />
              <Route path="/oauth-success" element={<OAuthSuccess />} />

              {/* Public layout */}
              <Route element={<PublicLayout />}>
                <Route path="/" element={<LandingPage />} />
                <Route path="/cooks" element={<Cooks />} />
              </Route>
          
            {/* Customer layout wraps all /customer/* pages */}
            <Route path="/customer" element={
              <ProtectedRoute allowedRoles={['CUSTOMER']}>
                <CustomerLayout />
              </ProtectedRoute>
              }>
              <Route index element={<CustomerHomePage />} />  {/* /customer */}
              <Route path="cooks" element={<CustomerCooks />} /> {/* /customer/cooks */}
              <Route path="bookings" element={<CustomerBookings />} /> {/* /customer/bookings */}
              <Route path="service-details/:id" element={<ServiceDetails />} /> {/* /customer/service-details */}
              <Route path="service-payment" element={<PaymentPage />} /> {/* /customer/payment */}
            </Route>

            {/* Cook layout wraps all /cook/* pages */}
            <Route path="/cook" element={
              <ProtectedRoute allowedRoles={['COOK']}>
                <CookLayout />
              </ProtectedRoute>
            }>
              <Route index element={<CookHomePage />} />  {/* /cook */}
              <Route path="bookings" element={<CookBookings />} /> {/* /cook/bookings */}
              <Route path="portfolio" element={<CookPortfolio />} /> {/* /cook/portfolio */}
              {/* <Route path="orders" element={<CookOrders />} /> → /cook/orders */}

            </Route>
            
            {/* Admin layout wraps all /admin/* pages */}
            <Route path="/admin" element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <AdminHomePage />
              </ProtectedRoute>
            }>
              </Route>

            </Routes>
          </AuthProvider>

        </BrowserRouter>
    </div>
  );
}

export default App;
