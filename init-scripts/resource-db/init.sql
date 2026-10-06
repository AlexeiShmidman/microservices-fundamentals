CREATE TABLE IF NOT EXISTS resources (
    id               BIGSERIAL    PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    storage_location VARCHAR(512) NOT NULL,
    content_type     VARCHAR(255) NOT NULL,
    size             BIGINT
);
