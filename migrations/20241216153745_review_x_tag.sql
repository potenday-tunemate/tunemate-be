-- +goose Up
-- +goose StatementBegin
CREATE TABLE review_x_tag (
	id INT primary key auto_increment,
	tag_id INT,
	review_id INT
);
-- +goose StatementEnd

-- +goose Down
-- +goose StatementBegin
DROP TABLE review_x_tag;
-- +goose StatementEnd
