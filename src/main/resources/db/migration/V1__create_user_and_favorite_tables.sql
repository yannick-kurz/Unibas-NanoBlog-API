-- Tabelle: users
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabelle: favorites
CREATE TABLE favorites (
                           id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT NOT NULL REFERENCES users(id),
                           wp_post_id BIGINT NOT NULL,
                           created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    -- Stellt sicher, dass ein Post nur einmal favorisiert werden kann
                           UNIQUE (user_id, wp_post_id)
);