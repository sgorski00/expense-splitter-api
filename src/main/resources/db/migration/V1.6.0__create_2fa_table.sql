create table user_2fa (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    is_enabled boolean not null default false,
    secret varchar(255),
    created_at timestamp with time zone not null
)