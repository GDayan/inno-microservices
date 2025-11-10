# Authentication Service

This service provides authentication and authorization functionality for users using JWT tokens. It is built with **Spring Boot**, **Spring Security**, **PostgreSQL**, and **Redis**.

## Features

- User registration with **login** and **password**
- Passwords are **hashed using BCrypt** with unique salt for each password
- **JWT authentication**:
    - Access token
    - Refresh token
- Endpoints for token management:
    - **Create token (login)**
    - **Validate token**
    - **Refresh token**
- **Authorization** for all endpoints using JWT
- **Exception handling** for security errors
- Dockerized application for easy deployment

## Technologies

- Java 23 / Spring Boot 3.5.7
- Spring Security
- PostgreSQL
- Redis
- Liquibase for DB migrations
- Docker & Docker Compose