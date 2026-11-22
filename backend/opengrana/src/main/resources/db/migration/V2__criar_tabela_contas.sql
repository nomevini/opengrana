CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    public_id UUID NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,

    name VARCHAR(150) NOT NULL,
    type VARCHAR(30) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    opening_balance NUMERIC(19,4) NOT NULL,

    source VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    external_id UUID NULL,
    item_id UUID NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    closed_at TIMESTAMP NULL,
    balance_as_of TIMESTAMP NOT NULL DEFAULT now(),

    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT chk_accounts_type CHECK (
        type IN ('CHECKING', 'SAVINGS', 'CREDIT_CARD', 'INVESTMENT', 'CASH')
    ),
    CONSTRAINT chk_accounts_status CHECK (status IN ('ACTIVE', 'ARCHIVED', 'CLOSED')),
    CONSTRAINT chk_accounts_currency CHECK (char_length(currency) = 3),
    CONSTRAINT chk_accounts_source CHECK (source IN ('MANUAL', 'PLUGGY')),
    CONSTRAINT chk_accounts_origin CHECK (
        (source = 'MANUAL' AND external_id IS NULL AND item_id IS NULL)
        OR (source = 'PLUGGY' AND external_id IS NOT NULL AND item_id IS NOT NULL)
    )
);

CREATE INDEX idx_accounts_user_id_status ON accounts(user_id, status);
CREATE UNIQUE INDEX uq_accounts_external_id
    ON accounts(external_id) WHERE external_id IS NOT NULL;
CREATE INDEX idx_accounts_item_id
    ON accounts(item_id) WHERE item_id IS NOT NULL;

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_accounts_updated_at
    BEFORE UPDATE ON accounts
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();