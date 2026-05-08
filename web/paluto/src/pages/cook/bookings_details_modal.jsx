import React, { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';

export default function BookingDetailsModal({ booking: initialBooking, onClose }) {
    const { user } = useAuth(); 
    const [booking, setBooking] = useState(initialBooking);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        document.body.style.overflow = 'hidden';
        
        const fetchDetails = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await fetch(`http://localhost:8080/api/bookings/${initialBooking.id}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });
                const result = await response.json();
                if (result.success) {
                    setBooking(result.data);
                }
            } catch (error) {
                console.error("Error fetching booking details:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchDetails();

        return () => { document.body.style.overflow = 'unset'; };
    }, [initialBooking.id]);

    if (!booking) return null;

    const status = booking.status?.toUpperCase() || 'PENDING';

    // Logic: Identify the "other party" based on the logged-in user's role
    const isCook = user?.role === 'COOK';
    const counterpartyLabel = isCook ? "Customer" : "Cook";
    const counterpartyName = isCook ? booking.customerName : booking.cookName;

    const getStages = () => {
        const formatDate = (dateStr) => {
            if (!dateStr) return null;
            return new Date(dateStr).toLocaleString('en-PH', {
                month: 'short',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
            });
        };

        const baseStages = [
            { 
                label: 'Payment Confirmed', 
                desc: formatDate(booking.createdAt) || 'Booking is secured.' 
            },
        ];

        if (['REJECTED', 'REJECTED_REFUNDED', 'CANCELLED'].includes(status)) {
            return [
                ...baseStages,
                { 
                    label: 'Booking Rejected', 
                    desc: formatDate(booking.rejectedAt) || 'Service declined by cook.' 
                },
                { label: 'Refund Processed', desc: 'Payment returned to customer.' }
            ];
        }

        return [
            ...baseStages,
            { 
                label: 'Booking Accepted', 
                desc: formatDate(booking.acceptedAt) || 'Awaiting confirmation...' 
            },
            { 
                label: 'Service Completed', 
                desc: formatDate(booking.completedAt) || 'Ready for service date.' 
            }
        ];
    };

    const stages = getStages();
    const currentStep = status === 'PAID_PENDING' ? 0 : status === 'ACCEPTED' ? 1 : (status === 'COMPLETED' ? 2 : 0);

    return (
        <div style={styles.overlay} onClick={onClose}>
            <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
                <button style={styles.closeBtn} onClick={onClose}>✕</button>
                <h2 style={styles.title}>Booking Details</h2>
                
                {loading ? (
                    <div style={styles.loader}>Updating details...</div>
                ) : (
                    <div style={styles.mainLayout}>
                        <div style={{ flex: 1.5 }}>
                            <Section label="Service" value={`${booking.serviceTitle} (x${booking.quantity})`} />
                            
                            {/* Dynamically shows only the person you are dealing with */}
                            <Section label={counterpartyLabel} value={counterpartyName} />
                            
                            <Section label="Schedule" value={`${booking.scheduledDate} at ${booking.scheduledTime}`} />
                            <Section label="Address" value={booking.serviceAddress || "Cebu City, Philippines"} isAddress />
                            
                            <div style={{ marginTop: '24px' }}>
                                <span style={styles.label}>Transaction Amount</span>
                                <p style={styles.price}>₱{booking.totalAmount}</p>
                                {status.includes('REJECTED') && <p style={styles.refundText}>Status: Refunded</p>}
                            </div>
                        </div>

                        <div style={styles.timelineContainer}>
                            <p style={styles.label}>Status Timeline</p>
                            {stages.map((stage, index) => (
                                <div key={index} style={styles.stepRow}>
                                    <div style={styles.indicatorCol}>
                                        <div style={styles.circle(index <= currentStep, status.includes('REJECTED') && index > 0)} />
                                        {index !== stages.length - 1 && <div style={styles.line(index < currentStep)} />}
                                    </div>
                                    <div style={styles.textCol}>
                                        <p style={styles.stepLabel(index <= currentStep)}>{stage.label}</p>
                                        <p style={styles.stepDesc}>{stage.desc}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

const Section = ({ label, value, isAddress }) => {
    if (!value) return null;
    return (
        <div style={{ marginBottom: '16px' }}>
            <span style={styles.label}>{label}</span>
            <p style={{ ...styles.value, color: isAddress ? '#BA1313' : '#000' }}>{value}</p>
        </div>
    );
};

const styles = {
    overlay: { position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.6)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 2000 },
    modal: { backgroundColor: '#FCF8F2', borderRadius: '20px', padding: '40px', width: '750px', maxWidth: '90vw', border: '1px solid #D6CFC7', position: 'relative' },
    closeBtn: { position: 'absolute', top: '24px', right: '24px', background: 'none', border: 'none', fontSize: '22px', cursor: 'pointer', color: '#888' },
    title: { fontSize: '28px', fontWeight: 'bold', margin: '0 0 32px 0' },
    mainLayout: { display: 'flex', gap: '40px' },
    loader: { textAlign: 'center', padding: '40px', fontSize: '18px', color: '#888' },
    label: { fontSize: '11px', color: '#888', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '4px', display: 'block' },
    value: { fontSize: '17px', fontWeight: '600', margin: 0 },
    price: { fontSize: '24px', fontWeight: 'bold', color: '#27ae60', margin: 0 },
    refundText: { color: '#BA1313', fontWeight: 'bold', fontSize: '14px', marginTop: '4px' },
    timelineContainer: { flex: 1, backgroundColor: '#fff', padding: '20px', borderRadius: '12px', border: '1px solid #eee' },
    stepRow: { display: 'flex', gap: '12px', minHeight: '65px' },
    indicatorCol: { display: 'flex', flexDirection: 'column', alignItems: 'center' },
    circle: (active, isError) => ({ 
        width: '12px', height: '12px', borderRadius: '50%', 
        backgroundColor: active ? (isError ? '#BA1313' : '#27ae60') : '#ddd',
        border: active ? `3px solid ${isError ? '#ffcdd2' : '#c8e6c9'}` : 'none' 
    }),
    line: (active) => ({ width: '2px', flex: 1, backgroundColor: active ? '#27ae60' : '#ddd', margin: '4px 0' }),
    stepLabel: (active) => ({ margin: 0, fontSize: '14px', fontWeight: '700', color: active ? '#000' : '#aaa' }),
    stepDesc: { margin: '2px 0 0', fontSize: '12px', color: '#888' },
    textCol: { flex: 1 }
};