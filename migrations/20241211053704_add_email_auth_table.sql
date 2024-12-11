-- +goose Up
CREATE TABLE email_auth (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    email varchar(100) NOT NULL UNIQUE,
    token varchar(255) NOT NULL UNIQUE,
    expired_at timestamp NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- +goose Down
DROP TABLE email_auth;
