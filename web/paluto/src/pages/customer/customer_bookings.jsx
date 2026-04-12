import React, { useState, useEffect } from "react";
import axios from "axios";

const TABS = ['Active', 'Completed', 'Rejected'];

export default function CustomerBookingsPage() {
    const [activeTab, setActiveTab] = useState('Active');
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchBookings = async () => {
            try {
                // 1. MATCH THE KEY: Your AuthContext uses 'userData'
                const userStr = localStorage.getItem('userData');
                const token = localStorage.getItem('token');
                
                if (!userStr || !token) {
                    console.error("No user or token found in localStorage");
                    setLoading(false);
                    return;
                }

                const userData = JSON.parse(userStr);
                
                // 2. USE DYNAMIC ID: Extracting the ID we just added to the DTO
                const userId = userData.id;

                if (!userId) {
                    console.error("User ID is missing from storage!", userData);
                    setLoading(false);
                    return;
                }

                // 3. DYNAMIC URL: Using backticks and the userId variable
                const response = await axios.get(`http://localhost:8080/api/bookings/customer/${userId}`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (response.data.success) {
                    setBookings(response.data.data);
                }
            } catch (error) {
                console.error("Fetch failed:", error.response?.data || error.message);
            } finally {
                setLoading(false);
            }
        };
        fetchBookings();
    }, []);

    useEffect(() => {
    const pendingBooking = sessionStorage.getItem('pendingBooking');
    if (!pendingBooking) return; // Normal visit, no pending payment

    const createBooking = async () => {
        try {
            const token = localStorage.getItem('token');

            const response = await axios.post(
                'http://localhost:8080/api/bookings/create',
                JSON.parse(pendingBooking),
                { headers: { 'Authorization': `Bearer ${token}` } }
            );

            if (response.data.success) {
                alert('🎉 Booking confirmed! Your booking has been created successfully.');
            }
        } catch (error) {
            alert(
                error.response?.data?.error?.message ||
                'Payment was received but booking creation failed. Please contact support.'
            );
        } finally {
            sessionStorage.removeItem('pendingBooking'); // ADDED: always clean up
        }
    };

    createBooking();
}, []);

    const filtered = bookings.filter(b => {
        const status = b.status ? b.status.toLowerCase() : '';
        
        if (activeTab === 'Active') {
            return status === 'paid_pending' || status === 'accepted' || status === 'pending';
        }
        if (activeTab === 'Completed') return status === 'completed';
        if (activeTab === 'Rejected') return status === 'rejected' || status === 'cancelled';
        return false;
    });

    if (loading) return <div style={styles.wrapper}>Loading bookings...</div>;

    return (
        <div style={styles.wrapper}>
            <h2 style={styles.pageTitle}>Bookings</h2>

            <div style={styles.tabs}>
                {TABS.map(tab => (
                    <button
                        key={tab}
                        onClick={() => setActiveTab(tab)}
                        style={{
                            ...styles.tab,
                            ...(activeTab === tab ? styles.tabActive : {}),
                        }}
                    >
                        {tab}
                    </button>
                ))}
            </div>

            <div style={styles.list}>
                {filtered.length === 0 ? (
                    <p style={styles.empty}>No {activeTab.toLowerCase()} bookings found.</p>
                ) : (
                    filtered.map(booking => (
                        <div key={booking.id} style={styles.card}>
                            <img src={booking.serviceImage} alt={booking.serviceTitle} style={styles.cardImg} />

                            <div style={styles.cardInfo}>
                                <p style={styles.menuName}>{booking.serviceTitle}</p>
                                <p style={styles.quantity}>x{booking.quantity}</p>
                            </div>

                            <div style={styles.cardMeta}>
                                <p style={styles.metaText}>{booking.scheduledDate}</p>
                                <p style={styles.metaText}>{booking.scheduledTime}</p>
                            </div>

                            <div style={styles.cardRight}>
                                <p style={{
                                    ...styles.paymentStatus,
                                    color: booking.status.includes('PENDING') ? '#ec9812' : '#28a745'
                                }}>
                                    {booking.status.replace('_', ' ')}
                                </p>
                                <button style={styles.detailsBtn}>Details</button>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

const styles = {
    wrapper: {
        padding: '32px 40px',
        height: '100%',
        boxSizing: 'border-box',
        display: 'flex',
        flexDirection: 'column',
        overflow: 'hidden',
    },
    pageTitle: {
        fontSize: '26px',
        fontWeight: '700',
        margin: '0 0 24px',
    },
    tabs: {
        display: 'flex',
        gap: '12px',
        marginBottom: '24px',
    },
    tab: {
        padding: '10px 24px',
        borderRadius: '999px',
        border: 'none',
        fontSize: '14px',
        fontWeight: '600',
        cursor: 'pointer',
        backgroundColor: 'transparent',
        color: '#555',
    },
    tabActive: {
        backgroundColor: '#1a1a1a',
        color: '#fff',
    },
    list: {
        display: 'flex',
        flexDirection: 'column',
        gap: '16px',
        overflowY: 'auto',
        flex: 1,
    },
    empty: {
        color: '#aaa',
        fontSize: '14px',
    },
    card: {
        display: 'flex',
        alignItems: 'center',
        gap: '20px',
        backgroundColor: '#fdf8f2',
        border: '1.5px solid #eee',
        borderRadius: '16px',
        padding: '16px 20px',
    },
    cardImg: {
        width: '120px',
        height: '90px',
        objectFit: 'cover',
        borderRadius: '10px',
        flexShrink: 0,
    },
    cardInfo: {
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        gap: '4px',
    },
    menuName: {
        margin: 0,
        fontSize: '16px',
        fontWeight: '700',
    },
    customer: {
        margin: 0,
        fontSize: '14px',
        color: '#555',
    },
    quantity: {
        margin: 0,
        fontSize: '14px',
        color: '#555',
    },
    cardMeta: {
        display: 'flex',
        flexDirection: 'column',
        gap: '4px',
        alignItems: 'center',
        minWidth: '200px',
    },
    metaText: {
        margin: 0,
        fontSize: '14px',
        color: '#333',
    },
    cardRight: {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'flex-end',
        gap: '12px',
        minWidth: '100px',
    },
    paymentStatus: {
        margin: 0,
        fontSize: '13px',
        fontWeight: '600',
        textTransform: 'uppercase'
    },
    detailsBtn: {
        backgroundColor: '#F5A623',
        color: '#fff',
        border: 'none',
        borderRadius: '10px',
        padding: '10px 28px',
        fontWeight: '700',
        fontSize: '14px',
        cursor: 'pointer',
    },
};