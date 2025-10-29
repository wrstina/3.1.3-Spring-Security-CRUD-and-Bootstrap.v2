USE spring_security_db;
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    age INT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS roles (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(45) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT,
                                          role_id INT,
                                          PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
    );

-- роли
INSERT INTO roles (name) VALUES ('ROLE_USER')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO roles (name) VALUES ('ROLE_ADMIN')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

-- пользователи (пароли — bcrypt)
INSERT INTO users (username, password, email, age)
VALUES
    ('Punya', '$2a$10$O6ECZxmVqhUUl6Qv/IoSteMlYdwpbqVEs9TEyMNtr2zdTEDpG0F8e', 'dota@mail.ru', 5),
    ('Pudge', '$2a$10$LufXtk/afjVS/HuBhTG8z.OGagZEBPXVgtskQYyJ/5N72GbVcyOT6', 'dota2@mail.ru', 119)
    ON DUPLICATE KEY UPDATE email = VALUES(email), age = VALUES(age), password = VALUES(password);

-- связи пользователь–роль
INSERT IGNORE INTO user_roles (user_id, role_id)
VALUES
((SELECT id FROM users WHERE username='Punya'), (SELECT id FROM roles WHERE name='ROLE_USER')),
((SELECT id FROM users WHERE username='Pudge'), (SELECT id FROM roles WHERE name='ROLE_ADMIN')),
((SELECT id FROM users WHERE username='Pudge'), (SELECT id FROM roles WHERE name='ROLE_USER'));