set time zone 'GMT-2';

create table genres
(
    id   serial primary key,
    name varchar(30) unique not null
);


create table films
(
    id                serial primary key,
    title             character varying(100)                         not null,
    len               integer check (len > 0 AND len <= 240),
    year_prod         varchar(4)                                     not null,
    category          integer check (category = 0 or
                                     category = 12 or
                                     category = 16 or category = 18) not null,
    descr             varchar(3000)                                  not null,
    rating            numeric(3, 1) check (rating > 0 and rating <= 10),
    img               varchar(300)                                   not null unique,
    last_showing_date date                                           not null,
    unique (title, year_prod, descr)
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
    email    varchar(30) unique                            not null,
    password varchar(32)                                   not null check (length(password) >= 8),
    login    varchar(16) unique check (length(login) >= 3) not null,
    role     role_type                                     not null
);


create table wallets
(
    id      serial primary key,
    user_id integer unique                                         not null,
    balance decimal(6, 2) check (balance >= 0 AND balance <= 5000) not null,
    foreign key (user_id) references users (id) on delete cascade
);

create type lang as enum ('ukrainian', 'original');
create table sessions
(
    id        serial primary key,
    film_id   integer,
    date_time timestamp not null unique check (date_time::time between '9:00' and '23:59'),
    lang      lang      not null,
    foreign key (film_id) references films (id) on delete cascade
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
    number         smallint check (number > 0 AND number <= 75) not null,
    ticket_type_id integer                                  not null,
    session_id     integer                                  not null,
    user_id        integer,
    foreign key (session_id) references sessions (id) on delete cascade,
    foreign key (user_id) references users (id) on delete set null,
    foreign key (ticket_type_id) references ticket_types (id) on delete set null,
    unique (number, session_id)
);



insert into genres (name)
values ('fantasy'),
       ('adventure'),
       ('action'),
       ('action movie'),
       ('drama'),
       ('comedy'),
       ('cartoon'),
       ('thriller');

insert into films (title, len, year_prod, category, descr, rating, img, last_showing_date)
values ('The French Dispatch', 168, '2021', 16,
        'Plot: in the center of the plot is a branch of the american newspaper "french herald", located in a fictional french town of the 20th century. ' ||
        'Cast: Bnicio del Toro ("avengers: infinity war"),Tilda Swinton ("doctor strange"), Francis Mcdormand ("land of the nomads"), Timothy Shalame ("dune"), Aries Wilson (trilogy "night at the museum") , Adrian Brody, Lea Seydou and others. ' ||
        'Countries: Germany, USA. Director: Weight Anderson',
        7.5, 'static/image/french_dispatch.png', date '24-01-2022'),
       ('The Eternals', 158, '2021', 12,
        'Plot: superhero action "eternal" from marvel studios presents a new incredible team of superheroes of the marvel movie universe. an epic story that unfolds over several millennia, and immortal heroes who are forced to come out of the shadows to protect humanity from its oldest enemies, the deviants. ' ||
        'Cast: Angelina Jolie ("the witch: mistress of darkness"), Richard Madden ("1917"), Salma Hayek ("the killer''s wife''s bodyguard"), Kumail Nanjiani ("doolittle", "teacher''s wave"), Gemma Chan ("captain marvel") , Keith Herrington (game of thrones series) and others. ' ||
        'Countries: USA. Director: Chloe Zhao',
        6.9, 'static/image/eternals.png', date '28-01-2022');
insert into films (title, len, year_prod, category, descr, img, last_showing_date)
values ('Ghostbusters: afterlife', 120, '2021', 18,
        'Plot: a new part of fantastic adventures from the original universe of ghostbusters! Having inherited an old abandoned house, the family moves to a small town. their appearance coincides in time with the extraordinary activity of paranormal forces, which turn the usual life upside down. the time has come! the new generation of ghost hunters needs to find out the nature of the mysterious legacy and eliminate the consequences on an unprecedented scale! ' ||
        'Cast: Paul Rudd, Bill Murray, Sigourney Weaver, Finn Woolford. ' ||
        'Countries: USA, Canada. Director: Jason Wsrightman', 'static/image/ghostbusters_afterlife.png',
        date '15-01-2022');

insert into genres_films
values (1, 6),
       (1, 7),
       (2, 3),
       (2, 4),
       (2, 6),
       (2, 2),
       (3, 1),
       (3, 2),
       (3, 3);

