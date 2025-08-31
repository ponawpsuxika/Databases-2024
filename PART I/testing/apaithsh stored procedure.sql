DELETE FROM concert;
DELETE FROM venue;
-- insert into dba (Username , Start_Date) values ('admin' , '2020-01-01');
SET @current_dba := 'admin';
-- Προσθήκη venues
INSERT INTO venue (VenueID, VenueName, Capacity, IsAvailable, CompletedConcerts, YearsOperating) VALUES
(1, 'Small Hall',     500, 1,  20,  2),   -- Μικρός χώρος
(2, 'Medium Hall',  3000, 1, 150,  5),   -- Μεσαίος χώρος
(3, 'Big Arena'   , 3000, 1, 300, 10),   -- Μεγάλος χώρος
(4, 'Closed Hall',   2500, 0,  50,  3);   -- Μη διαθέσιμος

select * from venue;
-- Συναυλία Ακυρωμένη
INSERT INTO concert (ConcertID, ArtistID, VenueID, ConcertDate, ConcertStatus)
VALUES (101, 1, NULL, '2025-10-01',  'CANCELLED');

-- Συναυλία με ήδη κλεισμένο χώρο
INSERT INTO concert (ConcertID, ArtistID, VenueID, ConcertDate, ConcertStatus)
VALUES (102, 1, 2, '2025-10-05','SCHEDULED');

-- Συναυλία χωρίς χώρο (NULL στο VenueID)
INSERT INTO concert (ConcertID, ArtistID, VenueID, ConcertDate, ConcertStatus)
VALUES (103, 1, NULL, '2025-10-10', 'SCHEDULED');



CALL GetConcertVenueInfo(101, 1000, @venue_id, @venue_capacity);
SELECT @venue_id AS Venue_ID, @venue_capacity AS Venue_Capacity;

CALL GetConcertVenueInfo(102, 1000, @venue_id, @venue_capacity);
SELECT @venue_id AS Venue_ID, @venue_capacity AS Venue_Capacity;


CALL GetConcertVenueInfo(103, 1500, @venue_id, @venue_capacity);
SELECT @venue_id AS Venue_ID, @venue_capacity AS Venue_Capacity;
