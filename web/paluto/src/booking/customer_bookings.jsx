import React, { useState, useEffect } from "react";
import axios from "axios";
import { API_BASE_URL } from '../core/api.js';
import BookingDetailsModal from "./bookings_details_modal";
const TABS = ['Active', 'Completed', 'Rejected', 'Cancelled'];

export default function CustomerBookingsPage() {
    const [activeTab, setActiveTab] = useState('Active');
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    
    
    const [selectedBooking, setSelectedBooking] = useState(null);

    const [cancellingId, setCancellingId] = useState(null);

    // Add state for rating modal
    const [showRatingModal, setShowRatingModal] = useState(false);
    const [ratingBooking, setRatingBooking] = useState(null);
    const [ratingValue, setRatingValue] = useState(0);
    const [ratingComment, setRatingComment] = useState('');
    const [hasRated, setHasRated] = useState({});

    useEffect(() => {
        const completedBookings = bookings.filter(b => b.status === 'COMPLETED');
        completedBookings.forEach(b => checkIfRated(b.id));
    }, [bookings]);

    // Check if booking has been rated
    const checkIfRated = async (bookingId) => {
        const token = localStorage.getItem('token');
        try {
            const res = await axios.get(
                `${API_BASE_URL}/api/ratings/check/${bookingId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setHasRated(prev => ({ ...prev, [bookingId]: res.data.data }));
        } catch (err) {
            console.error("Failed to check rating", err);
        }
    };

    const handleSubmitRating = async () => {
        const token = localStorage.getItem('token');
        if (ratingValue === 0) {
            alert("Please select a star rating.");
            return;
        }
        try {
            await axios.post(
                `${API_BASE_URL}/api/ratings/submit`,
                { bookingId: ratingBooking.id, rating: ratingValue, comment: ratingComment },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            alert("Rating submitted successfully!");
            setShowRatingModal(false);
            setRatingValue(0);
            setRatingComment('');
            setHasRated(prev => ({ ...prev, [ratingBooking.id]: true }));
        } catch (err) {
            alert(err.response?.data?.error?.message || "Failed to submit rating.");
        }
    };

    const handleCancel = async (id) => {
        const token = localStorage.getItem('token');
        const confirm = window.confirm("Are you sure you want to cancel this booking? You will be refunded.");
        if (!confirm) return;

        try {
            setCancellingId(id);
            await axios.put(
                `${API_BASE_URL}/api/bookings/${id}/cancel-booking`,
                {}, 
                { headers: { Authorization: `Bearer ${token}` } }
            );
            alert("Booking cancelled successfully. Your refund will be processed.");
            fetchBookings();
        } catch (err) {
            alert(err.response?.data?.error?.message || "Failed to cancel booking.");
        } finally {
            setCancellingId(null);
        }
    };

    const fetchBookings = async () => {
        try {
            const userStr = localStorage.getItem('userData');
            const token = localStorage.getItem('token');
            
            if (!userStr || !token) {
                setLoading(false);
                return;
            }

            const userData = JSON.parse(userStr);
            const userId = userData.id;

            // Updated this to the general customer endpoint
            const response = await axios.get(`${API_BASE_URL}/api/bookings/customer/${userId}`, {
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

    useEffect(() => {
        fetchBookings();
    }, []);

    useEffect(() => {
        const pendingBooking = sessionStorage.getItem('pendingBooking');
        if (!pendingBooking) return;

        const createBooking = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await axios.post(
                    `${API_BASE_URL}/api/bookings/create`,
                    JSON.parse(pendingBooking),
                    { headers: { 'Authorization': `Bearer ${token}` } }
                );

                if (response.data.success) {
                    alert('🎉 Booking confirmed!');
                    fetchBookings(); // Refresh list after creation
                }
            } catch (error) {
                alert('Booking creation failed. Please contact support.');
            } finally {
                sessionStorage.removeItem('pendingBooking');
            }
        };
        createBooking();
    }, []);

    const filtered = bookings.filter(b => {
        const status = b.status ? b.status.toLowerCase() : '';
        if (activeTab === 'Active') return ['paid_pending', 'accepted'].includes(status);
        if (activeTab === 'Completed') return status === 'completed';
        if (activeTab === 'Cancelled') return status === 'cancelled_refunded';
        if (activeTab === 'Rejected') return status === 'rejected_refunded';
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
                                    color: booking.status.includes('PENDING') ? '#ec9812' 
                                        : booking.status.includes('COMPLETED') ? '#28a745'
                                        : booking.status.includes('REJECTED') || booking.status.includes('CANCELLED') ? '#d10b04'
                                        : '#28a745'
                                }}>
                                    {booking.status.replace(/_/g, ' ')}
                                </p>

                                {booking.status === 'PAID_PENDING' && (
                                    <button
                                        style={{ ...styles.detailsBtn, backgroundColor: '#fff', color: '#d10b04', border: '1.5px solid #d10b04' }}
                                        onClick={() => handleCancel(booking.id)}
                                        disabled={cancellingId === booking.id} // 🚀 Prevent double submittal clicks
                                        >
                                            {cancellingId === booking.id ? "Processing..." : "Cancel"}
                                    </button>
                                )}

                                {booking.status === 'COMPLETED' && (
                                    <button
                                        style={{
                                            ...styles.detailsBtn,
                                            backgroundColor: hasRated[booking.id] ? '#ccc' : '#F5A623',
                                            cursor: hasRated[booking.id] ? 'not-allowed' : 'pointer'
                                        }}
                                        disabled={hasRated[booking.id]}
                                        onClick={() => {
                                            setRatingBooking(booking);
                                            setShowRatingModal(true);
                                            checkIfRated(booking.id);
                                        }}
                                    >
                                        {hasRated[booking.id] ? 'Rated' : 'Rate'}
                                    </button>
                                )}

                                {showRatingModal && ratingBooking && (
                                    <div style={{
                                        position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)',
                                        display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000
                                    }}>
                                        <div style={{
                                            backgroundColor: '#fff', borderRadius: '20px', padding: '40px',
                                            width: '420px', position: 'relative'
                                        }}>
                                            <button style={{
                                                position: 'absolute', top: '16px', right: '20px',
                                                background: 'none', border: 'none', fontSize: '22px', cursor: 'pointer'
                                            }} onClick={() => setShowRatingModal(false)}>✕</button>

                                            <h2 style={{ marginTop: 0 }}>Rate your Cook!</h2>
                                            <p style={{ color: '#000000' }}>Cook {ratingBooking.cookName}</p>

                                            {/* Star Rating */}
                                            <div style={{ display: 'flex', gap: '8px', marginBottom: '16px', justifyContent: 'center' }}>
                                                {[1, 2, 3, 4, 5].map(star => (
                                                    <span
                                                        key={star}
                                                        style={{ fontSize: '64px', cursor: 'pointer', color: star <= ratingValue ? '#F5A623' : '#ddd' }}
                                                        onClick={() => setRatingValue(star)}
                                                    >
                                                        ★
                                                    </span>
                                                ))}
                                            </div>

                                            <textarea
                                                placeholder="Leave a comment (optional)..."
                                                style={{
                                                    width: '100%', padding: '12px', borderRadius: '12px',
                                                    border: '1.5px solid #ccc', fontSize: '14px', height: '80px',
                                                    resize: 'none', boxSizing: 'border-box', marginBottom: '16px'
                                                }}
                                                value={ratingComment}
                                                onChange={(e) => setRatingComment(e.target.value)}
                                            />

                                            <button
                                                style={{
                                                    width: '100%', backgroundColor: '#F5A623', color: '#fff',
                                                    border: 'none', borderRadius: '12px', padding: '14px',
                                                    fontWeight: '700', fontSize: '16px', cursor: 'pointer'
                                                }}
                                                onClick={handleSubmitRating}
                                            >
                                                Submit Rating
                                            </button>
                                        </div>
                                    </div>
                                )}
                                
                                {/* === TRIGGER MODAL === */}
                                <button 
                                    style={styles.detailsBtn}
                                    onClick={() => setSelectedBooking(booking)}
                                >
                                    Details
                                </button>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* === RENDER MODAL === */}
            {selectedBooking && (
                <BookingDetailsModal 
                    booking={selectedBooking} 
                    onClose={() => setSelectedBooking(null)} 
                />
            )}
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