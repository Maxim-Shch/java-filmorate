drop table if exists films, users, friends, genres, film_genres, mpa_rating, film_likes;

CREATE TABLE if not exists users (
  user_id integer generated by default as identity PRIMARY KEY,
  user_email varchar(255),
  user_login varchar(255),
  user_name varchar(255),
  user_birthday date
);

CREATE TABLE if not exists friends (
  user_id integer,
  friend_id integer,
  confirmed boolean,

  PRIMARY KEY(user_id, friend_id),

  CONSTRAINT friends_fk FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT friends_fk_1 FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE if not exists mpa_rating (
  rating_id integer generated by default as identity PRIMARY KEY,
  rating varchar,
  rating_description varchar
);

CREATE TABLE if not exists genres (
  genre_id integer generated by default as identity PRIMARY KEY,
  genre_name varchar(255)
);

CREATE TABLE if not exists films (
  film_id integer generated by default as identity PRIMARY KEY,
  name varchar(300),
  description varchar(500),
  release_date date,
  duration integer,
  mpa_id integer,

  CONSTRAINT films_mpa_fk FOREIGN KEY (mpa_id) REFERENCES mpa_rating(rating_id)
);

CREATE TABLE if not exists film_genres (
  film_id integer NOT NULL,
  genre_id integer NOT NULL,

  PRIMARY KEY(film_id, genre_id),

  CONSTRAINT film_id_fk FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
  CONSTRAINT genre_id_fk FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);

CREATE TABLE if not exists film_likes (
  film_id integer NOT NULL,
  user_id integer NOT NULL,

  PRIMARY KEY(film_id, user_id),

  CONSTRAINT film_id_fk_1 FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
