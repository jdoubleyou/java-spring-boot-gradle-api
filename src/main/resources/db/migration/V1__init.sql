CREATE SEQUENCE menus_id_seq START 1;
CREATE TABLE IF NOT EXISTS menus (
    id integer not null primary key
    , name text not null

    , created_date date not null
    , last_updated_date date
);

CREATE SEQUENCE customers_id_seq START 1;
CREATE TABLE IF NOT EXISTS customers (
    id integer not null primary key
    , name text not null

    , created_date date not null
    , last_updated_date date
);

CREATE SEQUENCE menu_items_id_seq START 1;
CREATE TABLE IF NOT EXISTS menu_items (
    id integer not null primary key
    , menu integer not null references menus(id)
    , price decimal not null check ( price > 0 )

    , created_date date not null
    , last_updated_date date
);

CREATE SEQUENCE orders_id_seq START 1;
CREATE TABLE IF NOT EXISTS orders (
    id integer not null primary key
    , status text not null
    , customer integer not null references customers(id)

    , created_date date not null
    , last_updated_date date
);

CREATE SEQUENCE order_items_id_seq START 1;
CREATE TABLE IF NOT EXISTS order_items (
    item_id integer not null references menu_items(id)
    , order_id integer not null references orders(id)

    , primary key (item_id, order_id)
);
