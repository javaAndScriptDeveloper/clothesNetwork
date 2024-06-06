CREATE TABLE users
(
    id           BIGSERIAL PRIMARY KEY,
    username     VARCHAR NOT NULL,
    password     VARCHAR NOT NULL,
    phone_number VARCHAR NOT NULL,
    email        VARCHAR NOT NULL
);

CREATE TABLE users_permissions
(
    user_id         BIGINT      NOT NULL,
    permission      VARCHAR     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE images
(
    id      BIGSERIAL PRIMARY KEY,
    data    TEXT   NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE brands
(
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR NOT NULL,
    enabled BOOLEAN NOT NULL
);

CREATE TABLE invites
(
    id       UUID PRIMARY KEY,
    url      VARCHAR NOT NULL,
    brand_id BIGINT  NOT NULL,
    used     BOOLEAN NOT NULL,
    FOREIGN KEY (brand_id) REFERENCES brands (id)
);

CREATE TABLE users_brands_subscriptions
(
    user_id  BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, brand_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (brand_id) REFERENCES brands (id)
);

CREATE TABLE users_brands_affiliation
(
    user_id  BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, brand_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (brand_id) REFERENCES brands (id)
);

CREATE TABLE users_brands_management
(
    user_id  BIGINT NOT NULL,
    brand_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, brand_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (brand_id) REFERENCES brands (id)
);

INSERT INTO brands(name, enabled)
VALUES ('smth', true);

INSERT INTO users(username, password, phone_number, email)
VALUES (
        'user',
        'password',
        '044',
        'gmail'
       );

INSERT INTO users_permissions(user_id, permission)
VALUES (
        1, 'BO_READ'
       );

INSERT INTO users_brands_subscriptions(user_id, brand_id) VALUES (1, 1);
INSERT INTO users_brands_affiliation(user_id, brand_id) VALUES (1, 1);
INSERT INTO users_brands_management(user_id, brand_id) VALUES (1, 1);


/*

CREATE TABLE chats
(
    id             BIGSERIAL PRIMARY KEY,
    main_user_id   BIGINT REFERENCES users (id) NOT NULL,
    participant_id BIGINT REFERENCES users (id) NOT NULL
);

CREATE TABLE messages
(
    id          BIGSERIAL PRIMARY KEY,
    receiver_id BIGINT REFERENCES users (id),
    sender_id   BIGINT REFERENCES users (id),
    chat_id     BIGINT REFERENCES chats(id),
    text        VARCHAR NOT NULL
);

 */