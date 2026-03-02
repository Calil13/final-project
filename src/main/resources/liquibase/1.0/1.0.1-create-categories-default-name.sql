-- 1. əsas kateqoriyalar
INSERT INTO categories (name, parent_id) VALUES
                                             ('Elektronika', NULL),
                                             ('Nəqliyyat', NULL),
                                             ('Tikinti və Alətlər', NULL),
                                             ('Tədbir və Dekor', NULL),
                                             ('İdman və Aktiv İstirahət', NULL),
                                             ('Foto və Video', NULL);

-- 2. Elektronika alt kateqoriyaları
INSERT INTO categories (name, parent_id) VALUES
                                             ('Telefon', (SELECT id FROM categories WHERE name = 'Elektronika')),
                                             ('Laptop', (SELECT id FROM categories WHERE name = 'Elektronika')),
                                             ('Planşet', (SELECT id FROM categories WHERE name = 'Elektronika')),
                                             ('Oyun konsolu', (SELECT id FROM categories WHERE name = 'Elektronika')),
                                             ('Proyektor', (SELECT id FROM categories WHERE name = 'Elektronika'));

-- 3. Nəqliyyat alt kateqoriyaları
INSERT INTO categories (name, parent_id) VALUES
                                             ('Motosiklet', (SELECT id FROM categories WHERE name = 'Nəqliyyat')),
                                             ('Velosiped', (SELECT id FROM categories WHERE name = 'Nəqliyyat')),
                                             ('Skuter', (SELECT id FROM categories WHERE name = 'Nəqliyyat'));

-- 4. Tikinti və Alətlər alt kateqoriyaları
INSERT INTO categories (name, parent_id) VALUES
                                             ('Matkap', (SELECT id FROM categories WHERE name = 'Tikinti və Alətlər')),
                                             ('Generator', (SELECT id FROM categories WHERE name = 'Tikinti və Alətlər')),
                                             ('Qaynaq aparatı', (SELECT id FROM categories WHERE name = 'Tikinti və Alətlər')),
                                             ('Kompressor', (SELECT id FROM categories WHERE name = 'Tikinti və Alətlər'));

-- 5. Tədbir və Dekor alt kateqoriyaları
INSERT INTO categories (name, parent_id) VALUES
                                             ('Səs sistemi', (SELECT id FROM categories WHERE name = 'Tədbir və Dekor')),
                                             ('Çadır', (SELECT id FROM categories WHERE name = 'Tədbir və Dekor')),
                                             ('Masa və stul', (SELECT id FROM categories WHERE name = 'Tədbir və Dekor')),
                                             ('Toy dekoru', (SELECT id FROM categories WHERE name = 'Tədbir və Dekor'));

-- 6. İdman və Aktiv İstirahət alt kateqoriyaları
INSERT INTO categories (name, parent_id) VALUES
                                             ('Trenajor avadanlığı', (SELECT id FROM categories WHERE name = 'İdman və Aktiv İstirahət')),
                                             ('Qaçış trenajoru', (SELECT id FROM categories WHERE name = 'İdman və Aktiv İstirahət')),
                                             ('Velotrenajor', (SELECT id FROM categories WHERE name = 'İdman və Aktiv İstirahət')),
                                             ('Çadır və kamp ləvazimatları', (SELECT id FROM categories WHERE name = 'İdman və Aktiv İstirahət')),
                                             ('Dağ velosipedi', (SELECT id FROM categories WHERE name = 'İdman və Aktiv İstirahət')),
                                             ('Kayak avadanlığı', (SELECT id FROM categories WHERE name = 'İdman və Aktiv İstirahət')),
                                             ('Snoubord', (SELECT id FROM categories WHERE name = 'İdman və Aktiv İstirahət')),
                                             ('Balıqçılıq avadanlığı', (SELECT id FROM categories WHERE name = 'İdman və Aktiv İstirahət')),
                                             ('Dalış avadanlığı', (SELECT id FROM categories WHERE name = 'İdman və Aktiv İstirahət')),
                                             ('Fitness aksesuarları', (SELECT id FROM categories WHERE name = 'İdman və Aktiv İstirahət'));

-- 7. Foto və Video alt kateqoriyaları
INSERT INTO categories (name, parent_id) VALUES
                                             ('Kamera', (SELECT id FROM categories WHERE name = 'Foto və Video')),
                                             ('Drone', (SELECT id FROM categories WHERE name = 'Foto və Video')),
                                             ('Obyektiv', (SELECT id FROM categories WHERE name = 'Foto və Video')),
                                             ('Stabilizator', (SELECT id FROM categories WHERE name = 'Foto və Video'));