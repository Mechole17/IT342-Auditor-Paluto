import { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../core/context/AuthContext';
import { API_BASE_URL } from '../core/api.js';

export default function CookPortfolio() {
    const { token } = useAuth();
    const [showServiceModal, setShowServiceModal] = useState(false);
    const [showCertModal, setShowCertModal] = useState(false);
    const [loading, setLoading] = useState(false);
    const [certLoading, setCertLoading] = useState(false);
    const [error, setError] = useState(null);
    const [certError, setCertError] = useState(null);
    const [imagePreview, setImagePreview] = useState(null);
    const [imageFile, setImageFile] = useState(null);
    const [certFile, setCertFile] = useState(null);
    const [certTitle, setCertTitle] = useState('');
    const [services, setServices] = useState([]);
    const [certificates, setCertificates] = useState([]);
    //edit service
    const [showEditModal, setShowEditModal] = useState(false);
    const [editingService, setEditingService] = useState(null);
    const [editImageFile, setEditImageFile] = useState(null);
    const [editImagePreview, setEditImagePreview] = useState(null);
    const [editLoading, setEditLoading] = useState(false);
    const [editError, setEditError] = useState(null);

    const [formData, setFormData] = useState({
        title: '',
        description: '',
        servingSize: '',
        ingredientsList: '',
        ingredientsCost: '',
        estPrepTime: '',
    });

    const [editFormData, setEditFormData] = useState({
        title: '',
        description: '',
        servingSize: '',
        ingredientsList: '',
        ingredientsCost: '',
        estPrepTime: '',
    });

    const fetchServices = async () => {
        try {
            const res = await axios.get(
                `${API_BASE_URL}/api/services/my-services`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setServices(res.data.data || []);
        } catch (err) {
            console.error("Failed to fetch services", err);
        }
    };

    const fetchCertificates = async () => {
        try {
            const res = await axios.get(
                `${API_BASE_URL}/api/certificates/my-certificates`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            setCertificates(res.data.data || []);
        } catch (err) {
            console.error("Failed to fetch certificates", err);
        }
    };

    useEffect(() => {
        fetchServices();
        fetchCertificates();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;
        setImageFile(file);
        setImagePreview(URL.createObjectURL(file));
    };

    const handleCertFileChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;
        setCertFile(file);
    };

    const resetForm = () => {
        setFormData({ title: '', description: '', servingSize: '', ingredientsList: '', ingredientsCost: '', estPrepTime: '' });
        setImageFile(null);
        setImagePreview(null);
        setError(null);
    };

    const resetCertForm = () => {
        setCertFile(null);
        setCertTitle('');
        setCertError(null);
    };

    const handleServiceClose = () => {
        resetForm();
        setShowServiceModal(false);
    };

    const handleCertClose = () => {
        resetCertForm();
        setShowCertModal(false);
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
            let imageUrl = '';
            if (imageFile) {
                const imageData = new FormData();
                imageData.append('file', imageFile);

                const uploadRes = await axios.post(
                    `${API_BASE_URL}/api/storage/service-upload`,
                    imageData,
                    { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'multipart/form-data' } }
                );
                imageUrl = uploadRes.data.url;
            }

            const payload = {
                ...formData,
                servingSize: parseInt(formData.servingSize, 10),
                ingredientsCost: parseFloat(formData.ingredientsCost),
                estPrepTime: parseInt(formData.estPrepTime, 10),
                imageUrl,
            };

            await axios.post(
                `${API_BASE_URL}/api/services/create`,
                payload,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            handleServiceClose();
            fetchServices();
        } catch (err) {
            const msg = err.response?.data?.error?.message || "Failed to create service.";
            setError(msg);
        } finally {
            setLoading(false);
        }
    };
    //edit handlers
    const handleEditClick = (service) => {
        setEditingService(service);
        setEditFormData({
            title: service.title,
            description: service.description,
            servingSize: service.servingSize,
            ingredientsList: service.ingredientsList,
            ingredientsCost: service.ingredientsCost,
            estPrepTime: service.estPrepTime,
        });
        setEditImagePreview(service.imageUrl);
        setEditError(null);
        setShowEditModal(true);
    };

    const handleEditChange = (e) => {
        setEditFormData({ ...editFormData, [e.target.name]: e.target.value });
    };

    const handleEditImageChange = (e) => {
        const file = e.target.files[0];
        if (!file) return;
        setEditImageFile(file);
        setEditImagePreview(URL.createObjectURL(file));
    };

    const handleEditSubmit = async () => {
        if (!editFormData.title || !editFormData.description || !editFormData.servingSize ||
            !editFormData.ingredientsList || !editFormData.ingredientsCost || !editFormData.estPrepTime) {
            setEditError("Please fill in all required fields.");
            return;
        }

        setEditLoading(true);
        setEditError(null);

        try {
            let imageUrl = editingService.imageUrl;

            if (editImageFile) {
                const imageData = new FormData();
                imageData.append('file', editImageFile);
                const uploadRes = await axios.post(
                    `${API_BASE_URL}/api/storage/service-upload`,
                    imageData,
                    { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'multipart/form-data' } }
                );
                imageUrl = uploadRes.data.url;
            }

            const payload = {
                ...editFormData,
                servingSize: parseInt(editFormData.servingSize, 10),
                ingredientsCost: parseFloat(editFormData.ingredientsCost),
                estPrepTime: parseInt(editFormData.estPrepTime, 10),
                imageUrl,
            };

            await axios.put(
                `${API_BASE_URL}/api/services/${editingService.id}`,
                payload,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setShowEditModal(false);
            setEditImageFile(null);
            fetchServices();
        } catch (err) {
            const msg = err.response?.data?.error?.message || "Failed to update service.";
            setEditError(msg);
        } finally {
            setEditLoading(false);
        }
    };

    const handleCertSubmit = async () => {
        if (!certTitle || !certFile) {
            setCertError("Please provide a title and upload a file.");
            return;
        }

        setCertLoading(true);
        setCertError(null);

        try {
            // 1. Upload file to Supabase
            const fileData = new FormData();
            fileData.append('file', certFile);

            const uploadRes = await axios.post(
                `${API_BASE_URL}/api/storage/certificate-upload`,
                fileData,
                { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'multipart/form-data' } }
            );
            const fileUrl = uploadRes.data.url;

            // 2. Save certificate
            await axios.post(
                `${API_BASE_URL}/api/certificates/upload`,
                { title: certTitle, fileUrl },
                { headers: { Authorization: `Bearer ${token}` } }
            );

            handleCertClose();
            fetchCertificates();
        } catch (err) {
            const msg = err.response?.data?.error?.message || "Failed to upload certificate.";
            setCertError(msg);
        } finally {
            setCertLoading(false);
        }
    };

    const handleDeleteCertificate = async (certId) => {
        if (!window.confirm("Are you sure you want to remove this certificate?")) return;

        try {
            await axios.delete(
                `${API_BASE_URL}/api/certificates/${certId}`,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            fetchCertificates();
        } catch (err) {
            console.error("Failed to delete certificate", err);
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

    const styles = {
        page: { padding: '40px', fontFamily: 'Arial, sans-serif' },
        addBtn: { backgroundColor: '#0A0A1F', color: '#fff', padding: '12px 28px', borderRadius: '12px', fontWeight: 'bold', fontSize: '15px', border: 'none', cursor: 'pointer' },
        overlay: { position: 'fixed', inset: 0, backgroundColor: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 },
        modal: { backgroundColor: '#fff', borderRadius: '20px', padding: '40px', width: '580px', maxHeight: '85vh', overflowY: 'auto', position: 'relative' },
        closeBtn: { position: 'absolute', top: '16px', right: '20px', background: 'none', border: 'none', fontSize: '22px', cursor: 'pointer', color: '#666' },
        label: { fontSize: '14px', color: '#666', marginTop: '12px', display: 'block' },
        input: { width: '100%', padding: '12px', margin: '6px 0', borderRadius: '12px', border: '1.5px solid #7b7a7a', fontSize: '15px', outline: 'none', boxSizing: 'border-box' },
        row: { display: 'flex', gap: '12px' },
        btnRow: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '28px' },
        submitBtn: { backgroundColor: '#0A0A1F', color: '#fff', padding: '14px 40px', borderRadius: '12px', fontWeight: 'bold', fontSize: '16px', border: 'none', cursor: 'pointer' },
        cancelLink: { color: '#666', cursor: 'pointer', fontWeight: 'bold', textDecoration: 'underline' },
        errorMsg: { color: '#d10b04', fontWeight: 'bold', fontSize: '14px', marginBottom: '8px' },
        uploadBox: { border: '2px dashed #7b7a7a', borderRadius: '12px', padding: '20px', textAlign: 'center', cursor: 'pointer', marginTop: '6px', backgroundColor: '#fafafa' },
        preview: { width: '100%', height: '160px', objectFit: 'cover', borderRadius: '10px', marginTop: '10px' },
        serviceGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', gap: '20px', marginTop: '16px' },
        serviceCard: { border: '1.5px solid #eee', borderRadius: '16px', overflow: 'hidden', backgroundColor: '#fff' },
        serviceImg: { width: '100%', height: '140px', objectFit: 'cover', backgroundColor: '#f5f5f5' },
        serviceBody: { padding: '14px' },
        serviceTitle: { fontWeight: 'bold', fontSize: '16px', margin: '0 0 6px' },
        serviceMeta: { fontSize: '13px', color: '#888', margin: '2px 0' },
        certCard: { display: 'flex', alignItems: 'center', justifyContent: 'space-between', border: '1.5px solid #eee', borderRadius: '12px', padding: '16px 20px', marginBottom: '12px', backgroundColor: '#fff' },
        certInfo: { display: 'flex', alignItems: 'center', gap: '16px' },
        certTitle: { fontWeight: '700', fontSize: '15px', margin: '0 0 4px' },
        certActions: { display: 'flex', alignItems: 'center', gap: '12px' },
        viewLink: { fontSize: '13px', color: '#0A0A1F', fontWeight: '600', textDecoration: 'underline', cursor: 'pointer' },
        deleteBtn: { background: 'none', border: 'none', color: '#d10b04', cursor: 'pointer', fontSize: '13px', fontWeight: '600' },
    };

    return (
        <div style={styles.page}>
            {/* Header */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
                <h1 style={{ margin: 0 }}>My Portfolio</h1>
            </div>

            {/* Certificates Section */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <h2 style={{ margin: 0 }}>Certificates</h2>
                <button style={styles.addBtn} onClick={() => setShowCertModal(true)}>+ Add Certificate</button>
            </div>

            {certificates.length === 0 ? (
                <p style={{ color: '#999' }}>No certificates uploaded yet.</p>
            ) : (
                certificates.map(cert => (
                    <div key={cert.id} style={styles.certCard}>
                        <div style={styles.certInfo}>
                            <div>
                                <p style={styles.certTitle}>{cert.title}</p>
                                {getStatusBadge(cert.status)}
                                {cert.status === 'REJECTED' && cert.adminNote && (
                                    <p style={{ fontSize: '12px', color: '#888', margin: '4px 0 0' }}>Note: {cert.adminNote}</p>
                                )}
                            </div>
                        </div>
                        <div style={styles.certActions}>
                            <a href={cert.fileUrl} target="_blank" rel="noreferrer" style={styles.viewLink}>View</a>
                            <button style={styles.deleteBtn} onClick={() => handleDeleteCertificate(cert.id)}>Remove</button>
                        </div>
                    </div>
                ))
            )}

            {/* Services Section */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '40px', marginBottom: '16px' }}>
                <h2 style={{ margin: 0 }}>Service Offerings</h2>
                <button style={styles.addBtn} onClick={() => setShowServiceModal(true)}>+ Add Service</button>
            </div>

            {services.length === 0 ? (
                <p style={{ color: '#999' }}>No services listed yet. Click "Add Service" to get started.</p>
            ) : (
                <div style={styles.serviceGrid}>
                    {services.map((service) => (
                        <div key={service.id} style={styles.serviceCard}>
                            {service.imageUrl
                                ? <img src={service.imageUrl} alt={service.title} style={styles.serviceImg} />
                                : <div style={{ ...styles.serviceImg, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#ccc' }}>No Image</div>
                            }
                            <div style={styles.serviceBody}>
                                <p style={styles.serviceTitle}>{service.title}</p>
                                <p style={styles.serviceMeta}>₱{service.ingredientsCost} ingredients cost</p>
                                <p style={styles.serviceMeta}>🍽 Serves {service.servingSize} • ⏱ {service.estPrepTime} mins</p>
                                <button
                                    style={{ ...styles.addBtn, width: '100%', marginTop: '8px', fontSize: '13px', padding: '8px' }}
                                    onClick={() => handleEditClick(service)}
                                >
                                    Edit
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {/* Certificate Upload Modal */}
            {showCertModal && (
                <div style={styles.overlay} onClick={handleCertClose}>
                    <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
                        <button style={styles.closeBtn} onClick={handleCertClose}>✕</button>

                        <h2 style={{ marginTop: 0, fontSize: '28px', fontWeight: 'bold' }}>Upload Certificate</h2>
                        <p style={{ color: '#666', marginBottom: '16px' }}>Upload your culinary certificates for admin verification</p>

                        {certError && <div style={styles.errorMsg}>{certError}</div>}

                        <label style={styles.label}>Certificate Title</label>
                        <input
                            placeholder="e.g. Culinary Arts Diploma"
                            style={styles.input}
                            value={certTitle}
                            onChange={(e) => setCertTitle(e.target.value)}
                        />

                        <label style={styles.label}>Certificate File (PDF or Image)</label>
                        <div style={styles.uploadBox} onClick={() => document.getElementById('certUpload').click()}>
                            {certFile
                                ? <p style={{ margin: 0, color: '#0A0A1F', fontSize: '14px', fontWeight: '600' }}>📄 {certFile.name}</p>
                                : <>
                                    <p style={{ margin: 0, color: '#999', fontSize: '14px' }}>📎 Click to upload a file</p>
                                    <p style={{ margin: '4px 0 0', color: '#bbb', fontSize: '12px' }}>PDF, JPG, PNG up to 10MB</p>
                                  </>
                            }
                            <input
                                id="certUpload"
                                type="file"
                                accept=".pdf,image/*"
                                style={{ display: 'none' }}
                                onChange={handleCertFileChange}
                            />
                        </div>

                        <div style={styles.btnRow}>
                            <span style={styles.cancelLink} onClick={handleCertClose}>Cancel</span>
                            <button
                                style={{ ...styles.submitBtn, opacity: certLoading ? 0.7 : 1 }}
                                onClick={handleCertSubmit}
                                disabled={certLoading}
                            >
                                {certLoading ? "Uploading..." : "Submit for Review"}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Service Creation Modal */}
            {showServiceModal && (
                <div style={styles.overlay} onClick={handleServiceClose}>
                    <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
                        <button style={styles.closeBtn} onClick={handleServiceClose}>✕</button>

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

                        <label style={styles.label}>Service Image (optional)</label>
                        <div style={styles.uploadBox} onClick={() => document.getElementById('imageUpload').click()}>
                            {imagePreview
                                ? <img src={imagePreview} alt="preview" style={styles.preview} />
                                : <>
                                    <p style={{ margin: 0, color: '#999', fontSize: '14px' }}>📷 Click to upload an image</p>
                                    <p style={{ margin: '4px 0 0', color: '#bbb', fontSize: '12px' }}>JPG, PNG up to 10MB</p>
                                  </>
                            }
                            <input id="imageUpload" type="file" accept="image/*" style={{ display: 'none' }} onChange={handleImageChange} />
                        </div>

                        <div style={styles.btnRow}>
                            <span style={styles.cancelLink} onClick={handleServiceClose}>Cancel</span>
                            <button style={{ ...styles.submitBtn, opacity: loading ? 0.7 : 1 }} onClick={handleSubmit} disabled={loading}>
                                {loading ? "Publishing..." : "Publish Service"}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {showEditModal && editingService && (
                <div style={styles.overlay} onClick={() => setShowEditModal(false)}>
                    <div style={styles.modal} onClick={(e) => e.stopPropagation()}>
                        <button style={styles.closeBtn} onClick={() => setShowEditModal(false)}>✕</button>

                        <h2 style={{ marginTop: 0, fontSize: '28px', fontWeight: 'bold' }}>Edit Service</h2>
                        <p style={{ color: '#666', marginBottom: '16px' }}>Update your culinary service details</p>

                        {editError && <div style={styles.errorMsg}>{editError}</div>}

                        <label style={styles.label}>Service Title</label>
                        <input name="title" style={styles.input} onChange={handleEditChange} value={editFormData.title} />

                        <label style={styles.label}>Description</label>
                        <textarea name="description" style={{ ...styles.input, height: '80px', resize: 'none' }} onChange={handleEditChange} value={editFormData.description} />

                        <label style={styles.label}>Ingredients List</label>
                        <textarea name="ingredientsList" style={{ ...styles.input, height: '80px', resize: 'none' }} onChange={handleEditChange} value={editFormData.ingredientsList} />

                        <div style={styles.row}>
                            <div style={{ flex: 1 }}>
                                <label style={styles.label}>Ingredients Cost (₱)</label>
                                <input name="ingredientsCost" type="number" min="0" style={styles.input} onChange={handleEditChange} value={editFormData.ingredientsCost} />
                            </div>
                            <div style={{ flex: 1 }}>
                                <label style={styles.label}>Serving Size</label>
                                <input name="servingSize" type="number" min="1" style={styles.input} onChange={handleEditChange} value={editFormData.servingSize} />
                            </div>
                            <div style={{ flex: 1 }}>
                                <label style={styles.label}>Prep Time (mins)</label>
                                <input name="estPrepTime" type="number" min="1" style={styles.input} onChange={handleEditChange} value={editFormData.estPrepTime} />
                            </div>
                        </div>

                        <label style={styles.label}>Service Image</label>
                        <div style={styles.uploadBox} onClick={() => document.getElementById('editImageUpload').click()}>
                            {editImagePreview
                                ? <img src={editImagePreview} alt="preview" style={styles.preview} />
                                : <>
                                    <p style={{ margin: 0, color: '#999', fontSize: '14px' }}>📷 Click to change image</p>
                                </>
                            }
                            <input id="editImageUpload" type="file" accept="image/*" style={{ display: 'none' }} onChange={handleEditImageChange} />
                        </div>

                        <div style={styles.btnRow}>
                            <span style={styles.cancelLink} onClick={() => setShowEditModal(false)}>Cancel</span>
                            <button style={{ ...styles.submitBtn, opacity: editLoading ? 0.7 : 1 }} onClick={handleEditSubmit} disabled={editLoading}>
                                {editLoading ? "Saving..." : "Save Changes"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}