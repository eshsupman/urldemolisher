CREATE SEQUENCE IF NOT EXISTS urls_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS shedlock (
                                        name VARCHAR(64) NOT NULL,
                                        lock_until TIMESTAMP NOT NULL,
                                        locked_at TIMESTAMP NOT NULL,
                                        locked_by VARCHAR(255) NOT NULL,
                                        PRIMARY KEY (name)
);