-- +goose Up
CREATE TABLE review_tag (
	id INT primary key auto_increment,
	tag_id INT,
	review_id INT
);
-- +goose Down
DROP TABLE review_x_tag;
