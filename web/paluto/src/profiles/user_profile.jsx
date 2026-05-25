import { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../core/context/AuthContext';
import { API_BASE_URL } from '../core/api.js';

export default function UserProfile() {
    const { token, login, user } = useAuth();
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);
    const [isEditing, setIsEditing] = useState(false);

    const [profile, setProfile] = useState({
        firstname: '',
        lastname: '',
        email: '',
        address: '',
        role: '',
    });

    const [formData, setFormData] = useState({
        firstname: '',
        lastname: '',
        address: '',
    });

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const res = await axios.get(`${API_BASE_URL}/api/auth/me`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                const userData = res.data.data.user;
                setProfile(userData);
                setFormData({
                    firstname: userData.firstname,
                    lastname: userData.lastname,
                    address: userData.address,
                });
            } catch (err) {
                console.error("Failed to fetch profile", err);
            } finally {
                setLoading(false);
            }
        };
        fetchProfile();
    }, []);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSave = async () => {
        if (!formData.firstname || !formData.lastname || !formData.address) {
            setError("Please fill in all fields.");
            return;
        }

        setSaving(true);
        setError(null);
        setSuccess(false);

        try {
            await axios.put(
                `${API_BASE_URL}/api/users/profile`,
                formData,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setProfile(prev => ({ ...prev, ...formData }));
            setSuccess(true);
            setIsEditing(false);

            // Update AuthContext so navbar shows updated name
            login({ ...user, firstname: formData.firstname, lastname: formData.lastname }, token);

        } catch (err) {
            setError(err.response?.data?.error?.message || "Failed to update profile.");
        } finally {
            setSaving(false);
        }
    };

    const getInitials = () => {
        return `${profile.firstname?.[0] || ''}${profile.lastname?.[0] || ''}`.toUpperCase();
    };

    const getRoleBadgeColor = () => {
        const colors = {
            CUSTOMER: '#F5A623',
            COOK: '#E53935',
            ADMIN: '#0A0A1F',
        };
        return colors[profile.role] || '#888';
    };

    const styles = {
        wrapper: { padding: '40px', fontFamily: 'Arial, sans-serif', maxWidth: '600px', margin: '0 auto' },
        avatarSection: { display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '40px' },
        avatar: { width: '80px', height: '80px', borderRadius: '50%', backgroundColor: '#0A0A1F', color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: '700', fontSize: '28px', marginBottom: '12px' },
        name: { fontSize: '22px', fontWeight: '800', margin: '0 0 6px' },
        roleBadge: { backgroundColor: getRoleBadgeColor(), color: '#fff', fontSize: '12px', fontWeight: '700', padding: '4px 14px', borderRadius: '20px' },
        section: { backgroundColor: '#fdf8f2', border: '1.5px solid #eee', borderRadius: '16px', padding: '24px', marginBottom: '24px' },
        sectionTitle: { fontSize: '16px', fontWeight: '700', margin: '0 0 20px' },
        field: { marginBottom: '16px' },
        fieldLabel: { fontSize: '13px', color: '#888', marginBottom: '4px', display: 'block' },
        fieldValue: { fontSize: '15px', fontWeight: '600', color: '#0A0A1F' },
        input: { width: '100%', padding: '12px', borderRadius: '12px', border: '1.5px solid #ccc', fontSize: '15px', outline: 'none', boxSizing: 'border-box' },
        emailField: { fontSize: '15px', fontWeight: '600', color: '#888' },
        btnRow: { display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '8px' },
        editBtn: { backgroundColor: '#0A0A1F', color: '#fff', border: 'none', borderRadius: '12px', padding: '12px 32px', fontWeight: '700', fontSize: '14px', cursor: 'pointer' },
        saveBtn: { backgroundColor: '#0A0A1F', color: '#fff', border: 'none', borderRadius: '12px', padding: '12px 32px', fontWeight: '700', fontSize: '14px', cursor: 'pointer' },
        cancelBtn: { backgroundColor: 'transparent', color: '#666', border: '1.5px solid #ccc', borderRadius: '12px', padding: '12px 32px', fontWeight: '700', fontSize: '14px', cursor: 'pointer' },
        errorMsg: { color: '#d10b04', fontSize: '13px', fontWeight: '600', marginBottom: '12px' },
        successMsg: { color: '#28a745', fontSize: '13px', fontWeight: '600', marginBottom: '12px' },
    };

    if (loading) return <div style={styles.wrapper}>Loading profile...</div>;

    return (
        <div style={styles.wrapper}>
            {/* Avatar Section */}
            <div style={styles.avatarSection}>
                <div style={styles.avatar}>{getInitials()}</div>
                <h2 style={styles.name}>{profile.firstname} {profile.lastname}</h2>
                <span style={styles.roleBadge}>{profile.role}</span>
            </div>

            {/* Account Details */}
            <div style={styles.section}>
                <p style={styles.sectionTitle}>Account Details</p>

                {error && <div style={styles.errorMsg}>{error}</div>}
                {success && <div style={styles.successMsg}>Profile updated successfully!</div>}

                {/* Email — always read only */}
                <div style={styles.field}>
                    <span style={styles.fieldLabel}>Email</span>
                    <span style={styles.emailField}>{profile.email} (cannot be changed)</span>
                </div>

                {isEditing ? (
                    <>
                        <div style={styles.field}>
                            <span style={styles.fieldLabel}>First Name</span>
                            <input name="firstname" style={styles.input} value={formData.firstname} onChange={handleChange} />
                        </div>
                        <div style={styles.field}>
                            <span style={styles.fieldLabel}>Last Name</span>
                            <input name="lastname" style={styles.input} value={formData.lastname} onChange={handleChange} />
                        </div>
                        <div style={styles.field}>
                            <span style={styles.fieldLabel}>Address</span>
                            <input name="address" style={styles.input} value={formData.address} onChange={handleChange} />
                        </div>
                        <div style={styles.btnRow}>
                            <button style={styles.cancelBtn} onClick={() => { setIsEditing(false); setError(null); setFormData({ firstname: profile.firstname, lastname: profile.lastname, address: profile.address }); }}>
                                Cancel
                            </button>
                            <button style={{ ...styles.saveBtn, opacity: saving ? 0.7 : 1 }} onClick={handleSave} disabled={saving}>
                                {saving ? "Saving..." : "Save Changes"}
                            </button>
                        </div>
                    </>
                ) : (
                    <>
                        <div style={styles.field}>
                            <span style={styles.fieldLabel}>First Name</span>
                            <span style={styles.fieldValue}>{profile.firstname}</span>
                        </div>
                        <div style={styles.field}>
                            <span style={styles.fieldLabel}>Last Name</span>
                            <span style={styles.fieldValue}>{profile.lastname}</span>
                        </div>
                        <div style={styles.field}>
                            <span style={styles.fieldLabel}>Address</span>
                            <span style={styles.fieldValue}>{profile.address}</span>
                        </div>
                        <div style={styles.btnRow}>
                            <button style={styles.editBtn} onClick={() => setIsEditing(true)}>
                                Edit Profile
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}