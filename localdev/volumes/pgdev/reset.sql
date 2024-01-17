\connect postgres

DROP SCHEMA IF EXISTS test CASCADE;
CREATE SCHEMA test;

CREATE TABLE test.author (
  id SERIAL NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  created TIMESTAMP with time zone NOT NULL DEFAULT now(),

  CONSTRAINT pk_author PRIMARY KEY (id)
);

CREATE TABLE test.book (
  id SERIAL NOT NULL,
  author_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  pages INT2 NULL,

  CONSTRAINT pk_book PRIMARY KEY (id),
  CONSTRAINT fk_book_author FOREIGN KEY (id) REFERENCES test.author
);
