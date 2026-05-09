import { useState, useEffect } from "react";
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

export default function MealDetails() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [service, setService] = useState(null);
    const [loading, setLoading] = useState(true);
    const [quantity, setQuantity] = useState(1);

    useEffect(() => {
        const fetchService = async () => {
            try {
                const res = await axios.get(`http://localhost:8080/api/services/${id}`);
                if (res.data.success) setService(res.data.data);
                console.log("Fetched service details:", res.data);
            } catch (err) { console.error(err); }
            finally { setLoading(false); }
        };
        fetchService();
    }, [id]);

    const handleBookNow = () => {
        // We pass the WHOLE service object + selected quantity
        navigate('/customer/service-payment', { 
            state: { service, quantity } 
        });
    };

    if (loading) return <div>Loading...</div>;
    if (!service) return <div>Meal not found</div>;

    return (
        <div style={styles.wrapper}>
            <h2 style={styles.pageTitle}>Meal details</h2>
            <div style={styles.content}>
                <div style={styles.leftCol}>
                    <img src={service.imageUrl} alt={service.title} style={styles.image} />
                    <div style={styles.ingredientsBox}>
                        <h3 style={styles.ingredientsTitle}>Ingredients:</h3>
                        <p style={styles.ingredientLine}>{service.ingredientsList}</p>
                    </div>
                </div>
                <div style={styles.rightCol}>
                    <h1 style={styles.mealTitle}>{service.title}</h1>
                    <p style={styles.description}>{service.description}</p>
                    <div style={styles.metaRow}>
                        <div>
                            <p style={styles.metaLabel}>Est. ingredient cost</p>
                            <p style={styles.metaValue}>Php {service.ingredientsCost.toLocaleString()}</p>
                        </div>
                        <div>
                            <p style={styles.metaLabel}>Prep time</p>
                            <p style={styles.metaValue}>{service.estPrepTime} mins</p>
                        </div>
                    </div>
                    <div style={styles.quantityRow}>
                        <button style={styles.qtyBtn} onClick={() => setQuantity(Math.max(1, quantity - 1))}>−</button>
                        <span style={styles.qtyValue}>{quantity}</span>
                        <button style={styles.qtyBtn} onClick={() => setQuantity(quantity + 1)}>+</button>
                    </div>
                    <div style={styles.bookWrapper}>
                        <button style={styles.bookBtn} onClick={handleBookNow}>Book</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

const styles = {
    wrapper: {
        padding: '32px 40px',
        height: '100%',
        boxSizing: 'border-box',
        overflow: 'hidden',
        display: 'flex',
        flexDirection: 'column',
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
        alignItems: 'flex-start',
        flex: 1,
        overflow: 'hidden',
    },

    // Left
    leftCol: {
        flex: 1,
        height: '100%',
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
    ingredientsBox: {
        marginTop: '20px',
    },
    ingredientsTitle: {
        fontSize: '16px',
        fontWeight: '700',
        margin: '0 0 8px',
    },
    ingredientLine: {
        margin: '4px 0',
        fontSize: '13px',
        color: '#333',
        lineHeight: 1.5,
    },
    ingredientKey: {
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
        margin: '0 0 12px',
    },
    description: {
        fontSize: '15px',
        color: '#444',
        lineHeight: 1.6,
        margin: '0 0 28px',
    },
    metaRow: {
        display: 'flex',
        gap: '48px',
        marginBottom: '24px',
    },
    metaLabel: {
        margin: '0 0 4px',
        fontSize: '13px',
        color: '#888',
    },
    metaValue: {
        margin: 0,
        fontSize: '26px',
        fontWeight: '800',
    },

    // Quantity
    quantityRow: {
        display: 'flex',
        alignItems: 'center',
        gap: '0',
        marginTop: '8px',
        marginBottom: '32px',
        width: 'fit-content',
        border: '1.5px solid #ccc',
        borderRadius: '8px',
        overflow: 'hidden',
    },
    qtyBtn: {
        width: '40px',
        height: '40px',
        background: '#f5f5f5',
        border: 'none',
        fontSize: '18px',
        fontWeight: '700',
        cursor: 'pointer',
        color: '#333',
    },
    qtyValue: {
        width: '48px',
        textAlign: 'center',
        fontSize: '16px',
        fontWeight: '600',
        borderLeft: '1.5px solid #ccc',
        borderRight: '1.5px solid #ccc',
        lineHeight: '40px',
    },

    // Book
    bookWrapper: {
        display: 'flex',
        justifyContent: 'flex-end',
    },
    bookBtn: {
        backgroundColor: '#ec9812',
        color: '#000000',
        border: 'none',
        borderRadius: '12px',
        padding: '16px 64px',
        fontSize: '18px',
        fontWeight: '700',
        cursor: 'pointer',
    },
};