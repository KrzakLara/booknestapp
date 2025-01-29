-- Remove END_AT column from the book table
ALTER TABLE book DROP COLUMN end_at;

ALTER TABLE book
    ADD version BIGINT;

-- Set version to 0 where it is null
UPDATE book
SET version = 0
WHERE version IS NULL;

ALTER TABLE book
    ALTER COLUMN version SET NOT NULL;

DROP SEQUENCE CHOICE_SEQ;

DROP SEQUENCE RESPONSE_SEQ;

ALTER TABLE book
    ALTER COLUMN allow_multi_choice SET NOT NULL;

ALTER TABLE book
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE book
    ALTER COLUMN creator_id SET NOT NULL;

ALTER TABLE book
    ALTER COLUMN is_active SET NOT NULL;

ALTER TABLE book
    ALTER COLUMN question SET NOT NULL;

-- Add for_sale column
ALTER TABLE book
    ADD COLUMN for_sale BOOLEAN DEFAULT FALSE;

ALTER TABLE book
    ADD COLUMN for_sale BOOLEAN DEFAULT FALSE;



SELECT column_name
FROM information_schema.columns
WHERE table_name = 'book';


ALTER TABLE book
    ADD COLUMN for_sale BOOLEAN DEFAULT FALSE;
ALTER TABLE book
    ALTER COLUMN for_sale SET NOT NULL;


ALTER TABLE book ADD COLUMN for_sale BOOLEAN DEFAULT FALSE NOT NULL;

ALTER TABLE book ADD COLUMN price DOUBLE DEFAULT 0.0 NOT NULL;

-- Add `for_sale` column if not exists
ALTER TABLE book ADD COLUMN for_sale BOOLEAN DEFAULT FALSE NOT NULL;

-- Add `price` column if not exists
ALTER TABLE book ADD COLUMN price DOUBLE DEFAULT 0.0 NOT NULL;

INSERT INTO user_details (first_name, last_name, username, email, password, role) VALUES (
                                                                                             'Admin', -- First Name
                                                                                             'User', -- Last Name
                                                                                             'auser', -- Username (can be generated using your logic or set manually)
                                                                                             'admin@example.com', -- Email
                                                                                             '$2a$10$D9k5Oa5HlTvnzYjXFi6G6eVqUeAfDme6fuPsp7uXsXmkOdIiQpyWu', -- BCrypt hash for 'admin123'
                                                                                             'ADMIN' -- Role
                                                                                         );
