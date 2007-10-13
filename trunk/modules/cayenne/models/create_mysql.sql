

/*Simple TODO DEMO
This ERD is used for generating the database for the JPublish Cayenne integration 
demo project and represents a simple db structure to demonstrate some of the basic 
CRUD operations.

(c)2007 Florin T.PATRASCU*/
DROP TABLE IF EXISTS `todos`;
GO

DROP TABLE IF EXISTS `todo_types`;
GO

DROP TABLE IF EXISTS `schema_info`;
GO

DROP TABLE IF EXISTS `users`;
GO




CREATE TABLE `todo_types`
(
`id` INTEGER AUTO_INCREMENT ,
`name` VARCHAR(50) NOT NULL,
`is_default` TINYINT(1) DEFAULT 0,
PRIMARY KEY (`id`)
) TYPE=InnoDB;
GO



CREATE TABLE `schema_info`
(
`id` INTEGER AUTO_INCREMENT ,
`description` VARCHAR(250),
PRIMARY KEY (`id`)
) TYPE=MyISAM;
GO



CREATE TABLE `users`
(
`id` INTEGER AUTO_INCREMENT ,
`name` VARCHAR(50) NOT NULL,
`email` VARCHAR(250) NOT NULL,
PRIMARY KEY (`id`)
) TYPE=InnoDB;
GO



CREATE TABLE `todos`
(
`id` INTEGER AUTO_INCREMENT ,
`description` VARCHAR(250) NOT NULL,
`is_done` TINYINT(1) DEFAULT 0 NOT NULL,
`created_at` DATETIME,
`expires_at` DATETIME,
`user_id` INTEGER NOT NULL,
`type_id` INTEGER NOT NULL,
PRIMARY KEY (`id`)
) TYPE=InnoDB;
GO


ALTER TABLE `todos` ADD FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE;
GO
ALTER TABLE `todos` ADD FOREIGN KEY (`type_id`) REFERENCES `todo_types`(`id`);
GO


INSERT INTO `todo_types`(`name`, `is_default`)
VALUES('personal', 1)
GO
INSERT INTO `todo_types`(`name`, `is_default`)
VALUES('public', 0)
GO
INSERT INTO `todo_types`(`name`)
VALUES('not sure')
GO
INSERT INTO users(id, name, email)
  VALUES(1, 'Paul', 'paul@mail.ca')
GO
INSERT INTO users(id, name, email)
  VALUES(2, 'Joe', 'joe@mail.ca')
GO
INSERT INTO users(id, name, email)
  VALUES(3, 'Simon', 'simon@mail.com')
GO
INSERT INTO todos(id, description, is_done, created_at, expires_at, user_id, type_id)
  VALUES(1, 'remember to download the rest of the JPublish', false, '2007-09-25 19:38:07.0', '2007-10-01 00:00:00.0', 1, 1)
GO
INSERT INTO todos(id, description, is_done, created_at, expires_at, user_id, type_id)
  VALUES(2, 'use Ruby for scripting', false, '2007-09-25 19:38:46.0', '2007-10-01 00:00:00.0', 1, 2)
GO