import React, { useState, useEffect } from 'react'; // Added hooks
import { useNavigate } from 'react-router-dom';
import axios from 'axios'; // Ensure axios is installed

export default function CustomerHomePage() {
    const navigate = useNavigate();
    
    // 1. State for your live menu items
    const [menuItems, setMenuItems] = useState([]);
    const [loading, setLoading] = useState(true);

    // 2. Fetch data from Backend
    useEffect(() => {
        const fetchServices = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/services/all');
                
                // Uniformity check: response -> axios data -> our ApiResponse .data
                if (response.data.success) {
                    setMenuItems(response.data.data);
                }
            } catch (error) {
                console.error("Failed to fetch services:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchServices();
    }, []);

    if (loading) {
        return <div style={{ padding: '50px', textAlign: 'center' }}>Loading delicious meals...</div>;
    }

    return (
        <div>
            {/* Hero Section stays the same */}
            <div style={styles.hero}>
                <div style={styles.overlay} />
                <div style={styles.heroContent}>
                    <p style={styles.heroSubtitle}>Welcome</p>
                    <h1 style={styles.heroTitle}>Your Private Chef,<br />Just a Tap Away.</h1>
                    <button style={styles.heroBtn}>Book a Chef →</button>
                </div>
            </div>

            {/* Popular Offerings */}
            <div style={styles.offerings}>
                <h1 style={styles.offeringsTitle}>Popular Offerings</h1>
                <div style={styles.menuGrid}>
                    {menuItems.map((item) => (
                        <div
                            key={item.id} // Use the DB ID as key
                            style={{
                                ...styles.menuCard,
                                // Fallback image if the service doesn't have one
                                backgroundImage: `url('${item.imageUrl || 'https://via.placeholder.com/400x220?text=Paluto+Dish'}')`,
                            }}
                            // Pass the actual service ID to the details page
                            onClick={() => navigate(`/customer/service-details/${item.id}`)}
                        >
                            <div style={styles.menuCardOverlay} />

                            <div style={styles.menuCardContent}>
                                {/* Map 'category' to your 'dishType' or similar field */}
                                {/* <div style={styles.menuCategory}>{item.category || 'Home Cooked'}</div> */}
                                <h3 style={styles.menuTitle}>{item.title}</h3>
                                {/* Assuming ingredientsCost is your base price */}
                                <p style={styles.menuPrice}>₱{item.ingredientsCost}<span style={styles.menuPriceSub}> + Labor</span></p>
                            </div>
                        </div>
                    ))}
                </div>
                
                {menuItems.length === 0 && <p>No offerings available at the moment.</p>}

                <div style={styles.seeMoreWrapper}>
                    <button style={styles.seeMoreBtn}>See More →</button>
                </div>
            </div>
        </div>
    );
}

// ... styles stay the same

const styles = {
    hero: {
        position: 'relative',
        width: '100%',
        height: '50vh',
        backgroundImage: `url('https://static.vecteezy.com/system/resources/previews/031/734/445/non_2x/spices-and-herbs-on-dark-background-food-and-cuisine-ingredients-colorful-collection-spices-and-herbs-on-background-black-table-ai-generated-free-photo.jpg')`,
        backgroundSize: 'cover',
        backgroundPosition: 'center 60%',
        overflow: 'hidden',
        display: 'flex',
        alignItems: 'center',
    },
    overlay: {
        position: 'absolute',
        inset: 0,
        background: 'linear-gradient(90deg, rgba(0,0,0,0.72) 40%, rgba(0,0,0,0.1) 100%)',
    },
    heroContent: {
        position: 'relative',
        zIndex: 1,
        padding: '0 36px',
    },
    heroSubtitle: {
        margin: '0 0 6px',
        color: '#FFD700',
        fontSize: '12px',
        fontWeight: '700',
        letterSpacing: '0.15em',
        textTransform: 'uppercase',
    },
    heroTitle: {
        margin: '0 0 20px',
        color: '#fff',
        fontSize: '64px',
        fontWeight: '800',
        lineHeight: 1.25,
    },
    heroBtn: {
        backgroundColor: '#FFD700',
        color: '#1a1a1a',
        border: 'none',
        borderRadius: '8px',
        padding: '12px 24px',
        fontWeight: '700',
        fontSize: '14px',
        cursor: 'pointer',
        letterSpacing: '0.03em',
    },

    // Offerings
    offerings: {
        padding: '32px 36px',
    },
    offeringsTitle: {
        margin: '0 0 20px',
        fontSize: '24px',
        fontWeight: '800',
    },
    menuGrid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(3, 1fr)',
        gap: '16px',
    },

    // Menu Card
    menuCard: {
        position: 'relative',
        height: '220px',
        borderRadius: '14px',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        overflow: 'hidden',
        cursor: 'pointer',
        display: 'flex',
        alignItems: 'flex-end',
    },
    menuCardOverlay: {
        position: 'absolute',
        inset: 0,
        background: 'linear-gradient(180deg, rgba(0,0,0,0.0) 30%, rgba(0,0,0,0.78) 100%)',
    },
    menuCardContent: {
        position: 'relative',
        zIndex: 1,
        padding: '16px',
        width: '100%',
    },
    menuCategory: {
        display: 'inline-block',
        backgroundColor: '#FFD700',
        color: '#1a1a1a',
        fontSize: '10px',
        fontWeight: '700',
        letterSpacing: '0.1em',
        textTransform: 'uppercase',
        padding: '3px 8px',
        borderRadius: '4px',
        marginBottom: '6px',
    },
    menuTitle: {
        margin: '0 0 4px',
        color: '#fff',
        fontSize: '17px',
        fontWeight: '700',
    },
    menuPrice: {
        margin: 0,
        color: '#FFD700',
        fontSize: '16px',
        fontWeight: '700',
    },
    menuPriceSub: {
        fontSize: '11px',
        fontWeight: '400',
        color: '#ccc',
        marginLeft: '2px',
    },
    seeMoreWrapper: {
    display: 'flex',
    justifyContent: 'center',
    marginTop: '24px',
    },
    seeMoreBtn: {
        backgroundColor: 'transparent',
        color: '#1a1a1a',
        border: '2px solid #1a1a1a',
        borderRadius: '8px',
        padding: '12px 32px',
        fontWeight: '700',
        fontSize: '14px',
        cursor: 'pointer',
        letterSpacing: '0.03em',
    },
};