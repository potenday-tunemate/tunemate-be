-- +goose Up
CREATE TABLE artist (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    img varchar(500) NOT NULL,
    born_year int NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- +goose Down
DROP TABLE artist;
