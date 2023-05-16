    CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT NULL,
    email varchar(512) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email));

    CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(200) NOT NULL,
    description varchar(2000) NOT NULL,
    is_available boolean NOT NULL,
    user_id BIGINT NOT NULL,
    item_request_id BIGINT,
    CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users(id));

