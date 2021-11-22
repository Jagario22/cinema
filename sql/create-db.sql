/*/*----------------------------------------films--------------------------------------------------*/
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'cinema'
  AND pid <> pg_backend_pid();
DROP DATABASE IF EXISTS cinema;
CREATE DATABASE "cinema"
    WITH OWNER "postgres"
    ENCODING 'UTF8';

drop table if exists tickets;
drop table if exists sessions;
drop table if exists ticket_types;
drop table if exists wallets;
drop table if exists users;
drop table if exists genres_films;
drop table if exists films;
drop table if exists genres;


create table genres
(
    id   serial primary key,
    name varchar(30) unique not null
);


create table films
(
    id        serial primary key,
    title     character varying(100) not null,
    len       integer check (len > 0 AND len <= 240),
    year_prod varchar(4)             not null,
    category  integer check (category = 0 or
                             category = 12 or
                             category = 16),
    descr     varchar(3000)          not null,
    rating    numeric(3, 1) check (rating > 0 and rating <= 10),
    img       varchar(300) not null unique ,
    unique (title, year_prod, descr, img)
);

create table genres_films
(
    film_id  integer REFERENCES films (id) ON DELETE CASCADE,
    genre_id integer REFERENCES genres (id) ON DELETE CASCADE,
    unique (film_id, genre_id)
);

/*----------------------------------------session--------------------------------------------------*/


create type role_type as enum ('admin', 'user');
create table users
(
    id       serial primary key,
    email    varchar(30) unique,
    password varchar(32),
    check (length(password) >= 8),
    login    varchar(16) unique check (length(login) >= 3),
    role     role_type
);


create table wallets
(
    id      serial primary key,
    user_id integer unique,
    balance decimal(6, 2) check (balance >= 0 AND balance <= 5000) not null,
    foreign key (user_id) references users (id) on delete cascade
);

create type lang as enum ('ukrainian', 'original');
create table sessions
(
    id      serial primary key,
    film_id integer,
    date_time timestamp not null unique check (date_time::time between '9:00' and '23:59'),
    lang    lang not null,
    foreign key (film_id) references films (id) on delete cascade)
);


create table ticket_types
(
    id    serial primary key,
    name  varchar(20) not null unique,
    price integer     not null unique
);

create table tickets
(
    id             serial primary key,
    number         int2 check (number > 0 AND number <= 75),
    ticket_type_id integer,
    session_id     integer,
    user_id        integer,
    foreign key (session_id) references sessions (id) on delete cascade,
    foreign key (user_id) references users (id) on delete set null,
    foreign key (ticket_type_id) references ticket_types (id) on delete set null,
    unique (number, session_id)
);

*/