insert into users(email, password, login, role)
VALUES ('user@gmail.com', 'Password1&', 'user1', 'user'),
       ('user2@gmail.com', 'Password1&', 'user2', 'user'),
       ('user3@gmail.com', 'Password1&', 'user3', 'user'),
       ('user5@gmail.com', 'Password1&', 'user5', 'user'),
       ('user6@gmail.com', 'Password1&', 'user6', 'user'),
       ('user7@gmail.com', 'Password1&', 'user7', 'user'),
       ('user8@gmail.com', 'Password1&', 'user8', 'user'),
       ('user9@gmail.com', 'Password1&', 'user9', 'user'),
       ('admin@gmail.com', 'Admin11&', 'admin1', 'admin'),
       ('admin2@gmail.com', 'Admin12&', 'admin2', 'admin');



insert into wallets(user_id, balance)
VALUES (1, 300),
       (2, 100),
       (3, 200),
       (4, 50),
       (5, 1000),
       (6, 120),
       (7, 345),
       (8, 123.50);

insert into sessions(film_id, date_time, lang)
values (1, timestamp '2021-12-22 9:00', 'ukrainian'),
       (2, timestamp '2021-12-22 11:05', 'ukrainian'),
       (1, timestamp '2021-12-12 14:00', 'ukrainian'),
       (2, timestamp '2021-12-22 16:05', 'ukrainian'),
       (1, timestamp '2021-12-22 19:00', 'ukrainian'),
       (2, timestamp '2021-12-22 21:05', 'ukrainian'),

       (1, timestamp '2021-11-23 9:00', 'ukrainian'),
       (1, timestamp '2021-11-23 11:05', 'ukrainian'),
       (2, timestamp '2021-11-23 13:10', 'ukrainian'),
       (1, timestamp '2021-11-23 16:05', 'ukrainian'),
       (2, timestamp '2021-11-23 18:10', 'ukrainian'),
       (1, timestamp '2021-11-23 21:05', 'ukrainian'),
       (3, timestamp '2021-11-23 23:10', 'ukrainian'),

       (1, timestamp '2021-11-24 9:00', 'ukrainian'),
       (2, timestamp '2021-11-24 11:05', 'ukrainian'),
       (2, timestamp '2021-11-24 14:00', 'ukrainian'),
       (2, timestamp '2021-11-24 16:05', 'ukrainian'),
       (1, timestamp '2021-11-24 19:00', 'ukrainian'),
       (1, timestamp '2021-11-24 21:05', 'ukrainian'),
       (3, timestamp '2021-11-24 23:10', 'ukrainian'),

       (1, timestamp '2021-11-25 9:00', 'ukrainian'),
       (3, timestamp '2021-11-25 11:00', 'ukrainian'),
       (1, timestamp '2021-11-25 13:10', 'ukrainian'),
       (2, timestamp '2021-11-25 15:10', 'ukrainian'),
       (2, timestamp '2021-11-25 18:00', 'ukrainian'),
       (1, timestamp '2021-11-25 22:00', 'ukrainian'),

       (1, timestamp '2021-11-26 9:00', 'ukrainian'),
       (1, timestamp '2021-11-26 11:05', 'ukrainian'),
       (1, timestamp '2021-11-26 13:10', 'ukrainian'),
       (2, timestamp '2021-11-26 15:15', 'ukrainian'),
       (1, timestamp '2021-11-26 18:10', 'ukrainian'),
       (2, timestamp '2021-11-26 20:15', 'ukrainian'),
       (1, timestamp '2021-11-26 23:10', 'ukrainian'),

       (1, timestamp '2021-11-27 9:00', 'ukrainian'),
       (1, timestamp '2021-11-27 11:05', 'ukrainian'),
       (2, timestamp '2021-11-27 13:10', 'ukrainian'),
       (1, timestamp '2021-11-27 16:05', 'ukrainian'),
       (2, timestamp '2021-11-27 18:10', 'ukrainian'),
       (1, timestamp '2021-11-27 21:05', 'ukrainian'),
       (3, timestamp '2021-11-27 23:10', 'ukrainian'),

       (1, timestamp '2021-11-28 9:00', 'ukrainian'),
       (2, timestamp '2021-11-28 11:05', 'ukrainian'),
       (1, timestamp '2021-11-28 14:00', 'ukrainian'),
       (2, timestamp '2021-11-28 16:05', 'ukrainian'),
       (1, timestamp '2021-11-28 19:00', 'ukrainian'),
       (2, timestamp '2021-11-28 21:05', 'ukrainian');


insert into ticket_types(name, price)
VALUES ('simple', 120),
       ('vip', 200);

insert into tickets(number, ticket_type_id, session_id)
VALUES (1, 1, 1),
       (2, 1, 1),
       (3, 1, 1),
       (62, 2, 1),
       (61, 2, 1),
       (1, 1, 3),
       (2, 1,3),
       (3, 1, 3),
       (62, 2, 3);

insert into tickets(number, ticket_type_id, session_id, user_id)
VALUES (4, 1, 1, 3);