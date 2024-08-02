INSERT INTO users (id, login, email, password) VALUES
                                               (1, 'John Doe', 'john.doe@example.com', '$2a$10$Cq4nQiGlSGMSyq5c8G9p7.J7pvJ6gWlo0zNHhsXrRBr/FSfNS2Kl6'),
                                               (2, 'admin', 'admin@example.com', '$2a$10$97PAm86ZzNJ7cirzEP/j.O9HISb8ksCHFy.9JHBAU0670BfDJqq4.');

INSERT INTO user_to_role (user_id, user_role) VALUES
                                                  (1, 1),
                                                  (2, 8);