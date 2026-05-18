import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../core/context/AuthContext";
import BookingDetailsModal from "./bookings_details_modal";

const TABS = ['Requests', 'Upcoming', 'History'];

export default function CookBookings() {
    const { user, token } = useAuth();
    const [activeTab, setActiveTab] = useState('Requests');
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    const [selectedBooking, setSelectedBooking] = useState(null);

    const fetchBookings = async () => {
        if (!user?.id) return;
        try {
            const res = await axios.get(`http://localhost:8080/api/bookings/cook/${user.id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setBookings(res.data.data || []);
        } catch (err) {
            console.error("Failed to fetch cook bookings", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBookings();
    }, [user]);

    const handleStatusUpdate = async (id, newStatus, actionStr) => {
        try {
            await axios.put(
                `http://localhost:8080/api/bookings/${id}/status?status=${newStatus}&action=${actionStr}`,
                {},
                { headers: { Authorization: `Bearer ${token}` } }
            );
            fetchBookings();
        } catch (err) {
            alert(err.response?.data?.error?.message || "Failed to update booking status.");
        }
    };

    const filteredBookings = bookings.filter(b => {
        if (activeTab === 'Requests') return b.status === 'PAID_PENDING';
        if (activeTab === 'Upcoming') return b.status === 'ACCEPTED';
        if (activeTab === 'History') return ['COMPLETED', 'REJECTED_REFUNDED', 'CANCELLED'].includes(b.status);
        return true;
    });

    const isScheduleMet = (dateStr, timeStr) => {
        const now = new Date();
        const schedule = new Date(`${dateStr}T${timeStr}`);
        return now >= schedule;
    };

    const styles = {
        // 1. Lock the page height and hide main overflow
        page: { 
            padding: '40px', 
            fontFamily: 'Arial, sans-serif',
            height: '91vh',
            boxSizing: 'border-box',
            display: 'flex',
            flexDirection: 'column',
            overflow: 'hidden'
        },
        // 2. Prevent title and tabs from shrinking
        title: { fontSize: '28px', fontWeight: 'bold', marginBottom: '30px', marginTop: 0, flexShrink: 0 },
        tabContainer: { display: 'flex', gap: '20px', marginBottom: '30px', borderBottom: '2px solid #eee', flexShrink: 0 },
        tab: (isActive) => ({
            padding: '10px 0',
            cursor: 'pointer',
            fontWeight: isActive ? 'bold' : 'normal',
            color: isActive ? '#0A0A1F' : '#888',
            borderBottom: isActive ? '3px solid #0A0A1F' : '3px solid transparent',
            marginBottom: '-2px'
        }),
        // 3. New scrollable area just for the cards
        scrollableArea: {
            flex: 1,
            overflowY: 'auto',
            paddingRight: '10px',
            paddingBottom: '20px'
        },
        cardList: { display: 'flex', flexDirection: 'column', gap: '20px' },
        card: { 
            display: 'flex', 
            padding: '20px', 
            backgroundColor: '#FCF8F2',
            border: '1px solid #D6CFC7', 
            borderRadius: '16px', 
            gap: '24px',
            alignItems: 'stretch'
        },
        image: { 
            width: '180px', 
            height: '120px', 
            objectFit: 'cover', 
            borderRadius: '12px',
            backgroundColor: '#eee'
        },
        contentArea: { 
            flex: 1, 
            display: 'flex', 
            flexDirection: 'column', 
            justifyContent: 'space-between' 
        },
        rowTop: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' },
        rowMiddle: { display: 'flex', flex: 1, alignItems: 'center' },
        rowBottom: { display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' },
        
        serviceTitle: { fontWeight: 'bold', fontSize: '20px', margin: '0' },
        dateTimeWrapper: { display: 'flex', gap: '24px', marginRight: 'auto', marginLeft: '40px' },
        dateTimeText: { fontSize: '16px', color: '#000', margin: 0 },
        customerName: { fontSize: '16px', color: '#000', margin: 0 },
        quantity: { fontSize: '16px', color: '#000', margin: 0, fontWeight: '500' },
        
        detailsBtn: { 
            backgroundColor: '#FF8C00', 
            color: '#000', 
            border: 'none', 
            borderRadius: '8px', 
            padding: '10px 32px', 
            fontWeight: 'bold', 
            fontSize: '14px',
            cursor: 'pointer' 
        },
        rejectBtn: { 
            backgroundColor: '#FCF8F2', 
            color: '#000', 
            border: '1px solid #000', 
            borderRadius: '8px', 
            padding: '10px 40px', 
            fontWeight: 'bold', 
            fontSize: '14px',
            cursor: 'pointer' 
        },
        acceptBtn: { 
            backgroundColor: '#BA1313', 
            color: '#fff', 
            border: 'none', 
            borderRadius: '8px', 
            padding: '10px 40px', 
            fontWeight: 'bold', 
            fontSize: '14px',
            cursor: 'pointer', 
            marginLeft: '12px' 
        },
        completeBtn: { 
            backgroundColor: '#27ae60', 
            color: '#fff', 
            border: 'none', 
            borderRadius: '8px', 
            padding: '10px 40px', 
            fontWeight: 'bold', 
            fontSize: '14px',
            cursor: 'pointer' 
        },
        statusBadge: { 
            display: 'inline-block', 
            padding: '8px 24px', 
            borderRadius: '8px', 
            fontSize: '14px', 
            fontWeight: 'bold', 
            backgroundColor: '#ddd' 
        }
    };

    if (loading) return <div style={styles.page}>Loading bookings...</div>;

    return (
        <div style={styles.page}>
            <h1 style={styles.title}>My Bookings</h1>

            <div style={styles.tabContainer}>
                {TABS.map(tab => (
                    <div 
                        key={tab} 
                        style={styles.tab(activeTab === tab)}
                        onClick={() => setActiveTab(tab)}
                    >
                        {tab}
                    </div>
                ))}
            </div>

            {/* 4. Wrap the cards block inside the scrollableArea */}
            <div style={styles.scrollableArea}>
                {filteredBookings.length === 0 ? (
                    <p style={{ color: '#999' }}>No bookings found in this category.</p>
                ) : (
                    <div style={styles.cardList}>
                        {filteredBookings.map(booking => (
                            <div key={booking.id} style={styles.card}>
                                
                                <img 
                                    src={booking.serviceImage || 'https://via.placeholder.com/180x120?text=No+Image'} 
                                    alt={booking.serviceTitle} 
                                    style={styles.image} 
                                />
                                
                                <div style={styles.contentArea}>
                                    {/* Top Row: Title, Date/Time, Details Button */}
                                    <div style={styles.rowTop}>
                                        <h3 style={styles.serviceTitle}>{booking.serviceTitle}</h3>
                                        <div style={styles.dateTimeWrapper}>
                                            <p style={styles.dateTimeText}>{booking.scheduledDate}</p>
                                            <p style={styles.dateTimeText}>{booking.scheduledTime}</p>
                                        </div>
                                        <button style={styles.detailsBtn} onClick={() => setSelectedBooking(booking)}>Details</button>
                                    </div>

                                    {/* Middle Row: Customer Name */}
                                    <div style={styles.rowMiddle}>
                                        <p style={styles.customerName}>
                                            {booking.customerName || "Customer Name"}
                                        </p>
                                    </div>

                                    {/* Bottom Row: Quantity and Action Buttons */}
                                    <div style={styles.rowBottom}>
                                        <p style={styles.quantity}>x{booking.quantity}</p>
                                        
                                        <div>
                                            {/* Buttons for PAID_PENDING (Requests Tab) */}
                                            {booking.status === 'PAID_PENDING' && (
                                                <>
                                                    <button 
                                                        style={styles.rejectBtn}
                                                        onClick={() => handleStatusUpdate(booking.id, 'REJECTED', 'REJECT')}
                                                    >
                                                        Reject
                                                    </button>
                                                    <button 
                                                        style={styles.acceptBtn}
                                                        onClick={() => handleStatusUpdate(booking.id, 'ACCEPTED', 'ACCEPT')}
                                                    >
                                                        Accept
                                                    </button>
                                                </>
                                            )}

                                            {/* Button for ACCEPTED (Upcoming Tab) */}
                                            {booking.status === 'ACCEPTED' && (
                                                <button 
                                                    style={{
                                                        ...styles.completeBtn,
                                                        backgroundColor: isScheduleMet(booking.scheduledDate, booking.scheduledTime) 
                                                            ? '#28a745' 
                                                            : '#a5d6a7', // Lighter green when locked
                                                        cursor: isScheduleMet(booking.scheduledDate, booking.scheduledTime) 
                                                            ? 'pointer' 
                                                            : 'not-allowed'
                                                    }} 
                                                    disabled={!isScheduleMet(booking.scheduledDate, booking.scheduledTime)}
                                                    onClick={() => handleStatusUpdate(booking.id, 'COMPLETED', 'COMPLETE')}
                                                >
                                                    {isScheduleMet(booking.scheduledDate, booking.scheduledTime) 
                                                        ? 'Mark as Completed' 
                                                        : 'Locked (Schedule not met)'}
                                                </button>
                                            )}

                                            {/* Badge for History Tab */}
                                            {['COMPLETED', 'REJECTED', 'CANCELLED'].includes(booking.status) && (
                                                <span style={styles.statusBadge}>{booking.status}</span>
                                            )}
                                        </div>
                                    </div>
                                </div>

                            </div>
                        ))}
                    </div>
                )}
            </div>
            {selectedBooking && <BookingDetailsModal booking={selectedBooking} onClose={() => setSelectedBooking(null)} />}
        </div>
    );
    
}
