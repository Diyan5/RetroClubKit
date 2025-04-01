👕 RetroClubKit

**RetroClubKit** is a full-stack web application dedicated to retro football jersey lovers. It blends nostalgia with modern eCommerce functionality, enabling users to explore, shop, and interact in a clean and intuitive platform.


 🛠 Tech Stack
**Back-End:**
- Java 17, Spring Boot
- Spring Security
- Spring Data JPA
- MySQL

**Front-End:**
- HTML, CSS, JavaScript (ES6)
- Responsive Design

**Notifications Microservice:**
- REST API with Spring Boot
- Separate database
- Notification preferences
- Contact messages to admin

**Testing:**
- JUnit, Mockito, Spring Boot Test, MockMvc

**Deployment:**
- Maven
- (Planned) Docker + Cloud deployment

**Architecture:**
- Monolith + Notification Microservice (transitioning to Microservices)

---

## 🎯 About RetroClubKit

RetroClubKit allows users to:

✅ Browse and purchase retro football kits from legendary clubs  
✅ Use an interactive cart powered by JavaScript and `localStorage`  
✅ Receive order confirmation via email  
✅ Contact the admin through an integrated contact form  
✅ Manage personal notification preferences  
✅ Get inactivity warnings and automatic account deactivation  

---

## 📦 Key Features

### 🛒 Shopping Cart & Orders
- Add products by team, size, and quantity
- View and edit cart dynamically
- Send orders via `fetch` POST request
- Receive email confirmations

### 📬 Contact Form
- Users can send messages directly to the admin
- Stored and processed through the notification microservice

### 🔔 Notifications & Preferences
- Separate REST microservice
- Enable/disable notifications per type
- Supports user-to-admin communication

### 👤 User Accounts
- Registration & login
- Profile editing
- Automatic activity tracking

---

## 🔌 Integrations & Planned Features

🚀 Email service for order confirmation & inactivity  
🔒 Spring Security for robust account protection  
🧪 Unit & integration tests with high coverage  
📥 Admin panel for product and order management *(coming soon)*  
📦 Docker, CI/CD & public deployment *(in progress)*  

---

## 🌍 Public Area

### 🏠 Home Page
- Available to both guests and registered users
- Preview some products and promotions

### 📋 Contact Form
- Open form for sending feedback or inquiries

---

## 🔐 Private Area (after login)

### 👕 Product Catalog
- Explore all available retro kits
- Filter by team, size, price

### 🛒 Cart
- Built with localStorage + JavaScript
- Interactive UI

### ✅ Order Confirmation
- Send an order request with selected shirts
- Receive email confirmation

### 🔔 Notification Preferences
- Enable/disable notifications
- Managed via microservice

---

## 📸 Screenshots *(coming soon)*
- 🖼 Home Page  
- 🖼 Product Catalog  
- 🖼 Cart  
- 🖼 Notification Settings  
- 🖼 Contact Form  

---

## 🔧 Run Locally

```bash
git clone https://github.com/Diyan5/RetroClubKit.git
cd RetroClubKit
./mvnw spring-boot:run
