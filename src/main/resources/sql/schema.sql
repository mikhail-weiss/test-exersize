DROP TABLE IF EXISTS movie_star;
DROP TABLE IF EXISTS star;
DROP TABLE IF EXISTS movie;

DROP SEQUENCE IF EXISTS movie_seq;
DROP SEQUENCE IF EXISTS star_seq;

CREATE TABLE IF NOT EXISTS movie
(
    id           INTEGER PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    release_date DATE,
    CONSTRAINT uniquetitleandreleasedate UNIQUE(title, release_date)
);

CREATE TABLE IF NOT EXISTS star
(
    name VARCHAR(255) NOT NULL UNIQUE,
    id   INTEGER PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS movie_star
(
    movie_id VARCHAR(60),
    star_id  VARCHAR(60),
    PRIMARY KEY (movie_id, star_id),
    FOREIGN KEY (movie_id) REFERENCES movie (id),
    FOREIGN KEY (star_id) REFERENCES star (id)
);

CREATE SEQUENCE IF NOT EXISTS movie_seq AS INTEGER START WITH 1;
CREATE SEQUENCE IF NOT EXISTS star_seq AS INTEGER START WITH 1 INCREMENT BY 50;