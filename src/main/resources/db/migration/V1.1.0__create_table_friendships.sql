CREATE TABLE friendships (
    id uuid PRIMARY KEY,
    requester_id uuid NOT NULL references users(id) ON DELETE CASCADE,
    recipient_id uuid NOT NULL references users(id) ON DELETE CASCADE,
    status varchar(255) NOT NULL default 'PENDING' check (status in ('PENDING', 'ACCEPTED', 'REJECTED')),
    created_at TIMESTAMP with time zone NOT NULL,
    updated_at TIMESTAMP with time zone NOT NULL
);

CREATE UNIQUE INDEX unique_friend_invite ON friendships (
    LEAST(requester_id, recipient_id),
    GREATEST(requester_id, recipient_id)
) WHERE status != 'REJECTED';

CREATE INDEX idx_friendships_requester_id ON friendships(requester_id);
CREATE INDEX idx_friendships_recipient_id ON friendships(recipient_id);
CREATE INDEX idx_friendships_status ON friendships(status);
CREATE INDEX idx_friendships_requester_status ON friendships(requester_id, status);
CREATE INDEX idx_friendships_recipient_status ON friendships(recipient_id, status);

