// src/components/ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, allowedRoles }) {
    const { user } = useAuth();

    if (!user) return <Navigate to="/" replace />;//replace later to a error page or login page
    if (!allowedRoles.includes(user.role)) return <Navigate to="/dashboard" replace />;

    return children;
}