import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../core/context/AuthContext";
import BookingDetailsModal from "../booking/bookings_details_modal";
import { API_BASE_URL } from '../core/api.js';

export default function CookHomePage() {
    const { user, token } = useAuth();
    const [stats, setStats] = useState({ completedBookings: 0, upcomingBookings: 0, averageRating: 0 });
    const [activeBookings, setActiveBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    const [selectedBooking, setSelectedBooking] = useState(null);

    useEffect(() => {
        const fetchDashboardData = async () => {
            if (!user?.id) return;
            try {
                const [statsRes, bookingsRes] = await Promise.all([
                    axios.get(`${API_BASE_URL}/api/bookings/cook/${user.id}/stats`, {
                        headers: { Authorization: `Bearer ${token}` }
                    }),
                    axios.get(`${API_BASE_URL}/api/bookings/cook/${user.id}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    })
                ]);

                setStats(statsRes.data.data);
                // Filter only 'ACCEPTED' bookings for the "Active" section of the dashboard
                const active = (bookingsRes.data.data || []).filter(b => b.status === 'ACCEPTED');
                setActiveBookings(active);
            } catch (err) {
                console.error("Dashboard fetch error", err);
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, [user]);

    const KPI = [
        { label: 'Average Rating', value: stats.avgRating },
        { label: 'Completed Bookings', value: stats.completedBookings },
        { label: 'Total Earnings', value: `₱${Number(stats.totalEarnings || 0).toLocaleString()}` },
    ];

    if (loading) return <div style={styles.wrapper}>Loading Dashboard...</div>;

    return (
        <div style={styles.wrapper}>
            <h1 style={{ marginBottom: '24px' }}>Dashboard</h1>
            <div style={styles.row}>
                {KPI.map((kpi, index) => (
                    <div key={index} style={styles.card}>
                        <p style={styles.label}>{kpi.label}</p>
                        <p style={styles.value}>{kpi.value}</p>
                    </div>
                ))}
            </div>

            <div style={{ marginTop: '40px' }}>
                <h1 style={{ marginBottom: '20px' }}>Active Bookings</h1>
                {activeBookings.length === 0 ? (
                    <p style={{ color: '#aaa' }}>No active bookings at the moment.</p>
                ) : (
                    activeBookings.map((booking, index) => (
                        <div key={index} style={styles.bookingCard}>
                            <img
                                src={booking.serviceImage || 'https://via.placeholder.com/120x90'}
                                alt={booking.serviceTitle}
                                style={styles.bookingImg}
                            />

                            <div style={styles.bookingInfo}>
                                <p style={styles.bookingMenu}>{booking.serviceTitle}</p>
                                <p style={styles.bookingCustomer}>{booking.customerName || "Customer"}</p>
                            </div>

                            <div style={styles.bookingCol}>
                                <p style={styles.bookingColLabel}>Qty</p>
                                <p style={styles.bookingColValue}>{booking.quantity}</p>
                            </div>

                            <div style={styles.bookingCol}>
                                <p style={styles.bookingColLabel}>Date</p>
                                <p style={styles.bookingColValue}>{booking.scheduledDate}</p>
                            </div>

                            <div style={styles.bookingCol}>
                                <p style={styles.bookingColLabel}>Time</p>
                                <p style={styles.bookingColValue}>{booking.scheduledTime}</p>
                            </div>

                            <button style={styles.detailsBtn} onClick={() => setSelectedBooking(booking)}>
                                Details
                            </button>
                        </div>
                    ))
                )}
            </div>
            {selectedBooking && (
                <BookingDetailsModal 
                    booking={selectedBooking} 
                    onClose={() => setSelectedBooking(null)} 
                />
            )}
        </div>
    );
}

// Keep your existing styles object here

const styles = {
    wrapper: {
        padding: '20px',
    },
    row: {
        display: 'flex',
        justifyContent: 'space-between',
        gap: '16px',
    },
    card: {
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #a40a0a, red)',
        color: 'white',
        padding: '20px 28px',
        borderRadius: '8px',
        flex: 1,
    },
    label: {
        margin: 0,
        fontWeight: '200',
        fontSize: '25px',
        opacity: 0.85,
    },
    value: {
        margin: '4px 0 0',
        fontSize: '45px',
        fontWeight: '700',
    },

    // Booking card
    bookingCard: {
        display: 'flex',
        alignItems: 'center',
        gap: '24px',
        background: '#fff',
        border: '1px solid #b1b1b1',
        borderRadius: '10px',
        padding: '16px 20px',
        marginBottom: '12px',
        boxShadow: '0  1px 4px rgba(0,0,0,0.06)',
    },
    bookingImg: {
        width: '120px',
        height: '90px',
        borderRadius: '8px',
        objectFit: 'cover',
        flexShrink: 0,
    },
    bookingInfo: {
        flex: 1,
        minWidth: 0,
    },
    bookingMenu: {
        margin: 0,
        fontWeight: '700',
        fontSize: '24px',
    },
    bookingCustomer: {
        margin: '4px 0 0',
        fontSize: '16px',
        color: '#4d4d4d',
    },
    bookingCol: {
        textAlign: 'center',
        minWidth: '60px',
    },
    bookingColLabel: {
        margin: 0,
        fontSize: '12px',
        color: '#aaa',
        textTransform: 'uppercase',
        letterSpacing: '0.05em',
    },
    bookingColValue: {
        margin: '4px 0 0',
        fontWeight: '600',
        fontSize: '14px',
    },
    detailsBtn: {
        backgroundColor: '#faaf18',
        color: '#333',
        border: 'none',
        borderRadius: '6px',
        padding: '10px 18px',
        fontWeight: '700',
        fontSize: '13px',
        cursor: 'pointer',
        flexShrink: 0,
    },
};