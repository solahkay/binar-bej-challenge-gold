create database binar_fud;

create table users
(
    id         varchar(100)        not null primary key,
    username   varchar(40) unique  not null,
    name       varchar(150),
    email      varchar(100) unique not null,
    password   varchar(255)        not null,
    created_at timestamp,
    updated_at timestamp
);

create table merchants
(
    id       varchar(100)        not null primary key,
    name     varchar(150) unique not null,
    location varchar(100)        not null,
    status   varchar(50)         not null default 'CLOSED'
);

create table products
(
    id          varchar(100)        not null primary key,
    sku         varchar(100) unique not null,
    name        varchar(255)        not null,
    price       bigint              not null,
    quantity    bigint              not null default 0,
    status      varchar(50)         not null default 'AVAILABLE',
    merchant_id varchar(100)        not null,
    foreign key (merchant_id) references merchants (id)
);

create table orders
(
    id               varchar(100)        not null primary key,
    code             varchar(100) unique not null,
    shipping_address varchar(500)        not null,
    created_at       timestamp,
    status           varchar(50)         not null,
    user_id          varchar(100)        not null,
    foreign key (user_id) references users (id)
);

create table order_details
(
    product_id  varchar(100) not null,
    order_id    varchar(100) not null,
    quantity    bigint       not null,
    total_price bigint       not null,
    primary key (product_id, order_id),
    foreign key (product_id) references products (id),
    foreign key (order_id) references orders (id)
);
