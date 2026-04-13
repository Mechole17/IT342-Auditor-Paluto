import { useNavigate, useSearchParams } from 'react-router-dom';

export default function SelectRole() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    // Preserve Google data to pass to the next step
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
        <div style={styles.container}>
            <h1>One last step!</h1>
            <p>How will you be using PALUTO?</p>
            <div style={styles.cardContainer}>
                <div style={styles.card} onClick={() => handleChoice('CUSTOMER')}>
                    <h2>I am a Customer</h2>
                    <p>I want to find and book professional home cooks.</p>
                </div>
                <div style={styles.card} onClick={() => handleChoice('COOK')}>
                    <h2>I am a Cook</h2>
                    <p>I want to offer my culinary services to families.</p>
                </div>
            </div>
        </div>
    );
}

const styles = {
    container: { textAlign: 'center', padding: '50px', fontFamily: 'Arial' },
    cardContainer: { display: 'flex', justifyContent: 'center', gap: '20px', marginTop: '30px' },
    card: { border: '2px solid #0A0A1F', padding: '30px', borderRadius: '20px', cursor: 'pointer', width: '250px', transition: '0.3s' }
};