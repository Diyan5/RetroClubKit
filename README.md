👕 RetroClubKit

**RetroClubKit** is a full-stack web application dedicated to retro football jersey lovers. It blends nostalgia with modern eCommerce functionality, enabling users to explore, shop, and interact in a clean and intuitive platform.


 🛠 Tech Stack
**Back-End:**
- Java 17, Spring Boot
- Spring Security
- Spring Data JPA
- MySQL

**Front-End:**
- HTML, CSS, JavaScript 

**Notifications Microservice:**
- REST API with Spring Boot
- Separate database
- Notification preferences
- Contact messages to admin

**Testing:**
- JUnit, Mockito, Spring Boot Test, MockMvc

**Deployment:**
- Maven

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

## 📸 Screenshots
- 🖼 Home Page


![Screenshot 2025-04-01 153646](https://github.com/user-attachments/assets/624ef2ea-f8fe-4b21-a2e0-9006a7f7b6a4)


- 🖼 Product Catalog


![Screenshot 2025-04-01 155905](https://github.com/user-attachments/assets/98722234-602a-474f-ae93-f8b6e3accbde)


- 🖼 Cart

  
![Screenshot 2025-04-01 153846](https://github.com/user-attachments/assets/49b52ac2-2f27-4adc-9b64-e56a8c56f4d4)


- 🖼 Contact Form  

![Screenshot 2025-04-01 153922](https://github.com/user-attachments/assets/5bddc942-6f0f-469c-b376-a36adeaf41be)


