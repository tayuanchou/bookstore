DELETE FROM book;
ALTER TABLE book AUTO_INCREMENT = 1001;

DELETE FROM category;
ALTER TABLE category AUTO_INCREMENT = 1001;

INSERT INTO `category` (`name`) VALUES ('TopPicks'),('Textbooks'),('Fictions'),('Kids');

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Sweetness of Water', 'Nathan Harris', '', 23.83, 0, TRUE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Crying in H Mart', 'Michelle Zauner', '', 10.44, 0, TRUE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Twisted Love', 'Ana Huang', '', 12.06, 0, FALSE, FALSE, 1001);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('All the Light We Cannot See', 'Anthony Doerr', '', 6.06, 0, TRUE, FALSE, 1001);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Java Programming', 'Joyce Farrell', '', 13.31, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('R for Data Science', 'Garret Wickham, Garret Grolemund', '', 39.79, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Web Development with Django', 'Ben Shaw, Saurabh Badhwar, Chris Guest', '', 45.99, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('C++ Primer', 'Stanley B Lippman', '', 56.34, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Statistics', 'David Freedman', '', 14.95, 0, TRUE, FALSE, 1002);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Grokking Deep Learning', 'Andrew Trask', '', 42.36, 0, FALSE, FALSE, 1002);


INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Dune', 'Frank Herbert', '', 5.25, 0, TRUE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Star Wars', 'Timothy Zahn', '', 17.45, 0, TRUE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Mist', 'Stephen King', '', 7.99, 0, FALSE, FALSE, 1003);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Catch-22', 'Joseph Heller', '', 5.99, 0, FALSE, FALSE, 1003);

INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Everyone Poops', 'Taro Gomi', '',9.19, 0, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Curious Garden', 'Peter Brown', '', 11.89, 0, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('Dragons Love Tacos', 'Adam Rubin', '', 5.43, 0, TRUE, FALSE, 1004);
INSERT INTO `book` (title, author, description, price, rating, is_public, is_featured, category_id) VALUES ('The Day the Crayons Quit', 'Drew Daywalt', '', 10.81, 0, TRUE, FALSE, 1004);