import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../core/context/AuthContext';

export default function CookProfile() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user, openLoginModal } = useAuth();

    const [cook, setCook] = useState(null);
    const [services, setServices] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [cookRes, servicesRes] = await Promise.all([
                    axios.get(`http://localhost:8080/api/cook/${id}`),
                    axios.get(`http://localhost:8080/api/services/cook/${id}/services`)
                ]);
                setCook(cookRes.data.data);
                setServices(servicesRes.data.data);
            } catch (err) {
                console.error("Failed to fetch cook profile", err);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [id]);

    const getInitials = (firstname, lastname) => {
        return `${firstname?.[0] || ''}${lastname?.[0] || ''}`.toUpperCase();
    };

    const handleViewService = (service) => {
        if (!user) {
            openLoginModal();
            return;
        }
        navigate(`/customer/service-details/${service.id}`);
    };

    const styles = {
        wrapper: { padding: '32px 40px', fontFamily: 'Arial, sans-serif' },
        backBtn: { background: 'none', border: 'none', cursor: 'pointer', fontSize: '14px', color: '#888', fontWeight: '600', marginBottom: '24px', padding: 0 },
        profileCard: { display: 'flex', alignItems: 'center', gap: '24px', backgroundColor: '#fdf8f2', border: '1.5px solid #eee', borderRadius: '20px', padding: '28px', marginBottom: '40px' },
        avatar: { width: '80px', height: '80px', borderRadius: '50%', backgroundColor: '#c8c8c8', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: '700', fontSize: '28px', color: '#fff', flexShrink: 0 },
        cookInfo: { flex: 1 },
        cookName: { fontSize: '28px', fontWeight: '900', margin: '0 0 6px' },
        cookBio: { fontSize: '14px', color: '#555', margin: '0 0 12px', lineHeight: 1.6 },
        metaRow: { display: 'flex', gap: '32px' },
        metaItem: { display: 'flex', flexDirection: 'column', gap: '2px' },
        metaLabel: { fontSize: '12px', color: '#888' },
        metaValue: { fontSize: '16px', fontWeight: '700' },
        sectionTitle: { fontSize: '22px', fontWeight: '800', marginBottom: '20px' },
        grid: { display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px' },
        card: { border: '1.5px solid #ddd', borderRadius: '16px', overflow: 'hidden' },
        cardImg: { width: '100%', height: '140px', objectFit: 'cover', backgroundColor: '#f5f5f5' },
        cardBody: { padding: '14px' },
        cardTitle: { fontWeight: '700', fontSize: '16px', margin: '0 0 6px' },
        cardMeta: { fontSize: '13px', color: '#888', margin: '2px 0' },
        cardPrice: { fontSize: '18px', fontWeight: '800', margin: '10px 0' },
        viewBtn: { width: '100%', backgroundColor: '#F5A623', color: '#fff', border: 'none', borderRadius: '10px', padding: '10px', fontWeight: '700', fontSize: '14px', cursor: 'pointer' },
    };

    if (loading) return <div style={styles.wrapper}>Loading...</div>;
    if (!cook) return <div style={styles.wrapper}>Cook not found.</div>;

    return (
        <div style={styles.wrapper}>

            {/* Cook Profile */}
            <div style={styles.profileCard}>
                <div style={styles.avatar}>{getInitials(cook.firstname, cook.lastname)}</div>
                <div style={styles.cookInfo}>
                    <h1 style={styles.cookName}>{cook.firstname} {cook.lastname}</h1>
                    <p style={styles.cookBio}>{cook.bio}</p>
                    <div style={styles.metaRow}>
                        <div style={styles.metaItem}>
                            <span style={styles.metaLabel}>Hourly Rate</span>
                            <span style={styles.metaValue}>Php {Number(cook.hourlyRate).toLocaleString()}</span>
                        </div>
                        <div style={styles.metaItem}>
                            <span style={styles.metaLabel}>Experience</span>
                            <span style={styles.metaValue}>{cook.yearsXp} years</span>
                        </div>
                        <div style={styles.metaItem}>
                            <span style={styles.metaLabel}>Rating</span>
                            <span style={styles.metaValue}>4.5 ⭐</span>
                        </div>
                    </div>
                </div>
            </div>

            {/* Services */}
            <h2 style={styles.sectionTitle}>Menu</h2>
            {services.length === 0 ? (
                <p style={{ color: '#999' }}>No services listed yet.</p>
            ) : (
                <div style={styles.grid}>
                    {services.map(service => (
                        <div key={service.id} style={styles.card}>
                            {service.imageUrl
                                ? <img src={service.imageUrl} alt={service.title} style={styles.cardImg} />
                                : <div style={{ ...styles.cardImg, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#ccc' }}>No Image</div>
                            }
                            <div style={styles.cardBody}>
                                <p style={styles.cardTitle}>{service.title}</p>
                                <p style={styles.cardMeta}>🍽 Serves {service.servingSize}</p>
                                <p style={styles.cardMeta}>⏱ {service.estPrepTime} mins prep time</p>
                                <p style={styles.cardPrice}>Php {Number(service.ingredientsCost).toLocaleString()}</p>
                                <button style={styles.viewBtn} onClick={() => handleViewService(service)}>
                                    View Details
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}