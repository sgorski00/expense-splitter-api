CREATE TABLE users (
    id uuid PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    role varchar(50) NOT NULL check (role in ('USER', 'ADMIN')),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    is_password_for_change boolean not null DEFAULT false,
    UNIQUE(email, deleted_at)
);

CREATE TABLE user_identities (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL references users(id),
    provider varchar(255) NOT NULL check (provider in ('GOOGLE', 'FACEBOOK')),
    provider_id text NOT NULL,
    UNIQUE(provider, provider_id),
    UNIQUE(user_id, provider)
);

CREATE INDEX ix_users_email_active ON users(email) WHERE deleted_at IS NULL;
CREATE INDEX ix_user_identities_user_id ON user_identities(user_id);