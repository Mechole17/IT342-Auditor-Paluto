import { useNavigate } from 'react-router-dom';

const MENU_ITEMS = [
    { title: 'Italian Feast', category: 'Italian', price: 120, image: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400' },
    { title: 'Sushi Night', category: 'Japanese', price: 95, image: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400' },
    { title: 'BBQ Grill', category: 'American', price: 85, image: 'https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=400' },
    { title: 'Tapas & Wine', category: 'Spanish', price: 75, image: 'https://images.unsplash.com/photo-1534080564583-6be75777b70a?w=400' },
    { title: 'Thai Classics', category: 'Thai', price: 70, image: 'https://images.unsplash.com/photo-1569562211093-4ed0d0758f12?w=400' },
    { title: 'French Bistro', category: 'French', price: 130, image: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400' },
];

export default function CustomerHomePage() {
    const navigate = useNavigate();
    return (
        <div>
            {/* Hero Section */}
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
                    {MENU_ITEMS.map((item, index) => (
                        <div
                            key={index}
                            style={{
                                ...styles.menuCard,
                                backgroundImage: `url('${item.image}')`,
                            }}

                            onClick={()=> navigate('/customer/service-details')}
                        >
                            {/* Dark gradient at bottom for text */}
                            <div style={styles.menuCardOverlay} />

                            <div style={styles.menuCardContent}>
                                <div style={styles.menuCategory}>{item.category}</div>
                                <h3 style={styles.menuTitle}>{item.title}</h3>
                                <p style={styles.menuPrice}>${item.price}<span style={styles.menuPriceSub}>/person</span></p>
                            </div>
                        </div>
                    ))}
                </div>
                <div style={styles.seeMoreWrapper}>
                    <button style={styles.seeMoreBtn}>See More →</button>
                </div>
            </div>
        </div>
    );
}

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