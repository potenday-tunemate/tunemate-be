-- +goose Up
CREATE TABLE album(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    title varchar(300) NOT NULL,
    cover_img varchar(500) NOT NULL,
    artist bigint NOT NULL REFERENCES artist(id),
    year int NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- +goose Down
DROP TABLE album;
