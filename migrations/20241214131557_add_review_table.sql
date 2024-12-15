-- +goose Up
CREATE TABLE review (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    user bigint NOT NULL REFERENCES user(id),
    album bigint NOT NULL REFERENCES album(id),
    context text NOT NULL,
    created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);
-- +goose Down
DROP TABLE review;

