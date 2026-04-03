export default function CookHomePage() {
    const KPI = [
        { label: 'Total Orders', value: 120 },
        { label: 'Upcoming Bookings', value: 5 },
        { label: 'Average Rating', value: 4.8 },
    ];

    const active_bookings = [
    { img: 'https://www.italiarail.com/sites/default/files/styles/poproutes_main/public/2019-12/feast%20of%207%20fishes%20-%20shutterstock_1230950803_Fotor.jpg',menu: 'Italian Feast', date: '2024-07-15', time: '18:00', customer: 'John Doe', quantity: 2 },
    { img: 'https://mondrianhotels.com/wp-content/uploads/sites/34/2024/06/mondrian-doha-events-steak-and-sushi-1920x1280-1.jpg', menu: 'Sushi Night', date: '2024-07-20', time: '19:30', customer: 'Jane Smith', quantity: 4 },
    ];

    return (
        <div style={styles.wrapper}>
            <h1>Dashboard</h1>
            <div style={styles.row}>
                {KPI.map((kpi, index) => (
                    <div key={index} style={styles.card}>
                        <p style={styles.label}>{kpi.label}</p>
                        <p style={styles.value}>{kpi.value}</p>
                    </div>
                ))}
            </div>

            <div>
                <h1>Active Bookings</h1>
                {active_bookings.map((booking, index) => (
                    <div key={index} style={styles.bookingCard}>
                        {/* Image */}
                        <img
                            src={booking.img}
                            alt={booking.menu}
                            style={styles.bookingImg}
                        />

                        {/* Menu + Customer */}
                        <div style={styles.bookingInfo}>
                            <p style={styles.bookingMenu}>{booking.menu}</p>
                            <p style={styles.bookingCustomer}>{booking.customer}</p>
                        </div>

                        {/* Qty */}
                        <div style={styles.bookingCol}>
                            <p style={styles.bookingColLabel}>Qty</p>
                            <p style={styles.bookingColValue}>{booking.quantity}</p>
                        </div>

                        {/* Date */}
                        <div style={styles.bookingCol}>
                            <p style={styles.bookingColLabel}>Date</p>
                            <p style={styles.bookingColValue}>{booking.date}</p>
                        </div>

                        {/* Time */}
                        <div style={styles.bookingCol}>
                            <p style={styles.bookingColLabel}>Time</p>
                            <p style={styles.bookingColValue}>{booking.time}</p>
                        </div>

                        {/* Details Button */}
                        <button style={styles.detailsBtn}>Details</button>
                    </div>
                ))}
            </div>
        </div>
    );
}

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