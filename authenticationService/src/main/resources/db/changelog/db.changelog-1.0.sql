-- changeset author:1
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       login VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL
);

-- changeset author:2
CREATE TABLE refresh_tokens (
                                id BIGSERIAL PRIMARY KEY,
                                token VARCHAR(255) NOT NULL,
                                expiry_date TIMESTAMP NOT NULL,
                                user_id BIGINT REFERENCES users(id) ON DELETE CASCADE
);