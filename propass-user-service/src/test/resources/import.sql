DELETE FROM users; -- reset db state

INSERT INTO users (id, first_name, last_name, email, enc_password) VALUES (1, 'John', 'Doe', 'john@doe.com', '');
INSERT INTO users (id, first_name, last_name, email, enc_password) VALUES (2, 'John', 'Wick', 'john@wick.com', '');