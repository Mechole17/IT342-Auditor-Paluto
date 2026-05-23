import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../core/context/AuthContext';
import { API_BASE_URL } from '../core/api.js';

export default function CookProfile() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user, openLoginModal } = useAuth();

    const [cook, setCook] = useState(null);
    const [services, setServices] = useState([]);
    const [certificates, setCertificates] = useState([]);
    const [ratings, setRatings] = useState([]);
    const [averageRating, setAverageRating] = useState(0);
    const [loading, setLoading] = useState(true);

    const [activeTab, setActiveTab] = useState('Menu');

    // Tab styles
    const tabStyles = {
        tabContainer: { display: 'flex', gap: '0', marginBottom: '24px', borderBottom: '2px solid #eee' },
        tab: (isActive) => ({
            padding: '12px 28px',
            cursor: 'pointer',
            fontWeight: isActive ? '700' : '500',
            color: isActive ? '#0A0A1F' : '#888',
            borderBottom: isActive ? '3px solid #0A0A1F' : '3px solid transparent',
            marginBottom: '-2px',
            fontSize: '15px'
        }),
    };

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [cookRes, servicesRes, certsRes, ratingsRes, avgRes] = await Promise.all([
                    axios.get(`${API_BASE_URL}/api/cook/${id}`),
                    axios.get(`${API_BASE_URL}/api/services/cook/${id}/services`),
                    axios.get(`${API_BASE_URL}/api/certificates/cook/${id}`),
                    axios.get(`${API_BASE_URL}/api/ratings/cook/${id}`),
                    axios.get(`${API_BASE_URL}/api/ratings/cook/${id}/average`)
                ]);
                setCook(cookRes.data.data);
                setServices(servicesRes.data.data);
                setCertificates(certsRes.data.data.filter(c => c.status === 'APPROVED'));
                setRatings(ratingsRes.data.data || []);
                setAverageRating(avgRes.data.data || 0);
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
        certSection: { display: 'flex', flexDirection: 'column', gap: '12px', marginBottom: '40px' },
        certCard: { display: 'flex', alignItems: 'center', justifyContent: 'space-between', border: '1.5px solid #d1e7dd', borderRadius: '12px', padding: '16px 20px', backgroundColor: '#f8fff9' },
        certInfo: { display: 'flex', alignItems: 'center', gap: '14px' },
        certTitle: { fontWeight: '700', fontSize: '15px', margin: 0 },
        certBadge: { backgroundColor: '#d1e7dd', color: '#0a3622', fontSize: '11px', fontWeight: '700', padding: '3px 10px', borderRadius: '20px' },
        certViewLink: { fontSize: '13px', color: '#0A0A1F', fontWeight: '600', textDecoration: 'underline' },
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
                            <span style={styles.metaValue}>
                                {averageRating > 0 ? `${averageRating} ⭐` : 'No ratings yet'}
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            {/* Tabs */}
            <div style={tabStyles.tabContainer}>
                {['Menu', 'Reviews', 'Certificates'].map(tab => (
                    <div key={tab} style={tabStyles.tab(activeTab === tab)} onClick={() => setActiveTab(tab)}>
                        {tab} {tab === 'Reviews' && `(${ratings.length})`}
                    </div>
                ))}
            </div>

            {/* Menu Tab */}
            {activeTab === 'Menu' && (
                <>
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
                </>
            )}

            {/* Reviews Tab */}
            {activeTab === 'Reviews' && (
                <>
                    {ratings.length === 0 ? (
                        <p style={{ color: '#999' }}>No reviews yet.</p>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                            {ratings.map(r => (
                                <div key={r.id} style={{
                                    border: '1.5px solid #eee',
                                    borderRadius: '12px',
                                    padding: '16px 20px',
                                    backgroundColor: '#fdf8f2'
                                }}>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                                        <span style={{ fontWeight: '700', fontSize: '14px' }}>{r.customerName}</span>
                                        <span style={{ color: '#F5A623', fontSize: '16px' }}>
                                            {'★'.repeat(r.rating)}{'☆'.repeat(5 - r.rating)}
                                        </span>
                                    </div>
                                    {r.comment && (
                                        <p style={{ fontSize: '14px', color: '#555', margin: 0 }}>{r.comment}</p>
                                    )}
                                </div>
                            ))}
                        </div>
                    )}
                </>
            )}

            {/* Certificates Tab */}
            {activeTab === 'Certificates' && (
                <>
                    {certificates.length === 0 ? (
                        <p style={{ color: '#999' }}>No verified certificates yet.</p>
                    ) : (
                        <div style={styles.certSection}>
                            {certificates.map(cert => (
                                <div key={cert.id} style={styles.certCard}>
                                    <div style={styles.certInfo}>
                                        <span style={{ fontSize: '24px' }}>📄</span>
                                        <div>
                                            <p style={styles.certTitle}>{cert.title}</p>
                                            <span style={styles.certBadge}>✓ Verified</span>
                                        </div>
                                    </div>
                                    <a href={cert.fileUrl} target="_blank" rel="noreferrer" style={styles.certViewLink}>
                                        View
                                    </a>
                                </div>
                            ))}
                        </div>
                    )}
                </>
            )}
        </div>
    );
}