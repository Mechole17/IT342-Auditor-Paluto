import { useState } from "react";
import { useNavigate } from 'react-router-dom';

const MEAL = {
    title: 'Family Feast',
    image: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600',
    quantity: 1,
    ingredientCost: 1000,
    prepTime: '1 hour',
    cookRate: 450,
};

export default function PaymentPage() {
    const [date, setDate] = useState('');
    const [time, setTime] = useState('');
    const [address, setAddress] = useState('');
    const [paymentMethod, setPaymentMethod] = useState('');

    const navigate = useNavigate();

    const Cancel = () => {
        window.history.back();
    }

    // date validation
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const minDate = tomorrow.toISOString().split('T')[0];

    const total = MEAL.ingredientCost + MEAL.cookRate;

    const [errors, setErrors] = useState({});

const handleSubmit = (e) => {
    e.preventDefault();

    const newErrors = {};
        if (!date) newErrors.date = 'Date is required';
        if (!time) newErrors.time = 'Time is required';
        if (!address.trim()) newErrors.address = 'Address is required';
        if (!paymentMethod || paymentMethod === '') newErrors.paymentMethod = 'Please select a payment method';

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        setErrors({});
        console.log({ date, time, address, paymentMethod });
        alert('Payment successful! For booking date: ' + date + ' at ' + time);
        // call your API here
        navigate('/customer/bookings');  // redirect to bookings page after payment
    };

    return (
        <div style={styles.wrapper}>
            <h2 style={styles.pageTitle}>Payment</h2>

            <div style={styles.content}>
                {/* Left Column */}
                <div style={styles.leftCol}>
                    <img src={MEAL.image} alt={MEAL.title} style={styles.image} />
                    <div style={styles.summaryBox}>
                        <div style={styles.summaryRow}>
                            <span style={styles.summaryLabel}>Quantity</span>
                            <span style={styles.summaryValue}>x{MEAL.quantity}</span>
                        </div>
                        <div style={styles.summaryRow}>
                            <span style={styles.summaryLabel}>Est. ingredient cost</span>
                            <span style={styles.summaryValue}>Php {MEAL.ingredientCost.toLocaleString()}</span>
                        </div>
                        <div style={styles.summaryRow}>
                            <span style={styles.summaryLabel}>Prep time</span>
                            <span style={styles.summaryValue}>{MEAL.prepTime}</span>
                        </div>
                        <div style={styles.summaryRow}>
                            <span style={styles.summaryLabel}>Cook rate/hr</span>
                            <span style={styles.summaryValue}>Php {MEAL.cookRate.toLocaleString()}</span>
                        </div>
                    </div>
                </div>

                {/* Right Column */}
                <div style={styles.rightCol}>
                    <h1 style={styles.mealTitle}>{MEAL.title}</h1>

                    <form onSubmit={handleSubmit} style={styles.form}>
                        {/* Date & Time */}
                        <div style={styles.dateTimeRow}>
                            <div style={styles.fieldGroupHalf}>
                                <label style={styles.label}>Date</label>
                                <input
                                    type="date"
                                    value={date}
                                    min={minDate}  // prevent selecting past dates
                                    onChange={e => setDate(e.target.value)}
                                    style={styles.input}
                                />
                                {errors.date && <span style={styles.errorText}>{errors.date}</span>}
                            </div>
                            <div style={styles.fieldGroupHalf}>
                                <label style={styles.label}>Time</label>
                                <input
                                    type="time"
                                    value={time}
                                    onChange={e => setTime(e.target.value)}
                                    style={styles.input}
                                />
                                {errors.time && <span style={styles.errorText}>{errors.time}</span>}
                            </div>
                        </div>

                        {/* Address */}
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Address</label>
                            <input
                                type="text"
                                value={address}
                                onChange={e => setAddress(e.target.value)}
                                style={styles.input}
                            />
                            {errors.address && <span style={styles.errorText}>{errors.address}</span>}
                        </div>

                        {/* Payment Method */}
                        <div style={styles.fieldGroup}>
                            <label style={styles.label}>Payment Method</label>
                            <select
                                value={paymentMethod}
                                onChange={e => setPaymentMethod(e.target.value)}
                                style={styles.input}
                            >
                                <option value="">Select payment method</option>
                                <option value="gcash">GCash</option>
                                <option value="card">Card</option>
                            </select>
                            {errors.paymentMethod && <span style={styles.errorText}>{errors.paymentMethod}</span>}
                        </div>

                        {/* Total */}
                        <div style={styles.totalRow}>
                            <span style={styles.totalLabel}>Total</span>
                            <span style={styles.totalAmount}>Php {total.toLocaleString()}</span>
                        </div>

                        {/* Buttons */}
                        <div style={styles.payRow}>
                            <button style={styles.cancelBtn} onClick={Cancel}>Cancel</button>
                            <button type="submit" style={styles.payBtn}>Pay now</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

const styles = {
    form: {
        display: 'flex',
        flexDirection: 'column',
        flex: 1,
    },
    inputError: {
        borderColor: 'red',
    },
    errorText: {
        fontSize: '12px',
        color: 'red',
        marginTop: '2px',
    },
    wrapper: {
        padding: '32px 40px',
        height: '100%',
        boxSizing: 'border-box',
        display: 'flex',
        flexDirection: 'column',
        overflow: 'hidden',
    },
    pageTitle: {
        fontSize: '22px',
        fontWeight: '800',
        margin: '0 0 24px',
        flexShrink: 0,
    },
    content: {
        display: 'flex',
        gap: '48px',
        flex: 1,
        overflow: 'hidden',
    },

    // Left
    leftCol: {
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        overflow: 'hidden',
    },
    image: {
        width: '100%',
        flex: 1,
        objectFit: 'cover',
        borderRadius: '12px',
        minHeight: 0,
    },
    summaryBox: {
        marginTop: '20px',
        display: 'flex',
        flexDirection: 'column',
        gap: '10px',
    },
    summaryRow: {
        display: 'flex',
        justifyContent: 'space-between',
    },
    summaryLabel: {
        fontSize: '14px',
        color: '#444',
    },
    summaryValue: {
        fontSize: '14px',
        fontWeight: '600',
    },

    // Right
    rightCol: {
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
    },
    mealTitle: {
        fontSize: '36px',
        fontWeight: '900',
        margin: '0 0 24px',
    },
    dateTimeRow: {
        display: 'flex',
        gap: '16px',
        marginBottom: '16px',
    },
    fieldGroup: {
        display: 'flex',
        flexDirection: 'column',
        gap: '6px',
        marginBottom: '16px',
    },
    fieldGroupHalf: {
        display: 'flex',
        flexDirection: 'column',
        gap: '6px',
        flex: 1,
    },
    label: {
        fontSize: '13px',
        color: '#555',
        fontWeight: '500',
    },
    input: {
        border: '1.5px solid #ccc',
        borderRadius: '8px',
        padding: '10px 14px',
        fontSize: '14px',
        outline: 'none',
        width: '100%',
        boxSizing: 'border-box',
        height: '42px',
        fontFamily: 'inherit',
    },

    // Total
    totalRow: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        borderTop: '1.5px solid #eee',
        paddingTop: '16px',
        marginTop: 'auto',
    },
    totalLabel: {
        fontSize: '16px',
        color: '#888',
        fontWeight: '500',
    },
    totalAmount: {
        fontSize: '28px',
        fontWeight: '800',
    },

    // Buttons
    payRow: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-end',
        gap: '12px',
        marginTop: '16px',
    },
    cancelBtn: {
        backgroundColor: 'transparent',
        color: '#333',
        border: '1.5px solid #ccc',
        borderRadius: '12px',
        padding: '12px 48px',
        fontSize: '16px',
        fontWeight: '700',
        cursor: 'pointer',
    },
    payBtn: {
        backgroundColor: '#F5A623',
        color: '#fff',
        border: 'none',
        borderRadius: '12px',
        padding: '12px 48px',
        fontSize: '16px',
        fontWeight: '700',
        cursor: 'pointer',
    },
};