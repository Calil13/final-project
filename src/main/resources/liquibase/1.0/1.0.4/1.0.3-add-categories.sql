INSERT INTO categories (name, parent_id) VALUES
                                             ('Telefonlar, Smart cihazlar və telefon aksessuarları', NULL),
                                             ('Notbuk və kompüterlər', NULL),
                                             ('Geyim', NULL),
                                             ('Ev və Mətbəx', NULL),
                                             ('Uşaq məhsulları', NULL),
                                             ('Gözəllik və Sağlamlıq', NULL),
                                             ('Kitablar və Ofis', NULL);

INSERT INTO categories (name, parent_id) VALUES
                                             ('Notbuklar', (SELECT id FROM categories WHERE name='Notbuk və kompüterlər')),
                                             ('Oyun notbukları', (SELECT id FROM categories WHERE name='Notbuk və kompüterlər')),
                                             ('Planşetlər', (SELECT id FROM categories WHERE name='Notbuk və kompüterlər')),
                                             ('Masaüstü kompüterlər', (SELECT id FROM categories WHERE name='Notbuk və kompüterlər')),
                                             ('Oyun kompüterləri', (SELECT id FROM categories WHERE name='Notbuk və kompüterlər')),
                                             ('Klaviaturalar və kompüter siçanları', (SELECT id FROM categories WHERE name='Notbuk və kompüterlər'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Mobil telefonlar və smartfonlar', (SELECT id FROM categories WHERE name='Telefonlar, Smart cihazlar və telefon aksessuarları')),
                                             ('Ağıllı saatlar və fitnes qolbaqları', (SELECT id FROM categories WHERE name='Telefonlar, Smart cihazlar və telefon aksessuarları')),
                                             ('Qulaqlıqlar və qarniturlar', (SELECT id FROM categories WHERE name='Telefonlar, Smart cihazlar və telefon aksessuarları')),
                                             ('Telefonlar və smartfonlar üçün aksessuarlar', (SELECT id FROM categories WHERE name='Telefonlar, Smart cihazlar və telefon aksessuarları')),
                                             ('Smartfonlar və mobil telefonlar üçün çexollar', (SELECT id FROM categories WHERE name='Telefonlar, Smart cihazlar və telefon aksessuarları'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Kişi geyimləri', (SELECT id FROM categories WHERE name='Geyim')),
                                             ('Qadın geyimləri', (SELECT id FROM categories WHERE name='Geyim')),
                                             ('Uşaq geyimləri', (SELECT id FROM categories WHERE name='Geyim'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Aksesuarlar', (SELECT id FROM categories WHERE name='Kişi geyimləri')),
                                             ('Ayaqqabılar', (SELECT id FROM categories WHERE name='Kişi geyimləri')),
                                             ('Çantalar, pul kisələri və aksesuarlar', (SELECT id FROM categories WHERE name='Kişi geyimləri')),
                                             ('Gün eynəkləri', (SELECT id FROM categories WHERE name='Kişi geyimləri')),
                                             ('Geyimlər', (SELECT id FROM categories WHERE name='Kişi geyimləri'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Aksesuarlar', (SELECT id FROM categories WHERE name='Qadın geyimləri')),
                                             ('Ayaqqabılar', (SELECT id FROM categories WHERE name='Qadın geyimləri')),
                                             ('Çantalar, pul kisələri və aksesuarlar', (SELECT id FROM categories WHERE name='Qadın geyimləri')),
                                             ('Gün eynəkləri', (SELECT id FROM categories WHERE name='Qadın geyimləri')),
                                             ('Geyimlər', (SELECT id FROM categories WHERE name='Qadın geyimləri'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Aksesuarlar', (SELECT id FROM categories WHERE name='Uşaq geyimləri')),
                                             ('Ayaqqabılar', (SELECT id FROM categories WHERE name='Uşaq geyimləri')),
                                             ('Çantalar, pul kisələri və aksesuarlar', (SELECT id FROM categories WHERE name='Uşaq geyimləri')),
                                             ('Gün eynəkləri', (SELECT id FROM categories WHERE name='Uşaq geyimləri')),
                                             ('Geyimlər', (SELECT id FROM categories WHERE name='Uşaq geyimləri'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Mətbəx əşyaları', (SELECT id FROM categories WHERE name='Ev və Mətbəx')),
                                             ('Ev dekoru', (SELECT id FROM categories WHERE name='Ev və Mətbəx')),
                                             ('Mebel', (SELECT id FROM categories WHERE name='Ev və Mətbəx'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Uşaq oyuncaqları', (SELECT id FROM categories WHERE name='Uşaq məhsulları')),
                                             ('Uşaq ədəbiyyatı', (SELECT id FROM categories WHERE name='Uşaq məhsulları')),
                                             ('Hobbi və yaradıcılıq üçün məhsullar', (SELECT id FROM categories WHERE name='Uşaq məhsulları')),
                                             ('Uşaq qidası', (SELECT id FROM categories WHERE name='Uşaq məhsulları'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Kosmetika', (SELECT id FROM categories WHERE name='Gözəllik və Sağlamlıq')),
                                             ('Dəriyə qulluq', (SELECT id FROM categories WHERE name='Gözəllik və Sağlamlıq')),
                                             ('Vitamin və əlavələr', (SELECT id FROM categories WHERE name='Gözəllik və Sağlamlıq'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Gigiyena və qorunma', (SELECT id FROM categories WHERE name='Kosmetika')),
                                             ('Uşaq üçün qulluq və kosmetika', (SELECT id FROM categories WHERE name='Kosmetika')),
                                             ('Gözəllik və sağlamlıq üçün texnika', (SELECT id FROM categories WHERE name='Kosmetika')),
                                             ('Gözəllik salonu və SPA üçün avadanlıq', (SELECT id FROM categories WHERE name='Kosmetika')),
                                             ('Ətir və dezodorantlar', (SELECT id FROM categories WHERE name='Kosmetika'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Üz üçün kremlər', (SELECT id FROM categories WHERE name='Dəriyə qulluq')),
                                             ('Üz maskaları', (SELECT id FROM categories WHERE name='Dəriyə qulluq')),
                                             ('Əllər üçün kremlər', (SELECT id FROM categories WHERE name='Dəriyə qulluq'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Multivitaminlər', (SELECT id FROM categories WHERE name='Vitamin və əlavələr')),
                                             ('Dərman preparatları', (SELECT id FROM categories WHERE name='Vitamin və əlavələr')),
                                             ('Tibbi avadanlıq', (SELECT id FROM categories WHERE name='Vitamin və əlavələr')),
                                             ('Protein tozları', (SELECT id FROM categories WHERE name='Vitamin və əlavələr')),
                                             ('Optika', (SELECT id FROM categories WHERE name='Vitamin və əlavələr')),
                                             ('Bitki mənşəli əlavələr', (SELECT id FROM categories WHERE name='Vitamin və əlavələr'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Kitablar', (SELECT id FROM categories WHERE name='Kitablar və Ofis')),
                                             ('Ofis ləvazimatları', (SELECT id FROM categories WHERE name='Kitablar və Ofis'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Bədii ədəbiyyat', (SELECT id FROM categories WHERE name='Kitablar')),
                                             ('Dərs vəsaitləri', (SELECT id FROM categories WHERE name='Kitablar')),
                                             ('Uşaq kitabları', (SELECT id FROM categories WHERE name='Kitablar')),
                                             ('Ensiklopediyalar', (SELECT id FROM categories WHERE name='Kitablar')),
                                             ('Xarici dillər üzrə kitablar', (SELECT id FROM categories WHERE name='Kitablar'));

INSERT INTO categories (name, parent_id) VALUES
                                             ('Qələm və markerlər', (SELECT id FROM categories WHERE name='Ofis ləvazimatları')),
                                             ('Kağız və dəftərlər', (SELECT id FROM categories WHERE name='Ofis ləvazimatları')),
                                             ('Kançelyariya aksesuarları', (SELECT id FROM categories WHERE name='Ofis ləvazimatları')),
                                             ('Qovluq və fayllar', (SELECT id FROM categories WHERE name='Ofis ləvazimatları')),
                                             ('Ofis texnikası üçün aksesuarlar', (SELECT id FROM categories WHERE name='Ofis ləvazimatları'));
