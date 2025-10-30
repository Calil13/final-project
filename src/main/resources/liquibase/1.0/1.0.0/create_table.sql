create table users
(
    id         bigserial
        primary key,
    email      varchar(100) not null
        unique,
    password   varchar(255) not null,
    created_at timestamp default CURRENT_TIMESTAMP,
    user_role  varchar(20)  not null
        constraint users_user_role_check
            check ((user_role)::text = ANY
        ((ARRAY ['CUSTOMER'::character varying, 'VENDOR'::character varying])::text[]))
    );

alter table users
    owner to postgres;

create table vendors
(
    id          bigserial
        primary key,
    user_id     bigint      not null
        references users,
    vendor_code varchar(50) not null
        unique,
    card_number varchar(50) not null,
    is_active   boolean default true
);

alter table vendors
    owner to postgres;

create table customers
(
    id          bigserial
        primary key,
    user_id     bigint not null
        references users,
    full_name   varchar(255),
    phone       varchar(255),
    card_number varchar(255)
);

alter table customers
    owner to postgres;

create table categories
(
    id   bigserial
        primary key,
    name varchar(100) not null
        unique
);

alter table categories
    owner to postgres;

create table products
(
    id          bigserial
        primary key,
    vendor_id   bigint           not null
        references vendors,
    category_id bigint           not null
        references categories,
    name        varchar(150)     not null,
    description varchar(255),
    price       double precision not null,
    stock       integer          not null,
    created_at  timestamp default CURRENT_TIMESTAMP
);

alter table products
    owner to postgres;

create table product_images
(
    id         bigserial
        primary key,
    product_id bigint       not null
        references products,
    image_url  varchar(255) not null
);

alter table product_images
    owner to postgres;

create table shopping_carts
(
    id          bigserial
        primary key,
    customer_id bigint not null
        constraint shopping_carts_customer_id_unique
            unique
        references customers,
    created_at  timestamp default CURRENT_TIMESTAMP
);

alter table shopping_carts
    owner to postgres;

create table cart_items
(
    id         bigserial
        primary key,
    cart_id    bigint  not null
        references shopping_carts,
    product_id bigint  not null
        references products,
    quantity   integer not null
);

alter table cart_items
    owner to postgres;

create table orders
(
    id          bigserial
        primary key,
    customer_id bigint       not null
        references customers,
    order_date  timestamp   default CURRENT_TIMESTAMP,
    status      varchar(20) default 'PENDING'::character varying
        constraint orders_status_check
            check ((status)::text = ANY
                   ((ARRAY ['PENDING'::character varying, 'PAID'::character varying, 'SHIPPED'::character varying, 'DELIVERED'::character varying, 'CANCELLED'::character varying])::text[])),
    location    varchar(255) not null
);

alter table orders
    owner to postgres;

create table order_items
(
    id         bigserial
        primary key,
    order_id   bigint           not null
        references orders,
    product_id bigint           not null
        references products,
    quantity   integer          not null,
    price      double precision not null
);

alter table order_items
    owner to postgres;

create table payments
(
    id             bigserial
        primary key,
    order_id       bigint
        references orders,
    vendor_id      bigint
        references vendors,
    amount         numeric(10, 2) not null,
    payment_date   timestamp   default CURRENT_TIMESTAMP,
    payment_method varchar(20) default 'CARD'::character varying
        constraint payments_payment_method_check
            check ((payment_method)::text = ANY
                   ((ARRAY ['CARD'::character varying, 'CASH'::character varying])::text[])),
    status         varchar(20) default 'PENDING'::character varying
        constraint payments_status_check
            check ((status)::text = ANY
                   ((ARRAY ['SUCCESS'::character varying, 'FAILED'::character varying, 'PENDING'::character varying])::text[])),
    transaction_id varchar(100)
);

alter table payments
    owner to postgres;

create table product_reviews
(
    id          bigserial
        primary key,
    product_id  bigint not null
        references products,
    customer_id bigint not null
        references customers,
    rating      integer
        constraint product_reviews_rating_check
            check ((rating >= 1) AND (rating <= 5)),
    comment     text,
    created_at  timestamp default CURRENT_TIMESTAMP
);

alter table product_reviews
    owner to postgres;

create table favorites
(
    id          bigserial
        primary key,
    customer_id bigint not null
        references customers,
    product_id  bigint not null
        references products,
    created_at  timestamp default CURRENT_TIMESTAMP,
    unique (customer_id, product_id)
);

alter table favorites
    owner to postgres;

