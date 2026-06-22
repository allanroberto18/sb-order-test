create table if not exists orders
(
    id           uuid primary key,
    status       varchar(32)              not null,
    total_amount numeric(19, 2)           not null,
    created_at   timestamp with time zone not null,
    updated_at   timestamp with time zone not null
);

create table if not exists order_items
(
    id         uuid primary key,
    order_id   uuid           not null references orders (id) on delete cascade,
    product_id uuid           not null references products (id) on delete cascade,
    quantity   integer        not null check (quantity > 0),
    unit_price numeric(19, 2) not null check (unit_price > 0)
);

create index if not exists idx_orders_status on orders (status);
create index if not exists idx_order_items_order_id on order_items (order_id);
create index if not exists idx_order_items_product_id on order_items (product_id);
