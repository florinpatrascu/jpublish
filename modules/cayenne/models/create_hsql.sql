CREATE TABLE users (email VARCHAR(250) NOT NULL, id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50) NOT NULL);
CREATE TABLE todo_types (id BIGINT AUTO_INCREMENT PRIMARY KEY, is_default BIT NULL, name VARCHAR(50) NOT NULL);
CREATE TABLE schema_info (description VARCHAR(250) NULL, id BIGINT AUTO_INCREMENT PRIMARY KEY);
CREATE TABLE todos (created_at TIMESTAMP NULL, description VARCHAR(250) NOT NULL, expires_at TIMESTAMP NULL, id BIGINT AUTO_INCREMENT PRIMARY KEY, is_done BIT NOT NULL, type_id INTEGER NOT NULL, user_id INTEGER NOT NULL);
ALTER TABLE todos ADD CONSTRAINT C_todos_144248387 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE todos ADD CONSTRAINT C_todos_144248387 FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
CREATE TABLE AUTO_PK_SUPPORT (  TABLE_NAME CHAR(100) NOT NULL,  NEXT_ID INTEGER NOT NULL,  PRIMARY KEY(TABLE_NAME));

DELETE FROM AUTO_PK_SUPPORT WHERE TABLE_NAME IN ('schema_info', 'todo_types', 'todos', 'users');
INSERT INTO AUTO_PK_SUPPORT (TABLE_NAME, NEXT_ID) VALUES ('schema_info', 1);
INSERT INTO AUTO_PK_SUPPORT (TABLE_NAME, NEXT_ID) VALUES ('todo_types', 4);
INSERT INTO AUTO_PK_SUPPORT (TABLE_NAME, NEXT_ID) VALUES ('todos', 3);
INSERT INTO AUTO_PK_SUPPORT (TABLE_NAME, NEXT_ID) VALUES ('users', 4);
	
INSERT INTO todo_types (name, is_default) VALUES ('personal', 1);
INSERT INTO todo_types (name, is_default) VALUES ('public', 0);
INSERT INTO todo_types (name, is_default) VALUES ('not sure', 0);
	
INSERT INTO users (name, email) VALUES( 'Paul', 'paul@mail.ca');
INSERT INTO users (name, email) VALUES( 'Joe', 'joe@mail.ca');
INSERT INTO users (name, email) VALUES( 'Simon', 'simon@mail.com');
INSERT INTO todos ( description, is_done, created_at, expires_at, user_id, type_id) VALUES ('remember to download the rest of the JPublish', 0, '2007-09-25 19:38:07.0', '2007-10-01 00:00:00.0', 1, 1);
INSERT INTO todos ( description, is_done, created_at, expires_at, user_id, type_id) VALUES ('use Ruby for scripting', 0, '2007-09-25 19:38:46.0', '2007-10-01 00:00:00.0', 1, 2);
