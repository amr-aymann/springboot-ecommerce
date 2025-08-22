# E-Commerce Backend (Spring Boot)

```
A Java Spring Boot e-commerce application showcasing backend development with
REST APIs, JPA/Hibernate, and MySQL.
```
## Features

- Products & Categories: Add, update, delete, and list products. Organize by
    categories and tags. Prevent duplicate category names.
- Shopping Cart: Add products with multiple quantities, accumulate items, and
    remove products.
- Orders: Place orders linked to cart items, customer, and shipping details. Admin
    endpoints to manage orders.
- Shipping: Validate required shipping information and link data to orders.
- Tags: Add, edit, and delete tags for products.
- Validation & Error Handling: Input validation with meaningful error messages
    and custom exceptions.

## Tech Stack

- Java 17+
- Spring Boot 3+
- Spring Data JPA / Hibernate
- REST APIs
- MySQL (default, can use PostgreSQL)
- Maven
- JUnit 5

## Project Structure

```
src/main/java/com/example/ecommerce
controller # REST Controllers
service # Business Logic
repository # JPA Repositories
model # Entities (Product , Category , Cart ,
Order , Shipping , Tag)
dto # Data Transfer Objects
exception # Custom exceptions & handlers
```

## Getting Started

1. Clone the Repository

```
git clone https :// github.com/amr -aymann/springboot -ecommerce.git
cd springboot -ecommerce
```
2. Configure Database
Create a database in MySQL:
    CREATE DATABASE ecommerce_db;
       Updateapplication.properties:
    spring.datasource.url=jdbc:mysql :// localhost :3306/ ecommerce_db
    spring.datasource.username=YOUR_DB_USER
    spring.datasource.password=YOUR_DB_PASSWORD
    spring.jpa.hibernate.ddl -auto=update
    spring.jpa.show -sql=true
3. Build & Run

```
mvn clean install
mvn spring -boot:run
Application runs at:http://localhost:
```
## Example Endpoints

Products

- POST /api/products→Add product
- GET /api/products→Get all products
- PUT /api/products/{id}→Update product
- DELETE /api/products/{id}→Delete product
Cart
- POST /api/cart/add→Add product to cart (with quantity)
- DELETE /api/cart/remove/{productId}→Remove product from cart
Orders
- POST /api/orders→Place order
- GET /api/orders→Get all orders


## Running Tests

```
mvn test
Includes unit & integration tests for:
```
- Product categories (duplicate handling)
- Shipping form validation
- Tag editing functionality

## Future Enhancements

- User authentication & roles (customer, admin)
- Payment integration (Stripe/PayPal mock)
- Frontend with React/Angular
- Docker support

## Author

Amr Ayman
GitHub — LinkedIn


