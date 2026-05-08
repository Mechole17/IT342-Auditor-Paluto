import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext'; // Adjust path if necessary

export default function AdminUsers() {
    const { user } = useAuth(); // Assuming your auth context provides a way to get the current token
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                // Retrieve your JWT token (adjust this based on where you store it: localStorage, cookies, or context)
                const token = localStorage.getItem('token'); 

                const response = await fetch('http://localhost:8080/api/admin/users', {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}` 
                    }
                });

                const result = await response.json();

                if (response.ok) {
                    // result.data accesses the payload inside your ApiResponse wrapper
                    setUsers(result.data); 
                } else {
                    setError(result.message || 'Failed to fetch users');
                }
            } catch (err) {
                setError("Network error: Could not connect to the server.");
            } finally {
                setLoading(false);
            }
        };

        fetchUsers();
    }, []);

    if (loading) return <div style={styles.message}>Loading users...</div>;
    if (error) return <div style={{ ...styles.message, color: 'red' }}>{error}</div>;

    return (
        <div style={styles.container}>
            <h2 style={styles.header}>User Management</h2>
            
            <div style={styles.tableWrapper}>
                <table style={styles.table}>
                    <thead>
                        <tr style={styles.tableHeadRow}>
                            <th style={styles.th}>ID</th>
                            <th style={styles.th}>Name</th>
                            <th style={styles.th}>Email</th>
                            <th style={styles.th}>Role</th>
                            <th style={styles.th}>Provider</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map((u) => (
                            <tr key={u.id} style={styles.tableBodyRow}>
                                <td style={styles.td}>{u.id}</td>
                                <td style={styles.td}>{u.firstname} {u.lastname}</td>
                                <td style={styles.td}>{u.email}</td>
                                <td style={styles.td}>
                                    <span style={u.role === 'COOK' ? styles.badgeCook : styles.badgeCustomer}>
                                        {u.role}
                                    </span>
                                </td>
                                <td style={styles.td}>{u.authProvider}</td>
                            </tr>
                        ))}
                        {users.length === 0 && (
                            <tr>
                                <td colSpan="5" style={styles.emptyState}>No users found.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

const styles = {
    // 1. Constrain container and make it a flex column
    container: { 
        display: 'flex',
        flexDirection: 'column',
        height: 'calc(100vh - 120px)', // Adjusts for the navbar height and margins
        padding: '20px', 
        backgroundColor: '#fff', 
        borderRadius: '8px', 
        boxShadow: '0 2px 4px rgba(0,0,0,0.05)', 
        margin: '20px',
        boxSizing: 'border-box'
    },
    header: { 
        marginBottom: '20px', 
        color: '#333',
        flexShrink: 0 // Prevents the header from shrinking
    },
    message: { padding: '20px', fontSize: '18px', textAlign: 'center' },
    
    // 2. Make the wrapper scrollable
    tableWrapper: { 
        flex: 1, 
        overflowY: 'auto', 
        border: '1px solid #eee', // Optional: adds a nice border around the scrollable area
        borderRadius: '4px'
    },
    table: { 
        width: '100%', 
        borderCollapse: 'collapse', 
        textAlign: 'left' 
    },
    tableHeadRow: { 
        // Removed background color here, it must be applied directly to the 'th' for sticky to work
    },
    
    // 3. Make the headers sticky
    th: { 
        position: 'sticky', 
        top: 0, 
        backgroundColor: '#f8f9fa', // Background is required so rows don't show through the header
        padding: '12px 15px', 
        color: '#555', 
        fontWeight: '600',
        zIndex: 1, // Ensures the header stays above the scrolling rows
        borderBottom: '2px solid #eee'
    },
    tableBodyRow: { 
        borderBottom: '1px solid #eee' 
    },
    td: { 
        padding: '12px 15px', 
        color: '#333' 
    },
    emptyState: { padding: '20px', textAlign: 'center', color: '#888' },
    badgeCustomer: { backgroundColor: '#FF8A00', color: '#fff', padding: '4px 10px', borderRadius: '12px', fontSize: '12px', fontWeight: 'bold' },
    badgeCook: { backgroundColor: '#E53935', color: '#fff', padding: '4px 10px', borderRadius: '12px', fontSize: '12px', fontWeight: 'bold' }
};