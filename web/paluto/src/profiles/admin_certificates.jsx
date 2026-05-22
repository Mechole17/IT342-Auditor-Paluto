import { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../core/context/AuthContext';
import { API_BASE_URL } from '../core/api.js';

export default function AdminCertificates() {
    const { token } = useAuth();
    const [certificates, setCertificates] = useState([]);
    const [loading, setLoading] = useState(true);
    const [reviewNote, setReviewNote] = useState('');
    const [selectedCert, setSelectedCert] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [filter, setFilter] = useState('PENDING');

    const fetchCertificates = async () => {
        try {
            const res = await axios.get(
                `${API_BASE_URL}/api/certificates/all`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setCertificates(res.data.data || []);
        } catch (err) {
            console.error("Failed to fetch certificates", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCertificates();
    }, []);

    const handleReview = async (status) => {
        try {
            await axios.patch(
                `${API_BASE_URL}/api/certificates/${selectedCert.id}/review`,
                { status, adminNote: reviewNote },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setShowModal(false);
            setSelectedCert(null);
            setReviewNote('');
            fetchCertificates();
        } catch (err) {
            console.error("Failed to review certificate", err);
        }
    };

    const getStatusBadge = (status) => {
        const colors = {
            PENDING: { bg: '#fff3cd', color: '#856404' },
            APPROVED: { bg: '#d1e7dd', color: '#0a3622' },
            REJECTED: { bg: '#f8d7da', color: '#58151c' },
        };
        const style = colors[status] || colors.PENDING;
        return (
            <span style={{
                backgroundColor: style.bg,
                color: style.color,
                fontSize: '11px',
                fontWeight: '700',
                padding: '3px 10px',
                borderRadius: '20px',
            }}>
                {status}
            </span>
        );
    };

    const filtered = certificates.filter(c => filter === 'ALL' || c.status === filter);

    const styles = {
        wrapper: { padding: '32px 40px', fontFamily: 'Arial, sans-serif' },
        header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
        title: { fontSize: '26px', fontWeight: '700', margin: 0 },
        tabs: { display: 'flex', gap: '12px', marginBottom: '24px' },
        tab: { padding: '8px 20px', borderRadius: '999px', border: 'none', fontSize: '14px', fontWeight: '600', cursor: 'pointer', backgroundColor: 'transparent', color: '#555' },
        tabActive: { backgroundColor: '#1a1a1a', color: '#fff' },
        card: { display: 'flex', alignItems: 'center', justifyContent: 'space-between', border: '1.5px solid #eee', borderRadius: '12px', padding: '16px 20px', marginBottom: '12px', backgroundColor: '#fff' },
        cardInfo: { display: 'flex', alignItems: 'center', gap: '16px' },
        certTitle: { fontWeight: '700', fontSize: '15px', margin: '0 0 4px' },
        cookName: { fontSize: '13px', color: '#888', margin: 0 },
        cardActions: { display: 'flex', alignItems: 'center', gap: '12px' },
        viewLink: { fontSize: '13px', color: '#0c0cc5', fontWeight: '600', textDecoration: 'none', cursor: 'pointer', },
        reviewBtn: { backgroundColor: '#0A0A1F', color: '#fff', border: 'none', borderRadius: '10px', padding: '8px 20px', fontWeight: '700', fontSize: '13px', cursor: 'pointer' },
        overlay: { position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 },
        modal: { backgroundColor: '#fff', borderRadius: '20px', padding: '40px', width: '500px', position: 'relative' },
        closeBtn: { position: 'absolute', top: '16px', right: '20px', background: 'none', border: 'none', fontSize: '22px', cursor: 'pointer', color: '#666' },
        label: { fontSize: '14px', color: '#666', marginTop: '12px', display: 'block' },
        input: { width: '100%', padding: '12px', margin: '6px 0', borderRadius: '12px', border: '1.5px solid #7b7a7a', fontSize: '15px', outline: 'none', boxSizing: 'border-box' },
        btnRow: { display: 'flex', gap: '12px', marginTop: '24px' },
        approveBtn: { flex: 1, backgroundColor: '#0a3622', color: '#fff', border: 'none', borderRadius: '12px', padding: '14px', fontWeight: '700', fontSize: '15px', cursor: 'pointer' },
        rejectBtn: { flex: 1, backgroundColor: '#d10b04', color: '#fff', border: 'none', borderRadius: '12px', padding: '14px', fontWeight: '700', fontSize: '15px', cursor: 'pointer' },
        empty: { color: '#aaa', fontSize: '14px' },
    };

    if (loading) return <div style={styles.wrapper}>Loading certificates...</div>;

    return (
        <div style={styles.wrapper}>
            <div style={styles.header}>
                <h2 style={styles.title}>Certificate Management</h2>
            </div>

            {/* Filter Tabs */}
            <div style={styles.tabs}>
                {['PENDING', 'APPROVED', 'REJECTED', 'ALL'].map(tab => (
                    <button
                        key={tab}
                        style={{ ...styles.tab, ...(filter === tab ? styles.tabActive : {}) }}
                        onClick={() => setFilter(tab)}
                    >
                        {tab}
                    </button>
                ))}
            </div>

            {/* Certificate List */}
            {filtered.length === 0 ? (
                <p style={styles.empty}>No {filter.toLowerCase()} certificates found.</p>
            ) : (
                filtered.map(cert => (
                    <div key={cert.id} style={styles.card}>
                        <div style={styles.cardInfo}>
                            <div>
                                <p style={styles.certTitle}>{cert.title}</p>
                                <p style={styles.cookName}>by {cert.cookName}</p>
                                {getStatusBadge(cert.status)}
                                {cert.adminNote && (
                                    <p style={{ fontSize: '12px', color: '#888', margin: '4px 0 0' }}>Note: {cert.adminNote}</p>
                                )}
                            </div>
                        </div>
                        <div style={styles.cardActions}>
                            <a href={cert.fileUrl} target="_blank" rel="noreferrer" style={styles.viewLink}>View</a>
                            {cert.status === 'PENDING' && (
                                <button style={styles.reviewBtn} onClick={() => { setSelectedCert(cert); setShowModal(true); }}>
                                    Review
                                </button>
                            )}
                        </div>
                    </div>
                ))
            )}

            {/* Review Modal */}
            {showModal && selectedCert && (
                <div style={styles.overlay} onClick={() => setShowModal(false)}>
                    <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
                        <button style={styles.closeBtn} onClick={() => setShowModal(false)}>✕</button>

                        <h2 style={{ marginTop: 0, fontSize: '24px', fontWeight: 'bold' }}>Review Certificate</h2>
                        <p style={{ color: '#666' }}>Certificate: <strong>{selectedCert.title}</strong></p>
                        <p style={{ color: '#666' }}>Cook: <strong>{selectedCert.cookName}</strong></p>

                        <a href={selectedCert.fileUrl} target="_blank" rel="noreferrer" style={{ ...styles.viewLink, display: 'inline-block', marginBottom: '16px' }}>
                            View Certificate
                        </a>

                        <label style={styles.label}>Note (optional — required for rejection)</label>
                        <textarea
                            placeholder="Add a note for the cook..."
                            style={{ ...styles.input, height: '80px', resize: 'none' }}
                            value={reviewNote}
                            onChange={(e) => setReviewNote(e.target.value)}
                        />

                        <div style={styles.btnRow}>
                            <button style={styles.approveBtn} onClick={() => handleReview('APPROVED')}>
                                ✓ Approve
                            </button>
                            <button style={styles.rejectBtn} onClick={() => handleReview('REJECTED')}>
                                ✗ Reject
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}