-- Wstawianie przykładowych pokoi
INSERT INTO rooms (title, description, price_per_night, max_guests, nr_of_rooms, location) VALUES
('Apartament z widokiem na morze', 'Piękny apartament z bezpośrednim widokiem na Morze Bałtyckie', 350.00, 4, 2, 'Gdańsk'),
('Przytulne studio w centrum', 'Nowoczesne studio w samym centrum miasta, blisko atrakcji', 220.00, 2, 1, 'Warszawa'),
('Domek w górach', 'Drewniany domek z kominkiem i widokiem na góry', 280.00, 6, 2, 'Zakopane');

-- Wstawianie przykładowych rezerwacji
INSERT INTO reservations (room_id, guest_name, guest_email, check_in_date, check_out_date, status, total_price) VALUES 
(1, 'Jan Kowalski', 'jan.kowalski@example.com', '2025-06-10', '2025-06-15', 'CONFIRMED', 1750.00),
(2, 'Anna Nowak', 'anna.nowak@example.com', '2025-07-05', '2025-07-10', 'CONFIRMED', 1100.00),
(3, 'Piotr Wiśniewski', 'piotr.wisniewski@example.com', '2025-08-01', '2025-08-07', 'PENDING', 1680.00),
(1, 'Magdalena Dąbrowska', 'magdalena.dabrowska@example.com', '2025-09-15', '2025-09-20', 'CONFIRMED', 1750.00),
(2, 'Tomasz Lewandowski', 'tomasz.lewandowski@example.com', '2025-05-01', '2025-05-05', 'CANCELLED_BY_GUEST', 880.00);