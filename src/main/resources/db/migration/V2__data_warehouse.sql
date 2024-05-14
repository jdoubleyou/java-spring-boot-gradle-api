CREATE SEQUENCE accounts_id_seq START 1;
CREATE TABLE IF NOT EXISTS accounts (
    id integer not null primary key
    , name text not null
);

CREATE TABLE IF NOT EXISTS account_hierarchy (
    parent integer not null references accounts(id)
    , child integer not null references accounts(id) check ( child != parent )
    , primary key (parent, child)
);
