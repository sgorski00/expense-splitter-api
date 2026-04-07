CREATE TABLE expenses (
    id uuid PRIMARY KEY,
    title varchar(255) NOT NULL,
    description text,
    payer_id uuid NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    amount_total numeric(19,2) NOT NULL CHECK (amount_total >= 0),
    split_type varchar(50) NOT NULL DEFAULT 'EQUAL' CHECK (split_type IN ('EQUAL')),
    expense_date TIMESTAMP with time zone NOT NULL,
    created_at TIMESTAMP with time zone NOT NULL,
    updated_at TIMESTAMP with time zone NOT NULL
);

CREATE INDEX idx_expenses_expense_date ON expenses(expense_date);
CREATE INDEX idx_expenses_created_at ON expenses(created_at);
CREATE INDEX idx_expenses_split_type ON expenses(split_type);
CREATE INDEX idx_expenses_payer_id ON expenses(payer_id);

CREATE TABLE expense_shares (
    id uuid PRIMARY KEY,
    expense_id uuid NOT NULL REFERENCES expenses(id) ON DELETE CASCADE,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount numeric(19,2) NOT NULL CHECK (amount >= 0)
);

CREATE UNIQUE INDEX unique_expense_user ON expense_shares(expense_id, user_id);
CREATE INDEX idx_expense_shares_expense_id ON expense_shares(expense_id);
CREATE INDEX idx_expense_shares_user_id ON expense_shares(user_id);