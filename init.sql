CREATE TABLE IF NOT EXISTS media (
    id              UUID PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    type            VARCHAR(20)  NOT NULL,
    status          VARCHAR(20)  NOT NULL,
    manifest_path   VARCHAR(500),
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    processed_at    TIMESTAMP,

    CONSTRAINT chk_media_type   CHECK (type IN ('AUDIO', 'VIDEO', 'IMAGE')),
    CONSTRAINT chk_media_status CHECK (status IN ('PENDING', 'AVAILABLE', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_media_status ON media (status);