ALTER TABLE categories
    ADD COLUMN parent_id BIGINT;

ALTER TABLE categories
    ADD CONSTRAINT fk_categories_parent
        FOREIGN KEY (parent_id)
            REFERENCES categories(id)
            ON DELETE CASCADE;
