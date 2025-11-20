ALTER TABLE users
    ADD CONSTRAINT users_user_role_check
        CHECK (user_role IN ('CUSTOMER', 'VENDOR', 'ADMIN'));