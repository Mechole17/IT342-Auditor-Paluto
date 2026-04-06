import { useState } from "react";

const BOOKINGS = [
    {
        id: 1,
        menu: 'Family Feast',
        image: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300',
        customer: 'John Doe',
        date: 'March 5, 2025',
        time: '4:30 PM',
        quantity: 1,
        status: 'active',
        paymentStatus: 'pending',
    },
    {
        id: 2,
        menu: 'Sushi Night',
        image: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=300',
        customer: 'Jane Smith',
        date: 'March 10, 2025',
        time: '7:00 PM',
        quantity: 2,
        status: 'completed',
        paymentStatus: 'paid',
    },
    {
        id: 3,
        menu: 'BBQ Grill',
        image: 'https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=300',
        customer: 'Bob Johnson',
        date: 'February 20, 2025',
        time: '6:00 PM',
        quantity: 3,
        status: 'rejected',
        paymentStatus: 'refunded',
    },
];

const TABS = ['Active', 'Completed', 'Rejected'];

export default function CustomerBookingsPage() {
    const [activeTab, setActiveTab] = useState('Active');

    const filtered = BOOKINGS.filter(b => b.status === activeTab.toLowerCase());

    return (
        <div style={styles.wrapper}>
            <h2 style={styles.pageTitle}>Bookings</h2>

            {/* Tabs */}
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

            {/* Booking Cards */}
            <div style={styles.list}>
                {filtered.length === 0 ? (
                    <p style={styles.empty}>No {activeTab.toLowerCase()} bookings.</p>
                ) : (
                    filtered.map(booking => (
                        <div key={booking.id} style={styles.card}>
                            <img src={booking.image} alt={booking.menu} style={styles.cardImg} />

                            <div style={styles.cardInfo}>
                                <p style={styles.menuName}>{booking.menu}</p>
                                <p style={styles.customer}>{booking.customer}</p>
                                <p style={styles.quantity}>x{booking.quantity}</p>
                            </div>

                            <div style={styles.cardMeta}>
                                <p style={styles.metaText}>{booking.date}</p>
                                <p style={styles.metaText}>{booking.time}</p>
                            </div>

                            <div style={styles.cardRight}>
                                <p style={styles.paymentStatus}>{booking.paymentStatus}</p>
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

    // Tabs
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

    // List
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

    // Card
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
        color: '#888',
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