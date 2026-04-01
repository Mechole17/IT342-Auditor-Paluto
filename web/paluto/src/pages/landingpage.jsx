import { useNavigate } from 'react-router-dom';
import ramen from '../asset/ramen.png';

export default function LandingPage() {
    const navigate = useNavigate();

    return (
        <div>
        <div style={styles.container}>
            {/* NOTE: We REMOVED <Navbar /> from here. 
               It is now globally handled in App.js 
            */}
            
            <div style={styles.hero}>
                <div style={{zIndex: 2}}>
                    <h1 style={styles.heroText}>
                        Professional<br/>Home cooking,<br/>Delivered at your<br/>table.
                    </h1>
                    <div style={styles.ctaContainer}>
                        <button style={styles.redBtn} onClick={() => navigate('/customer/register')}>
                            Find a cook
                        </button>
                        <button style={styles.lightBtn} onClick={() => navigate('/cook/register')}>
                            Join as a cook
                        </button>
                    </div>
                </div>
                <img src={ramen} alt="Ramen" style={styles.foodImage} />
            </div>
        </div>
        </div>
    );
}

const styles = {
    container: { height: '85vh', width: '100vw', margin: 0, padding: 0, overflow: 'hidden', fontFamily: 'Arial, sans-serif' },
    hero: { 
        height: 'calc(100vh - 75px)', 
        background: 'linear-gradient(to bottom, #FFB800 50%, #c18a01 100%)', 
        display: 'flex', alignItems: 'center', padding: '0 80px', position: 'relative' 
    },
    heroText: { color: '#fff', fontSize: '64px', fontWeight: 'bold', maxWidth: '600px', lineHeight: '1.1' },
    ctaContainer: { display: 'flex', gap: '20px', marginTop: '40px' },
    redBtn: { backgroundColor: '#E60000', color: '#fff', padding: '18px 45px', borderRadius: '15px', border: 'none', fontWeight: 'bold', cursor: 'pointer' },
    lightBtn: { backgroundColor: '#E5E5E5', color: '#000', padding: '18px 45px', borderRadius: '15px', border: 'none', fontWeight: 'bold', cursor: 'pointer' },
    foodImage: { position: 'absolute', right: '50px', width: '600px', height: '600px', objectFit: 'contain' },
};