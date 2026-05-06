import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';

// MUI Calendar Imports
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import dayjs from 'dayjs';

export default function PaymentPage() {
    const navigate = useNavigate();
    const location = useLocation();

    // 1. Data from DTO (Sent from MealDetails)
    const { service, quantity } = location.state || {};

    const [selectedDate, setSelectedDate] = useState(null);
    const [bookedDates, setBookedDates] = useState([]); // Array of YYYY-MM-DD strings
    const [time, setTime] = useState('');
    const [address, setAddress] = useState('');
    const [paymentMethod, setPaymentMethod] = useState('');
    const [errors, setErrors] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);

    // !! FETCH BUSY DATES
    useEffect(() => {
    // We create a helper function inside the effect
    const fetchDates = async () => {
            try {
                const token = localStorage.getItem('token');
                const res = await axios.get(`http://localhost:8080/api/bookings/cooks/${service.cookId}/booked-dates`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                // Unwrap the ApiResponse
                if (res.data && res.data.data) {
                    setBookedDates(res.data.data);
                }
            } catch (err) {
                // Handle error (e.g., logging or setting an error state)
                console.error("Failed to load booked dates", err);
                setBookedDates([]); 
            }
        };

        if (service?.cookId) {
            fetchDates();
        }
    }, [service?.cookId]);

    // !! GREY-OUT LOGIC
    const shouldDisableDate = (date) => {
        const dateString = date.format('YYYY-MM-DD');
        const isPast = date.isBefore(dayjs(), 'day');
        const isBooked = bookedDates.includes(dateString);
        return isPast || isBooked; // Disable if past or already booked
    };

    const minDate = dayjs().add(1, 'day');

    const Cancel = () => {
        window.history.back();
    };

    // --- Official Paluto Logic ---
    const ingredientCostPerSet = Number(service?.ingredientsCost) || 0;
    const cookRatePerHour      = Number(service?.cookHourlyRate) || 0;
    const basePrepTime         = Number(service?.estPrepTime) || 0;
    const qty                  = Number(quantity) || 1;

    // Smart Scaling: 1st set = 100% time, each extra set adds 20% more time
    const totalPrepTimeMinutes = qty > 1 
        ? basePrepTime + (basePrepTime * 0.20 * (qty - 1)) 
        : basePrepTime;

    const totalIngredients = ingredientCostPerSet * qty;
    const laborCost        = (totalPrepTimeMinutes / 60) * cookRatePerHour;
    const total            = totalIngredients + laborCost;

    const handleSubmit = async (e) => {
        e.preventDefault();

        const newErrors = {};
        if (!selectedDate) newErrors.date = 'Date is required';
        if (!time) newErrors.time = 'Time is required';
        if (!address.trim()) newErrors.address = 'Address is required';
        if (!paymentMethod || paymentMethod === '') newErrors.paymentMethod = 'Please select a payment method';

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        setErrors({});
        setIsSubmitting(true);

        try {
            const token = localStorage.getItem('token');
            const payload = {
                serviceId: service.id,
                quantity: qty,
                serviceAddress: address,
                scheduledDate: selectedDate.format('YYYY-MM-DD'),
                scheduledTime: time + ":00"
            };

            const response = await axios.post('http://localhost:8080/api/bookings/create', payload, {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.data.success) {
                alert('Payment successful! For booking date: ' + selectedDate.format('YYYY-MM-DD') + ' at ' + time);
                navigate('/customer/bookings');
            }
        } catch (error) {
            alert(error.response?.data?.error?.message || "Booking failed. Please try again.");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (!service) return <div style={styles.wrapper}><h2>No service data found.</h2></div>;

    return (
        <LocalizationProvider dateAdapter={AdapterDayjs}>
        <div style={styles.wrapper}>
            <h2 style={styles.pageTitle}>Booking</h2>

            <div style={styles.content}>
                {/* Left Column */}
                <div style={styles.leftCol}>
                    <img src={service.imageUrl} alt={service.title} style={styles.image} />
                    <div style={styles.summaryBox}>
                        <div style={styles.summaryRow}>
                            <span style={styles.summaryLabel}>Quantity</span>
                            <span style={styles.summaryValue}>x{qty}</span>
                        </div>
                        <div style={styles.summaryRow}>
                            <span style={styles.summaryLabel}>Est. ingredient cost</span>
                            <span style={styles.summaryValue}>
                                Php {totalIngredients.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                            </span>
                        </div>
                        <div style={styles.summaryRow}>
                            <span style={styles.summaryLabel}>Prep time</span>
                            <span style={styles.summaryValue}>{Math.round(totalPrepTimeMinutes)} mins</span>
                        </div>
                        <div style={styles.summaryRow}>
                            <span style={styles.summaryLabel}>Cook rate/hr</span>
                            <span style={styles.summaryValue}>
                                Php {cookRatePerHour.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                            </span>
                        </div>
                    </div>
                </div>

                {/* Right Column */}
                <div style={styles.rightCol}>
                    <h1 style={styles.mealTitle}>{service.title}</h1>

                    <form onSubmit={handleSubmit} style={styles.form}>
                        {/* Date & Time */}
                        <div style={styles.dateTimeRow}>
                            <div style={styles.fieldGroupHalf}>
                                <label style={styles.label}>Date</label>
                                <DatePicker
                                        value={selectedDate}
                                        onChange={(val) => setSelectedDate(val)}
                                        minDate={minDate}
                                        shouldDisableDate={shouldDisableDate} // !! GREY OUT CALL
                                        slotProps={{ textField: { size: 'small', sx: styles.muiInput } }}
                                    />
                                {errors.date && <span style={styles.errorText}>{errors.date}</span>}
                            </div>
                            <div style={styles.fieldGroupHalf}>
                                <label style={styles.label}>Time</label>
                                <TimePicker
                                    value={time ? dayjs(time, 'HH:mm') : null} // Convert string back to dayjs for MUI
                                    onChange={(newValue) => setTime(newValue ? newValue.format('HH:mm') : '')}
                                    slotProps={{
                                        textField: {
                                            size: 'small',
                                            sx: styles.muiInput // Reuse the same styles as the DatePicker!
                                        }
                                    }}
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
                            <span style={styles.totalAmount}>
                                Php {total.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                            </span>
                        </div>

                        {/* Buttons */}
                        <div style={styles.payRow}>
                            <button 
                                type="button" 
                                style={styles.cancelBtn} 
                                onClick={Cancel}
                            >
                                Cancel
                            </button>
                            <button 
                                type="submit" 
                                style={styles.payBtn} 
                                disabled={isSubmitting}
                            >
                                {isSubmitting ? 'Processing...' : 'Pay now'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        </LocalizationProvider>
    );
}

const styles = {
    muiInput: {
        '& .MuiOutlinedInput-root': {
            borderRadius: '8px',
            height: '42px',
            backgroundColor: 'white',
            fontSize: '14px'
        },
        // !! THIS PART MAKES THE CALENDAR POPUP VISIBLY GREY OUT DISABLED DATES
        '& .MuiPickersDay-root.Mui-disabled': {
            color: '#ccc !important',
            backgroundColor: '#f5f5f5 !important',
            textDecoration: 'line-through'
        }
    },
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