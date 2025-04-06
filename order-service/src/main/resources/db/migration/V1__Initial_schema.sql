CREATE TABLE orders (
	id BIGSERIAL PRIMARY KEY NOT NULL,
	name varchar(255) NOT NULL,
	price float8,
	quantity int NOT NULL,
	status varchar(255) NOT NULL,
	created_date timestamp NOT NULL,
	last_modified_date timestamp NOT NULL,
	version integer NOT NULL
);