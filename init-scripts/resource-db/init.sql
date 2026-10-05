CREATE TABLE IF NOT EXISTS resources (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    data        BYTEA        NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    size        BIGINT
);
