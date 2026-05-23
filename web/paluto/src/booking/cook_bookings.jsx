import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../core/context/AuthContext";
import BookingDetailsModal from "./bookings_details_modal";
import { API_BASE_URL } from '../core/api.js';

const TABS = ['Requests', 'Upcoming', 'History'];

export default function CookBookings() {
    const { user, token } = useAuth();
    const [activeTab, setActiveTab] = useState('Requests');
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    const [selectedBooking, setSelectedBooking] = useState(null);

    // 🚀 Track processing states
    const [processingId, setProcessingId] = useState(null);
    const [currentAction, setCurrentAction] = useState(null);

    const fetchBookings = async () => {
        if (!user?.id) return;
        try {
            const res = await axios.get(`${API_BASE_URL}/api/bookings/cook/${user.id}`, {
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
        if (actionStr === 'REJECT') {
            const confirm = window.confirm("Are you sure you want to reject this booking? The customer will be refunded.");
            if (!confirm) return;
        }
        
        if (actionStr === 'ACCEPT') {
            const confirm = window.confirm("Are you sure you want to accept this booking? You are committing to this schedule.");
            if (!confirm) return;
        }

        if (actionStr === 'COMPLETE') {
            const confirm = window.confirm("Are you sure you want to mark this booking as completed?");
            if (!confirm) return;
        }

        try {
            setProcessingId(id);
            setCurrentAction(actionStr);

            await axios.put(
                `${API_BASE_URL}/api/bookings/${id}/status?status=${newStatus}&action=${actionStr}`,
                {},
                { headers: { Authorization: `Bearer ${token}` } }
            );

            if (actionStr === 'COMPLETE') {
                alert("Booking marked as completed!");
            } else {
                alert(`Booking ${actionStr.toLowerCase()}ed successfully!`);
            }
            
            await fetchBookings();
        } catch (err) {
            alert(err.response?.data?.error?.message || "Failed to update booking status.");
        } finally {
            setProcessingId(null);
            setCurrentAction(null);
        }
    };

    const filteredBookings = bookings.filter(b => {
        if (activeTab === 'Requests') return b.status === 'PAID_PENDING';
        if (activeTab === 'Upcoming') return b.status === 'ACCEPTED';
        if (activeTab === 'History') return ['COMPLETED', 'REJECTED_REFUNDED', 'CANCELLED_REFUNDED'].includes(b.status);
        return true;
    });

    const isScheduleMet = (dateStr, timeStr) => {
        const now = new Date();
        const schedule = new Date(`${dateStr}T${timeStr}`);
        return now >= schedule;
    };

    // 🚀 Inline CSS Spinner Definition
    const Spinner = () => (
        <span className="btn-spinner" style={{
            display: 'inline-block',
            width: '12px',
            height: '12px',
            border: '2px solid rgba(255,255,255,0.3)',
            borderRadius: '50%',
            borderTopColor: '#fff',
            animation: 'spin 0.6s linear infinite',
            marginRight: '8px',
            verticalAlign: 'middle'
        }} />
    );

    const DarkSpinner = () => (
        <span className="btn-spinner" style={{
            display: 'inline-block',
            width: '12px',
            height: '12px',
            border: '2px solid rgba(0,0,0,0.1)',
            borderRadius: '50%',
            borderTopColor: '#000',
            animation: 'spin 0.6s linear infinite',
            marginRight: '8px',
            verticalAlign: 'middle'
        }} />
    );

    const styles = {
        page: { 
            padding: '40px', 
            fontFamily: 'Arial, sans-serif',
            height: '91vh',
            boxSizing: 'border-box',
            display: 'flex',
            flexDirection: 'column',
            overflow: 'hidden'
        },
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
            {/* Inject CSS keyframes for rotation animation */}
            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
            
            <h1 style={styles.title}>My Bookings</h1>

            <div style={styles.tabContainer}>
                {TABS.map(tab => (
                    <div 
                        key={tab} 
                        style={styles.tab(activeTab === tab)}
                        onClick={() => processingId === null && setActiveTab(tab)}
                    >
                        {tab}
                    </div>
                ))}
            </div>

            <div style={styles.scrollableArea}>
                {filteredBookings.length === 0 ? (
                    <p style={{ color: '#999' }}>No bookings found in this category.</p>
                ) : (
                    <div style={styles.cardList}>
                        {filteredBookings.map(booking => {
                            const isThisCard = processingId === booking.id;

                            return (
                                <div key={booking.id} style={styles.card}>
                                    <img 
                                        src={booking.serviceImage || 'https://via.placeholder.com/180x120?text=No+Image'} 
                                        alt={booking.serviceTitle} 
                                        style={styles.image} 
                                    />
                                    
                                    <div style={styles.contentArea}>
                                        <div style={styles.rowTop}>
                                            <h3 style={styles.serviceTitle}>{booking.serviceTitle}</h3>
                                            <div style={styles.dateTimeWrapper}>
                                                <p style={styles.dateTimeText}>{booking.scheduledDate}</p>
                                                <p style={styles.dateTimeText}>{booking.scheduledTime}</p>
                                            </div>
                                            <button 
                                                style={styles.detailsBtn} 
                                                disabled={processingId !== null}
                                                onClick={() => setSelectedBooking(booking)}
                                            >
                                                Details
                                            </button>
                                        </div>

                                        <div style={styles.rowMiddle}>
                                            <p style={styles.customerName}>
                                                {booking.customerName || "Customer Name"}
                                            </p>
                                        </div>

                                        <div style={styles.rowBottom}>
                                            <p style={styles.quantity}>x{booking.quantity}</p>
                                            
                                            <div>
                                                {/* Requests Tab */}
                                                {booking.status === 'PAID_PENDING' && (
                                                    <>
                                                        <button 
                                                            style={{ ...styles.rejectBtn, opacity: processingId !== null ? 0.6 : 1 }}
                                                            disabled={processingId !== null}
                                                            onClick={() => handleStatusUpdate(booking.id, 'REJECTED', 'REJECT')}
                                                        >
                                                            {isThisCard && currentAction === 'REJECT' && <DarkSpinner />}
                                                            Reject
                                                        </button>
                                                        <button 
                                                            style={{ ...styles.acceptBtn, opacity: processingId !== null ? 0.6 : 1 }}
                                                            disabled={processingId !== null}
                                                            onClick={() => handleStatusUpdate(booking.id, 'ACCEPTED', 'ACCEPT')}
                                                        >
                                                            {isThisCard && currentAction === 'ACCEPT' && <Spinner />}
                                                            Accept
                                                        </button>
                                                    </>
                                                )}

                                                {/* Upcoming Tab */}
                                                {booking.status === 'ACCEPTED' && (
                                                    <button 
                                                        style={{
                                                            ...styles.completeBtn,
                                                            backgroundColor: isScheduleMet(booking.scheduledDate, booking.scheduledTime) ? '#27ae60' : '#a5d6a7',
                                                            opacity: processingId !== null ? 0.6 : 1
                                                        }} 
                                                        disabled={processingId !== null || !isScheduleMet(booking.scheduledDate, booking.scheduledTime)}
                                                        onClick={() => handleStatusUpdate(booking.id, 'COMPLETED', 'COMPLETE')}
                                                    >
                                                        {isThisCard && currentAction === 'COMPLETE' && <Spinner />}
                                                        {isScheduleMet(booking.scheduledDate, booking.scheduledTime) ? 'Mark as Completed' : 'Locked (Schedule not met)'}
                                                    </button>
                                                )}

                                                {/* History Tab */}
                                                {['COMPLETED', 'REJECTED_REFUNDED', 'CANCELLED_REFUNDED'].includes(booking.status) && (
                                                    <span style={styles.statusBadge}>{booking.status.replace(/_/g, ' ')}</span>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>
            {selectedBooking && <BookingDetailsModal booking={selectedBooking} onClose={() => setSelectedBooking(null)} />}
        </div>
    );
}