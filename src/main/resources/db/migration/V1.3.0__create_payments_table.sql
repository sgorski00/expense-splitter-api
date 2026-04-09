CREATE TABLE payments (
    id uuid PRIMARY KEY,
    payer_id uuid NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    expense_id uuid NOT NULL REFERENCES expenses(id) ON DELETE RESTRICT,
    amount numeric(19,2) NOT NULL CHECK (amount >= 0),
    created_at TIMESTAMP with time zone NOT NULL,
    updated_at TIMESTAMP with time zone NOT NULL
);

CREATE INDEX idx_payments_expense_id ON payments (expense_id);
CREATE INDEX idx_payments_payer_id ON payments (payer_id);