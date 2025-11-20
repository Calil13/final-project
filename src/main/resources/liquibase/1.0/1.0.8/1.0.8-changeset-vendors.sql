ALTER TABLE vendors
    DROP COLUMN card_number cascade;

ALTER TABLE vendors
    ADD COLUMN store_name VARCHAR(50) NOT NULL;