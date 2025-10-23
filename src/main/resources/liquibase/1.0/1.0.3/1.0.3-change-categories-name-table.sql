ALTER TABLE categories
DROP CONSTRAINT IF EXISTS categories_name_key;

ALTER TABLE categories
    ALTER COLUMN name DROP NOT NULL;
