CREATE TABLE users
(
    id           BIGSERIAL PRIMARY KEY,
    username     VARCHAR NOT NULL,
    password     VARCHAR NOT NULL,
    phone_number VARCHAR NOT NULL,
    email        VARCHAR NOT NULL,
    feed_id      UUID    NULL
);

CREATE TABLE images
(
    id      BIGSERIAL PRIMARY KEY,
    data    OID    NOT NULL,
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
    id         UUID PRIMARY KEY,
    url        VARCHAR   NOT NULL,
    brand_id   BIGINT    NOT NULL,
    used       BOOLEAN   NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (brand_id) REFERENCES brands (id)
);

CREATE TABLE posts
(
    id               UUID PRIMARY KEY,
    user_id          BIGINT    NULL,
    brand_id         BIGINT    NULL,
    author_type      VARCHAR   NOT NULL,
    text_content     VARCHAR   NOT NULL,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    visible          BOOLEAN   NOT NULL,
    publication_time TIMESTAMP NULL,
    view_conditions  JSONB     NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (brand_id) REFERENCES brands (id)
);

CREATE TABLE feeds
(
    id UUID PRIMARY KEY
);

CREATE TABLE feeds_posts
(
    feed_id UUID NOT NULL,
    post_id uuid NOT NULL,
    PRIMARY KEY (feed_id, post_id),
    FOREIGN KEY (feed_id) REFERENCES feeds (id),
    FOREIGN KEY (post_id) REFERENCES posts (id)
);

CREATE TABLE users_permissions
(
    user_id    BIGINT  NOT NULL,
    permission VARCHAR NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE users_followers
(
    user_id     BIGINT NOT NULL,
    follower_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, follower_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (follower_id) REFERENCES users (id)
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

CREATE TABLE users_subscribed
(
    user_id       BIGINT NOT NULL,
    subscribed_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, subscribed_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (subscribed_id) REFERENCES users (id)
);