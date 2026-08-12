    CREATE TABLE complaint_cases (
                                     id                    BIGSERIAL     PRIMARY KEY,
                                     case_number           VARCHAR(255)  NOT NULL UNIQUE,
                                     case_type             VARCHAR(50)   NOT NULL,
                                     description           TEXT          NOT NULL,
                                     title                 VARCHAR(255)  NOT NULL,
                                     status                VARCHAR(50)   NOT NULL DEFAULT 'OPEN',
                                     customer_id           BIGINT        NOT NULL REFERENCES customer (id) ON DELETE CASCADE,
                                     satisfaction_score    INTEGER       CHECK (satisfaction_score BETWEEN 1 AND 5),
                                     feedback_comment      TEXT,
                                     feedback_submitted_at TIMESTAMPTZ,
                                     created_at            TIMESTAMPTZ   NOT NULL DEFAULT now(),
                                     updated_at            TIMESTAMPTZ   NOT NULL DEFAULT now()
    );

    CREATE TABLE case_tracking_numbers (
                                           case_id         BIGINT        NOT NULL REFERENCES complaint_cases (id) ON DELETE CASCADE,
                                           tracking_number VARCHAR(255)  NOT NULL,
                                           PRIMARY KEY (case_id, tracking_number)
    );

    CREATE INDEX idx_case_tracking_numbers_case_id ON case_tracking_numbers (case_id);