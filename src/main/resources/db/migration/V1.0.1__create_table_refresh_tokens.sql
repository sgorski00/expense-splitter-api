CREATE TABLE refresh_tokens (
    id uuid PRIMARY KEY,
    token uuid NOT NULL UNIQUE,
    user_id uuid NOT NULL references users(id) ON DELETE CASCADE,
    is_revoked boolean NOT NULL default false,
    expires_at TIMESTAMP NOT NULL
);

CREATE INDEX ix_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX ix_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX ix_refresh_tokens_expires_revoked ON refresh_tokens(expires_at, is_revoked);
