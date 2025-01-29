# 📚 BookNestApp - User & Book Management System  

## 🚀 Overview  
**BookNestApp** is a **Spring Boot** application for managing users and books with authentication, database interactions (**Hibernate, JPA & JDBC**), and caching for performance.  

## ✅ Features  
- **User Management**: CRUD operations, authentication, and role-based authorization  
- **Book Management**: Parent-child relational data handling
- **Database Communication**: ORM mapping with Hibernate & JPA 
- **REST API**: Endpoints for managing users & books 
- **Unit Testing**: JUnit & Mockito for service, repository, and controllers 
- **Multilingual & Task Scheduling**: For complex features 

## 🛠 Tech Stack  
- **Spring Boot, Security, Data JPA, JDBC**  
- **H2/PostgreSQL/MySQL**  
- **JUnit & Mockito** for testing  

## 🔍 Key API Endpoints  
- `POST /api/users` → Register user  
- `GET /api/users/{email}` → Get user  
- `GET /api/users/status/{firstname}` → Fetch users by first name (**JDBC**)  
- `GET /api/books` → List books  

