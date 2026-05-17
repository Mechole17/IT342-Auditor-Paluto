import LandingPage from './service/landingpage.jsx';
import Cooks from './profiles/cooks.jsx';

import CustomerRegister from './authentication/customer_registrationpage.jsx';
import CustomerHomePage from './service/customer_homepage.jsx';
import CustomerCooks from './profiles/customer_cooks.jsx';
import CustomerBookings from './booking/customer_bookings.jsx';
import ServiceDetails from './service/service_detailspage.jsx';
import PaymentPage from './booking/service_paymentpage.jsx';

import CookRegister from './authentication/cook_registrationpage.jsx';
import CookHomePage from './profiles/cook_dashboard.jsx';
import CookBookings from './booking/cook_bookings.jsx';
import CookPortfolio from './profiles/cook_portfolio.jsx';
import SelectRole from './authentication/select_role.jsx';
import CustomerExtraDetails from './authentication/customer_extra_details.jsx';
import CookExtraDetails from './authentication/cook_extra_details.jsx';
import OAuthSuccess from './authentication/oauth_success_page.jsx';

import AdminLayout from './core/layout/admin_layout.jsx';
import AdminUsers from './profiles/admin_users.jsx';

import PublicRoute from './core/context/PublicRoute.jsx';
import CookProfile from './profiles/cook_profile.jsx';
import AdminHomePage from './profiles/admin_homepage.jsx';

import AuthProvider from './core/context/AuthContext.jsx';
import ProtectedRoute from './core/context/protectedRoutes.jsx';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';
import PublicLayout from './core/layout/public_layout.jsx';
import CookLayout from './core/layout/cook_layout.jsx';
import CustomerLayout from './core/layout/customer_layout.jsx';

function App() {
  return (
    <div>
        <BrowserRouter>
          <AuthProvider>
            <Routes>
              {/* No layout — standalone pages */}
              <Route path="/customer/register" element={<PublicRoute><CustomerRegister /></PublicRoute>} />
              <Route path="/cook/register" element={<PublicRoute><CookRegister /></PublicRoute>} />
              <Route path="/select-role" element={<SelectRole />} />
              <Route path="/register-customer-details" element={<CustomerExtraDetails />} />
              <Route path="/register-cook-details" element={<CookExtraDetails />} />
              <Route path="/oauth-success" element={<OAuthSuccess />} />

              {/* Public layout */}
              <Route element={<PublicRoute><PublicLayout /></PublicRoute>}>
                <Route path="/" element={<LandingPage />} />
                <Route path="/cooks" element={<Cooks />} />
                <Route path="/cooks/:id" element={<CookProfile />} />
              </Route>
          
            {/* Customer layout wraps all /customer/* pages */}
            <Route path="/customer" element={
              <ProtectedRoute allowedRoles={['CUSTOMER']}>
                <CustomerLayout />
              </ProtectedRoute>
              }>
              <Route index element={<CustomerHomePage />} />  {/* /customer */}
              <Route path="cooks" element={<CustomerCooks />} /> {/* /customer/cooks */}
              <Route path="cooks/:id" element={<CookProfile />} />
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
                <AdminLayout />
              </ProtectedRoute>
            }>
              <Route index element={<AdminHomePage />} />  {/* /admin */}
              <Route path="users" element={<AdminUsers />} /> {/* /admin/users */}
            </Route>

            </Routes>
          </AuthProvider>

        </BrowserRouter>
    </div>
  );
}

export default App;
