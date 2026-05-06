import { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../../context/AuthContext';

export default function CookPortfolio() {
    const { user, token } = useAuth();
    const [showModal, setShowModal] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const [formData, setFormData] = useState({
        title: '',
        description: '',
        servingSize: '',
        ingredientsList: '',
        ingredientsCost: '',
        estPrepTime: '',
        imageUrl: ''
    });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const resetForm = () => {
        setFormData({ title: '', description: '', servingSize: '', ingredientsList: '', ingredientsCost: '', estPrepTime: '', imageUrl: '' });
        setError(null);
    };

    const handleClose = () => {
        resetForm();
        setShowModal(false);
    };

    const handleSubmit = async () => {
        if (!formData.title || !formData.description || !formData.servingSize ||
            !formData.ingredientsList || !formData.ingredientsCost || !formData.estPrepTime) {
            setError("Please fill in all required fields.");
            return;
        }

        setLoading(true);
        setError(null);

        try {
            const payload = {
                ...formData,
                servingSize: parseInt(formData.servingSize, 10),
                ingredientsCost: parseFloat(formData.ingredientsCost),
                estPrepTime: parseInt(formData.estPrepTime, 10),
            };

            await axios.post(
                `http://localhost:8080/api/service/create/${user.id}`,
                payload,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            alert("Service created successfully!");
            handleClose();
        } catch (err) {
            const msg = err.response?.data?.error?.message || "Failed to create service.";
            setError(msg);
        } finally {
            setLoading(false);
        }
    };

    const styles = {
        // Page
        page: { padding: '40px', fontFamily: 'Arial, sans-serif' },
        addBtn: { backgroundColor: '#0A0A1F', color: '#fff', padding: '12px 28px', borderRadius: '12px', fontWeight: 'bold', fontSize: '15px', border: 'none', cursor: 'pointer' },

        // Modal overlay
        overlay: { position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 },
        modal: { backgroundColor: '#fff', borderRadius: '20px', padding: '40px', width: '580px', maxHeight: '85vh', overflowY: 'auto', position: 'relative' },
        closeBtn: { position: 'absolute', top: '16px', right: '20px', background: 'none', border: 'none', fontSize: '22px', cursor: 'pointer', color: '#666' },

        // Form
        label: { fontSize: '14px', color: '#666', marginTop: '12px', display: 'block' },
        input: { width: '100%', padding: '12px', margin: '6px 0', borderRadius: '12px', border: '1.5px solid #7b7a7a', fontSize: '15px', outline: 'none', boxSizing: 'border-box' },
        row: { display: 'flex', gap: '12px' },
        btnRow: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '28px' },
        submitBtn: { backgroundColor: '#0A0A1F', color: '#fff', padding: '14px 40px', borderRadius: '12px', fontWeight: 'bold', fontSize: '16px', border: 'none', cursor: 'pointer' },
        cancelLink: { color: '#666', cursor: 'pointer', fontWeight: 'bold', textDecoration: 'underline' },
        errorMsg: { color: '#d10b04', fontWeight: 'bold', fontSize: '14px', marginBottom: '8px' }
    };

    return (
        <div style={styles.page}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
                <h1 style={{ margin: 0 }}>My Portfolio</h1>
                <button style={styles.addBtn} onClick={() => setShowModal(true)}>+ Add Service</button>
            </div>

            <h2>Certificates</h2>
            <p style={{ color: '#999' }}>No certificates uploaded yet.</p>

            <h2 style={{ marginTop: '32px' }}>Service Offerings</h2>
            <p style={{ color: '#999' }}>No services listed yet. Click "Add Service" to get started.</p>

            {/* MODAL */}
            {showModal && (
                <div style={styles.overlay} onClick={handleClose}>
                    <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
                        <button style={styles.closeBtn} onClick={handleClose}>✕</button>

                        <h2 style={{ marginTop: 0, fontSize: '28px', fontWeight: 'bold' }}>Create a Service</h2>
                        <p style={{ color: '#666', marginBottom: '16px' }}>Fill in the details of your culinary service</p>

                        {error && <div style={styles.errorMsg}>{error}</div>}

                        <label style={styles.label}>Service Title</label>
                        <input name="title" placeholder="e.g. Homemade Chicken Adobo" style={styles.input} onChange={handleChange} value={formData.title} />

                        <label style={styles.label}>Description</label>
                        <textarea name="description" placeholder="Describe your service..." style={{ ...styles.input, height: '80px', resize: 'none' }} onChange={handleChange} value={formData.description} />

                        <label style={styles.label}>Ingredients List</label>
                        <textarea name="ingredientsList" placeholder="e.g. Chicken, soy sauce, vinegar, garlic..." style={{ ...styles.input, height: '80px', resize: 'none' }} onChange={handleChange} value={formData.ingredientsList} />

                        <div style={styles.row}>
                            <div style={{ flex: 1 }}>
                                <label style={styles.label}>Ingredients Cost (₱)</label>
                                <input name="ingredientsCost" type="number" min="0" placeholder="0.00" style={styles.input} onChange={handleChange} value={formData.ingredientsCost} />
                            </div>
                            <div style={{ flex: 1 }}>
                                <label style={styles.label}>Serving Size</label>
                                <input name="servingSize" type="number" min="1" placeholder="e.g. 4" style={styles.input} onChange={handleChange} value={formData.servingSize} />
                            </div>
                            <div style={{ flex: 1 }}>
                                <label style={styles.label}>Prep Time (mins)</label>
                                <input name="estPrepTime" type="number" min="1" placeholder="e.g. 45" style={styles.input} onChange={handleChange} value={formData.estPrepTime} />
                            </div>
                        </div>

                        <label style={styles.label}>Image URL (optional)</label>
                        <input name="imageUrl" placeholder="https://..." style={styles.input} onChange={handleChange} value={formData.imageUrl} />

                        <div style={styles.btnRow}>
                            <span style={styles.cancelLink} onClick={handleClose}>Cancel</span>
                            <button style={{ ...styles.submitBtn, opacity: loading ? 0.7 : 1 }} onClick={handleSubmit} disabled={loading}>
                                {loading ? "Publishing..." : "Publish Service"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}