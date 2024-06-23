INSERT INTO brands(name, enabled)
VALUES ('smth', true);

INSERT INTO users(username, password, phone_number, email)
VALUES ('user',
        'password',
        '044',
        'gmail');

WITH new_feed AS (
    INSERT INTO feeds(id) VALUES ('1b952ec3-5e6a-404d-bacf-224945863dd9')
        RETURNING id
)

INSERT INTO users (username, password, phone_number, email, feed_id)
SELECT 'exampleUser', 'examplePassword', '123-456-7890', 'example@example.com', id
FROM new_feed;

INSERT INTO users_followers(user_id, follower_id)
VALUES (1, 2);

INSERT INTO users_permissions(user_id, permission)
VALUES (1, 'BO_READ');

INSERT INTO users_brands_subscriptions(user_id, brand_id)
VALUES (1, 1);
INSERT INTO users_brands_affiliation(user_id, brand_id)
VALUES (1, 1);
INSERT INTO users_brands_management(user_id, brand_id)
VALUES (1, 1);