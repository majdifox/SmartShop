SmartShop
A comprehensive e-commerce management system built with Spring Boot, providing complete order management, payment processing, and customer loyalty features.

Features
User Management
Role-based authentication (Admin/Client)
Session-based security
User registration and profile management
Product Management
CRUD operations for products
Soft delete functionality
Stock management
Pagination support
Order Management
Create and manage orders
Apply promotional codes
Automatic discount calculation based on customer tier
TVA (VAT) calculation
Order status tracking (PENDING, PAID, CANCELLED)
Payment System
Multiple payment methods (Cash, Check, Bank Transfer)
Payment status tracking (PENDING, ENCASHED, REJECTED)
Cash payment limit (20,000 DH)
Payment encashment and rejection workflows
Automatic order status updates
Customer Loyalty Program
Four tier levels: BASIC, SILVER, GOLD, PLATINUM
Automatic tier upgrades based on orders and spending
Tier-based discounts on orders
Discount increases with higher order amounts
Promotional Codes
Create and manage promo codes
Percentage-based discounts
Active/inactive status control
One-time use per order
Tech Stack
Framework: Spring Boot 4.0.0
Language: Java 17
Database: MySQL
ORM: JPA/Hibernate
Security: Session-based authentication
Validation: Jakarta Bean Validation
Mapping: MapStruct 1.6.3
Build Tool: Maven
Utilities: Lombok
Architecture
The project follows a layered architecture:

Controllers: REST API endpoints
Services: Business logic layer
Repositories: Data access layer
DTOs: Data transfer objects (Request/Response)
Mappers: Entity-DTO conversion
Entities: JPA entities
Exceptions: Custom exception handling
API Endpoints
Authentication
POST /api/auth/login - User login
POST /api/auth/logout - User logout
GET /api/auth/me - Get current user
Products
GET /api/products - List all products (paginated)
GET /api/products/{id} - Get product by ID
POST /api/products - Create new product (Admin only)
PUT /api/products/{id} - Update product (Admin only)
DELETE /api/products/{id} - Soft delete product (Admin only)
Orders
GET /api/orders - List all orders (Admin only)
GET /api/orders/{id} - Get order details
GET /api/orders/me - Get current user's orders
POST /api/orders - Create new order
PUT /api/orders/{id}/cancel - Cancel order
Payments
POST /api/payments - Record payment
GET /api/payments/{id} - Get payment details
GET /api/payments/order/{orderId} - Get payments by order
PUT /api/payments/{id}/encash - Encash payment
PUT /api/payments/{id}/reject - Reject payment
Promo Codes
GET /api/promo-codes - List all promo codes
GET /api/promo-codes/{id} - Get promo code details
POST /api/promo-codes - Create promo code
PUT /api/promo-codes/{id} - Update promo code
PUT /api/promo-codes/{id}/deactivate - Deactivate promo code
Clients
GET /api/clients - List all clients
GET /api/clients/{id} - Get client details
GET /api/clients/me - Get current client profile
POST /api/clients - Create new client
PUT /api/clients/{id} - Update client profile
Business Rules
Payment Rules
Cash payments are automatically encashed
Cash payments cannot exceed 20,000 DH
Check and transfer payments require bank information
Payments update order remaining amount
Rejecting a payment restores the order amount
Discount Rules
Customer tier discounts are cumulative based on order amount
Promo codes provide additional percentage discounts
Tier upgrades happen automatically after order completion
Tier Requirements
SILVER: 3 orders or 1,000 DH total spent
GOLD: 10 orders or 5,000 DH total spent
PLATINUM: 20 orders or 15,000 DH total spent
Tier Discounts
Base discounts: PLATINUM (15%), GOLD (10%), SILVER (5%)
Additional discounts for high-value orders
Promo codes stack with tier discounts
Configuration
Key configurations in application.properties:

spring.application.name=smartShop
spring.datasource.url=jdbc:mysql://localhost:3306/smartshop
spring.jpa.hibernate.ddl-auto=update
app.tva.rate=0.20
Getting Started
Clone the repository
Configure MySQL database
Update application.properties with your database credentials
Run mvn spring-boot:run
Access the API at http://localhost:8080
Database Schema
The application uses the following main entities:

User (authentication)
Client (customer profile)
Product (inventory)
Order (purchase orders)
OrderItem (order line items)
Payment (payment records)
PromoCode (promotional discounts)
Error Handling
The application provides structured error responses:

400 Bad Request - Validation errors
401 Unauthorized - Authentication required
403 Forbidden - Insufficient permissions
404 Not Found - Resource not found
409 Conflict - Data integrity violations
500 Internal Server Error - Server errors
Testing
Unit tests are provided for service layers, ensuring business logic correctness.