CREATE TABLE password_reset_tokens (
    id uuid PRIMARY KEY,
    token uuid NOT NULL UNIQUE,
    user_id uuid NOT NULL references users(id) ON DELETE CASCADE,
    is_revoked boolean NOT NULL default false,
    expires_at TIMESTAMP with time zone NOT NULL,
    created_at TIMESTAMP with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX ix_password_reset_tokens_token ON password_reset_tokens(token);
CREATE INDEX ix_password_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX ix_password_reset_tokens_expires_revoked ON password_reset_tokens(expires_at, is_revoked);
