import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { API_BASE_URL } from "../core/api.js";

export default function AdminHomepage() {
    const navigate = useNavigate();
    const [stats, setStats] = useState({ pendingCount: 0, totalSuccess: 0, totalRevenue: 0 });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchDashboardData = async () => {
            const token = localStorage.getItem("token");
            try {
                // Fetch aggregate metrics from your backend dashboard endpoints
                const response = await axios.get(`${API_BASE_URL}/api/admin/dashboard-stats`, { 
                    headers: { Authorization: `Bearer ${token}` } 
                });
                
                // Expecting backend to return payload keys matching our layout tracker state
                if (response.data.success) {
                    setStats(response.data.data);
                }
            } catch (err) {
                console.error("Failed to compile admin metrics", err);
            } finally {
                setLoading(false);
            }
        };
        fetchDashboardData();
    }, []);

    if (loading) return <div style={styles.wrapper}>Loading Admin Security Console...</div>;

    return (
        <div style={styles.wrapper}>
            <h2 style={styles.title}>ADMINISTRATOR DASHBOARD</h2>
            
            {/* DYNAMIC THREE-CARD ANALYTICS ROW */}
            <div style={styles.cardRow}>
                {/* CARD 1: COMPLIANCE BACKLOG (Links directly to your certificate review page) */}
                <div style={styles.statCard} onClick={() => navigate("/admin/cook-certificates")}>
                    <p style={styles.cardLabel}>Pending Certificate Approval</p>
                    <h3 style={styles.cardValue}>{stats.pendingCount}</h3>
                </div>

                {/* CARD 2: SYSTEM SUCCESS FREQUENCY */}
                <div style={styles.statCard}>
                    <p style={styles.cardLabel}>Completed Bookings</p>
                    <h3 style={styles.cardValue}>{stats.totalSuccess}</h3>
                </div>

                {/* CARD 3: THE "OTHER" - PLATFORM REVENUE VOLUME */}
                <div style={styles.statCard}>
                    <p style={styles.cardLabel}>Gross Platform Revenue</p>
                    <h3 style={styles.cardValue}>
                        PHP {stats.totalRevenue.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                    </h3>
                </div>
            </div>
        </div>
    );
}

const styles = {
    // 🚀 FIXED: Enforce absolute screen bounds box sizing to lock padding-overflow breaks
    wrapper: { 
        padding: "40px", 
        backgroundColor: "#fdf8f2", 
        boxSizing: "border-box", 
        fontFamily: "Arial, sans-serif",
        display: "flex",
        flexDirection: "column",
        overflow: "hidden"
    },
    // 🚀 FIXED: Balanced title constraints to prevent pushing cards below bounds
    title: { fontSize: "36px", fontWeight: "900", margin: "0 0 30px", flexShrink: 0 },
    cardRow: { display: "flex", gap: "24px", width: "100%" },
    statCard: { 
        flex: 1, 
        backgroundColor: "#000000", 
        padding: "24px", 
        borderRadius: "16px", 
        border: "1.5px solid #eee", 
        boxShadow: "0 4px 6px rgba(0,0,0,0.01)",
        cursor: "pointer",
        transition: "transform 0.2s ease",
        display: "flex",
        flexDirection: "column",
        justifyContent: "space-between",
        minHeight: "140px"
    },
    cardValue: { fontSize: "40px", fontWeight: "800", margin: "0 0 4px", color: "#ffffff" },
    cardLabel: { fontSize: "24px", color: "#ffffff", margin: "0 0 12px", fontWeight: "600" },
    linkText: { fontSize: "12px", fontWeight: "700", color: "#FF8C00", textTransform: "uppercase" },
    quickActions: { backgroundColor: "#fff", padding: "28px", borderRadius: "16px", border: "1.5px solid #eee" },
    navBtn: { backgroundColor: "#1a1a1a", color: "#fff", border: "none", padding: "12px 24px", borderRadius: "8px", fontWeight: "600", fontSize: "14px", cursor: "pointer" }
};