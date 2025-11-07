--liquibase formatted sql

-- changeset author:20251029

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    birth_date DATE,
    email VARCHAR(255) NOT NULL UNIQUE
    );

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

CREATE TABLE IF NOT EXISTS card_info (
                                         id BIGSERIAL PRIMARY KEY,
                                         user_id BIGINT NOT NULL,
                                         number VARCHAR(20) NOT NULL,
    holder VARCHAR(200) NOT NULL,
    expiration_date DATE,
    CONSTRAINT fk_cardinfo_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE
    );
