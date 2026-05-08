# Paluto: A Service-Based Booking Marketplace 🍳

**Connecting Local Culinary Talent with the Community.**

Paluto is a distributed full-stack ecosystem designed to bridge the gap between home-based cooks and customers. It manages the complete lifecycle of a professional food service—from cloud-hosted media management and dynamic pricing to a high-integrity, payment-validated booking system.

---

## 🏗 System Architecture

The system follows a **decoupled, cloud-synced architecture**, ensuring that the application remains functional and consistent regardless of where the server or client is running.

* **Frontend (The Interface):** A React-based SPA that manages user sessions and provides a real-time dashboard for both Cooks and Customers.
* **Backend (The Engine):** A Spring Boot REST API that implements the core business logic, including a **Strategy Design Pattern** for calculating service costs.
* **Database (The Core):** Supabase (PostgreSQL) hosted in the cloud, utilizing a **Transaction Pooler** (Port 6543) for stable connectivity across restrictive networks.
* **Cloud Storage (Media):** Supabase Storage Buckets used for high-performance delivery of service and profile imagery.
* **Payment Gateway:** PayMongo integration using an asynchronous **Webhook** architecture.

---

## 🚀 Key Technical Features

### 1. Payment-Validated Booking Lifecycle

Unlike traditional systems that create "Pending" records, Paluto implements a **High-Integrity Booking Flow**. A booking record is only committed to the permanent ledger (Supabase) upon successful payment verification:

* **The Handshake:** When a user initiates a booking, the system generates a PayMongo checkout session.
* **The Trigger:** The database remains untouched until the PayMongo **Webhook** signals a successful transaction.
* **The Record:** Only after this signal does the `BookingService` initialize the record with a `PAID_PENDING` status. This prevents "database bloat" from abandoned carts or failed payments.

### 2. Cloud-Based Media Management (Supabase Buckets)

Instead of storing bulky images in the database, Paluto utilizes **Supabase Storage**:

* **Decoupled Uploads:** The Frontend handles direct multi-part uploads to the `service-images` bucket.
* **Efficiency:** Only the **Public URL** is stored in the PostgreSQL database, significantly reducing database size and improving load times through a global CDN.

### 3. Dynamic Pricing Strategy

The system implements the **Strategy Pattern** to dynamically calculate costs based on scale:

* **Standard Strategy:** Optimized for single-order quantities.
* **Scaled Strategy:** Applied for bulk/group orders, adjusting labor costs and prep time relative to the order's complexity.

---

## 🛠 Tech Stack Details

| Layer | Technology | Purpose |
| --- | --- | --- |
| **Frontend** | React.js | Responsive UI & State Management |
| **Backend** | Spring Boot | Business Logic & API Orchestration |
| **Database** | Supabase (PostgreSQL) | Cloud-Native Data Persistence |
| **Storage** | Supabase Buckets | Scalable File & Image Hosting |
| **Payment** | PayMongo API | Secure PH-based Payment Processing |
| **Tunneling** | Ngrok | Local-to-Cloud Webhook Bridge |

---

## 👨‍💻 Engineering Intuition

This project emphasizes **System Reliability** and **Data Cleanliness**:

* **Atomic Validation:** By only recording paid bookings, the system ensures that every entry in the `booking` table represents real revenue and confirmed schedules.
* **Port 6543:** Chosen to bypass school firewalls (like CIT-U Wi-Fi) that often block the standard Postgres port.
* **Escaped Identifiers:** Strategic use of double-quoting (`"user"`, `"booking"`) to manage PostgreSQL's strict reserved-word policy.

---

*Developed as a Solo Project for the BSIT Program CSIT342 course at Cebu Institute of Technology – University.*
