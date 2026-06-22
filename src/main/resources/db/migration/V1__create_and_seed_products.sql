create table if not exists products
(
    id    uuid primary key,
    name  varchar(255)   not null,
    price numeric(19, 2) not null check (price > 0)
);

insert into products (id, name, price)
values ('11111111-1111-1111-1111-111111111111', 'Notebook Pro 14', 7499.90),
       ('22222222-2222-2222-2222-222222222222', 'Wireless Mouse', 199.90),
       ('33333333-3333-3333-3333-333333333333', 'USB-C Dock', 499.90);
