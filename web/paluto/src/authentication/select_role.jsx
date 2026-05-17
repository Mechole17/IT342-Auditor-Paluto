import { useNavigate, useSearchParams } from 'react-router-dom';

export default function SelectRole() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const googleData = {
        email: searchParams.get('email'),
        firstName: searchParams.get('firstName'),
        lastName: searchParams.get('lastName')
    };

    const handleChoice = (role) => {
        if (role === 'CUSTOMER') {
            navigate('/register-customer-details', { state: googleData });
        } else {
            navigate('/register-cook-details', { state: googleData });
        }
    };

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', backgroundColor: '#fff' }}>
            <div style={styles.whiteBox}>
                <h1 style={styles.title}>How will you use PALUTO</h1>
                <p style={styles.subtitle}>Choose account type</p>

                <div style={styles.cardContainer}>
                    {/* CUSTOMER CARD */}
                    <div style={styles.card} onClick={() => handleChoice('CUSTOMER')}>
                        <div>
                            <h2 style={styles.roleTitle}>Customer</h2>
                            <p style={styles.description}>I want to find and book professional home cooks.</p>
                        </div>
                        <button style={styles.lightBtn}>Select</button>
                    </div>

                    {/* COOK CARD */}
                    <div style={styles.card} onClick={() => handleChoice('COOK')}>
                        <div>
                            <h2 style={styles.roleTitle}>Cook</h2>
                            <p style={styles.description}>I want to offer my culinary services to families.</p>
                        </div>
                        <button style={styles.redBtn}>Select</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

const styles = {
    whiteBox: {
        textAlign: 'center',
        width: '100%',
        maxWidth: '800px',
        fontFamily: 'Arial, sans-serif'
    },
    title: { 
        fontSize: '40px', 
        fontWeight: '900', 
        color: '#0A0A1F', 
        marginBottom: '8px',
    },
    subtitle: {
        fontSize: '16px',
        color: '#888',
        marginBottom: '40px',
        fontWeight: '500'
    },
    cardContainer: { 
        display: 'flex', 
        justifyContent: 'center', 
        gap: '20px' 
    },
    card: { 
        flex: 1,
        border: '1.5px solid #b4afaf', 
        padding: '35px 25px', 
        borderRadius: '15px', 
        cursor: 'pointer', 
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        textAlign: 'left',
        transition: '0.2s'
    },
    roleTitle: {
        fontSize: '22px',
        fontWeight: 'bold',
        color: '#0A0A1F',
        marginBottom: '12px'
    },
    description: {
        fontSize: '14px',
        color: '#666',
        lineHeight: '1.6',
        marginBottom: '30px'
    },
    redBtn: { 
        backgroundColor: '#E60000', 
        color: '#fff', 
        padding: '14px', 
        width: '100%',
        borderRadius: '12px', 
        border: 'none', 
        fontWeight: 'bold', 
        cursor: 'pointer'
    },
    lightBtn: { 
        backgroundColor: '#e79702', 
        color: '#fff', 
        padding: '14px', 
        width: '100%',
        borderRadius: '12px', 
        border: 'none', 
        fontWeight: 'bold', 
        cursor: 'pointer'
    }
};