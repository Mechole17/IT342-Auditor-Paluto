import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { useAuth } from '../core/context/AuthContext';
import { API_BASE_URL } from '../core/api.js';

export default function Cooks() {
    const navigate = useNavigate();
    const [cooks, setCooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState({ yearsXp: '', hourlyRate: '' });
    const [applied, setApplied] = useState({ yearsXp: '', hourlyRate: '' });
    const { user } = useAuth();

    useEffect(() => {
        const fetchCooks = async () => {
            try {
                const res = await axios.get(`${API_BASE_URL}/api/cook/all`);
                setCooks(res.data.data);
            } catch (err) {
                console.error("Failed to fetch cooks", err);
            } finally {
                setLoading(false);
            }
        };
        fetchCooks();
    }, []);

    const getInitials = (firstname, lastname) => {
        return `${firstname?.[0] || ''}${lastname?.[0] || ''}`.toUpperCase();
    };

    const filtered = cooks.filter(cook => {
        const xpOk = applied.yearsXp === '' || cook.yearsXp >= parseInt(applied.yearsXp);
        const rateOk = applied.hourlyRate === '' || cook.hourlyRate <= parseFloat(applied.hourlyRate);
        return xpOk && rateOk;
    });

    const styles = {
        wrapper: { display: 'flex', gap: '32px', padding: '32px 40px', fontFamily: 'Arial, sans-serif' },
        sidebar: { width: '160px', flexShrink: 0 },
        filterBox: { border: '1.5px solid #ddd', borderRadius: '12px', padding: '16px' },
        filterTitle: { fontWeight: '700', fontSize: '16px', marginBottom: '16px' },
        filterLabel: { fontSize: '13px', color: '#555', marginBottom: '4px' },
        filterInput: { width: '100%', padding: '6px 10px', borderRadius: '8px', border: '1.5px solid #ccc', fontSize: '14px', boxSizing: 'border-box', marginBottom: '12px' },
        filterBtn: { width: '100%', backgroundColor: '#0A0A1F', color: '#fff', border: 'none', borderRadius: '10px', padding: '10px', fontWeight: '700', fontSize: '14px', cursor: 'pointer' },
        main: { flex: 1 },
        title: { fontSize: '28px', fontWeight: '800', marginBottom: '24px' },
        grid: { display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '20px' },
        card: { border: '1.5px solid #ddd', borderRadius: '16px', padding: '16px', display: 'flex', flexDirection: 'column', gap: '10px' },
        cardTop: { display: 'flex', alignItems: 'center', justifyContent: 'space-between' },
        avatarRow: { display: 'flex', alignItems: 'center', gap: '10px' },
        avatar: { width: '40px', height: '40px', borderRadius: '50%', backgroundColor: '#110202', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: '700', fontSize: '14px', color: '#fff', flexShrink: 0 },
        cookName: { fontWeight: '700', fontSize: '15px', margin: 0 },
        yrs: { fontSize: '13px', color: '#888' },
        bio: { fontSize: '13px', color: '#444', lineHeight: 1.5, margin: 0 },
        cardBottom: { display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '4px' },
        rating: { fontSize: '14px', fontWeight: '600' },
        rate: { fontSize: '14px', fontWeight: '700' },
        viewBtn: { width: '100%', backgroundColor: '#F5A623', color: '#fff', border: 'none', borderRadius: '10px', padding: '10px', fontWeight: '700', fontSize: '14px', cursor: 'pointer' },
    };

    if (loading) return <div style={styles.wrapper}>Loading cooks...</div>;

    return (
        <div style={styles.wrapper}>
            {/* Sidebar Filter */}
            <div style={styles.sidebar}>
                <div style={styles.filterBox}>
                    <p style={styles.filterTitle}>filter</p>

                    <p style={styles.filterLabel}>Years experience</p>
                    <input
                        type="number"
                        min="0"
                        style={styles.filterInput}
                        value={filters.yearsXp}
                        onChange={e => setFilters(prev => ({ ...prev, yearsXp: e.target.value }))}
                    />

                    <p style={styles.filterLabel}>Hourly rate</p>
                    <input
                        type="number"
                        min="0"
                        style={styles.filterInput}
                        value={filters.hourlyRate}
                        onChange={e => setFilters(prev => ({ ...prev, hourlyRate: e.target.value }))}
                    />

                    <button style={styles.filterBtn} onClick={() => setApplied(filters)}>
                        Apply filter
                    </button>
                </div>
            </div>

            {/* Main Content */}
            <div style={styles.main}>
                <h2 style={styles.title}>Cooks</h2>

                {filtered.length === 0 ? (
                    <p style={{ color: '#999' }}>No cooks found.</p>
                ) : (
                    <div style={styles.grid}>
                        {filtered.map(cook => (
                            <div key={cook.id} style={styles.card}>
                                <div style={styles.cardTop}>
                                    <div style={styles.avatarRow}>
                                        <div style={styles.avatar}>{getInitials(cook.firstname, cook.lastname)}</div>
                                        <p style={styles.cookName}>{cook.firstname} {cook.lastname}</p>
                                    </div>
                                    <span style={styles.yrs}>{cook.yearsXp} yrs</span>
                                </div>

                                <p style={styles.bio}>
                                    {cook.bio?.length > 80 ? cook.bio.substring(0, 80) + '...' : cook.bio}
                                </p>

                                <div style={styles.cardBottom}>
                                    <span style={styles.rating}>
                                        {cook.averageRating ? `${cook.averageRating.toFixed(1)} ⭐` : 'No ratings'}
                                    </span>
                                    <span style={styles.rate}>Php {Number(cook.hourlyRate).toLocaleString()}</span>
                                </div>

                                <button style={styles.viewBtn} onClick={
                                    () => {
                                        if (user) {
                                            navigate(`/customer/cooks/${cook.id}`);
                                        } else {
                                            navigate(`/cooks/${cook.id}`);
                                        }
                                    }
                                }>
                                    view menu
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}