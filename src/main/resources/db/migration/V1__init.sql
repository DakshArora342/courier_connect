CREATE TABLE delivery_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- auto-generate UUID
    client_url VARCHAR(255) NOT NULL,
    payload TEXT,
    status VARCHAR(20) NOT NULL,
    attempt_count INT NOT NULL DEFAULT 0,
    next_retry_at TIMESTAMP,
    last_error_message VARCHAR(500)
);