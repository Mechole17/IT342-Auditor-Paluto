import { useState } from "react";
import { useNavigate } from 'react-router-dom';

const MEAL = {
    title: 'Family Feast',
    description: 'A combination of common dishes served in most feast events in the Philippines.',
    ingredientCost: 1000,
    prepTime: '1 hour',
    image: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600',
    ingredients: {
        Protein: '1kg Pork Belly, 500g Chicken Thighs.',
        Vegetables: '1/4kg Squash, 100g Bitter Melon (Ampalaya), String Beans, Eggplant.',
        'Pantry Staples': 'Soy Sauce, Cane Vinegar, Peppercorns, Bay Leaves, Garlic, Shrimp Paste (Bagoong)',
    },
};

export default function MealDetails() {
    const [quantity, setQuantity] = useState(1);
    const navigate = useNavigate();

    const decrement = () => setQuantity(q => Math.max(1, q - 1));
    const increment = () => setQuantity(q => q + 1);

    return (
        <div style={styles.wrapper}>
            <h2 style={styles.pageTitle}>Meal details</h2>

            <div style={styles.content}>
                {/* Left Column */}
                <div style={styles.leftCol}>
                    <img src={MEAL.image} alt={MEAL.title} style={styles.image} />
                    <div style={styles.ingredientsBox}>
                        <h3 style={styles.ingredientsTitle}>Ingredients:</h3>
                        {Object.entries(MEAL.ingredients).map(([key, val]) => (
                            <p key={key} style={styles.ingredientLine}>
                                <span style={styles.ingredientKey}>{key}:</span> {val}
                            </p>
                        ))}
                    </div>
                </div>

                {/* Right Column */}
                <div style={styles.rightCol}>
                    <h1 style={styles.mealTitle}>{MEAL.title}</h1>
                    <p style={styles.description}>{MEAL.description}</p>

                    <div style={styles.metaRow}>
                        <div>
                            <p style={styles.metaLabel}>Est. ingredient cost</p>
                            <p style={styles.metaValue}>Php {MEAL.ingredientCost.toLocaleString()}</p>
                        </div>
                        <div>
                            <p style={styles.metaLabel}>Prep time</p>
                            <p style={styles.metaValue}>{MEAL.prepTime}</p>
                        </div>
                    </div>

                    <p style={styles.metaLabel}>Quantity</p>
                    <div style={styles.quantityRow}>
                        <button style={styles.qtyBtn} onClick={decrement}>−</button>
                        <span style={styles.qtyValue}>{quantity}</span>
                        <button style={styles.qtyBtn} onClick={increment}>+</button>
                    </div>

                    <div style={styles.bookWrapper}>
                        <button style={styles.bookBtn} onClick={()=> navigate('/customer/service-payment')}>Book</button>
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