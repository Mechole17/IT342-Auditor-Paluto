import LandingPage from './pages/landingpage';
import CustomerRegister from './pages/customer_registrationpage';
import CustomerHomePage from './pages/customer_homepage';
import CookRegister from './pages/cook_registrationpage';
import CookHomePage from './pages/cook_homepage';
import AuthProvider from './context/AuthContext.jsx';
import AdminHomePage from './pages/admin_homepage';
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

              {/* Public layout */}
              <Route element={<PublicLayout />}>
              <Route path="/" element={<LandingPage />} />
                  {/* <Route path="/cooks" element={<CooksPage />} />
                  <Route path="/bookings" element={<BookingsPage />} /> */}
              </Route>
          
            {/* Customer layout wraps all /customer/* pages */}
            <Route path="/customer" element={
              <ProtectedRoute allowedRoles={['CUSTOMER']}>
                <CustomerLayout />
              </ProtectedRoute>
              }>
              <Route index element={<CustomerHomePage />} />  {/* /customer */}
              {/* future customer routes: */}
              {/* <Route path="cooks" element={<CooksPage />} /> → /customer/cooks */}
              {/* <Route path="bookings" element={<BookingsPage />} /> → /customer/bookings */}
            </Route>

            {/* Cook layout wraps all /cook/* pages */}
            <Route path="/cook" element={
              <ProtectedRoute allowedRoles={['COOK']}>
                <CookLayout />
              </ProtectedRoute>
            }>
              <Route index element={<CookHomePage />} />  {/* /cook */}
              {/* future cook routes: */}
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
