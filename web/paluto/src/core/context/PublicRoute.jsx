import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

export default function PublicRoute({ children }) {
    const { user, loading } = useAuth();

    if (loading) return null;

    if (user) {
        // Redirect to their dashboard based on role
        if (user.role === 'CUSTOMER') return <Navigate to="/customer" replace />;
        if (user.role === 'COOK') return <Navigate to="/cook" replace />;
        if (user.role === 'ADMIN') return <Navigate to="/admin" replace />;
    }

    return children;
}