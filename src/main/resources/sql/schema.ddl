CREATE TABLE messages (
    id VARCHAR(100) UNIQUE NOT NULL,
    text VARCHAR(500),
    CONSTRAINT id_messages PRIMARY KEY (id)
);