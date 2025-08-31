SET @current_dba := 'admin';
-- eisagwgh venues
INSERT INTO venue (VenueName, Capacity , CompletedConcerts, YearsOperating , IsAvailable) VALUES ('Old Trafford' , 150000 , 150, 100 , 1);
INSERT INTO venue (VenueName, Capacity , CompletedConcerts, YearsOperating , IsAvailable) VALUES ('MSG' , 200000 , 500, 75 , 1);
INSERT INTO venue (VenueName, Capacity , CompletedConcerts, YearsOperating , IsAvailable) VALUES ('Manchester Apollo' , 3500 , 600 , 120,  1);

-- eisagwgh scheduled concerts gia thn apaithsh 3.1.4.2
INSERT INTO concert (ArtistID, VenueID, ConcertDate , ConcertCapacity, ConcertStatus) VALUES (1,1,'2025-09-03' , 2500 , 'SCHEDULED');
INSERT INTO concert (ArtistID, VenueID, ConcertDate , ConcertCapacity, ConcertStatus) VALUES (1,2,'2025-09-30' , 15000 , 'SCHEDULED');
INSERT INTO concert (ArtistID, VenueID, ConcertDate , ConcertCapacity, ConcertStatus) VALUES (1,3,'2025-10-07' , 2500 , 'SCHEDULED');
INSERT INTO concert (ArtistID, VenueID, ConcertDate , ConcertCapacity, ConcertStatus) VALUES (1,3,'2025-10-17' , 800 , 'SCHEDULED');


-- eisagwgh concerts gia thn apaithsh 3.1.4.3 (meta apo truncate pinaka)
INSERT INTO concert (ArtistID, VenueID, ConcertDate , ConcertCapacity, ConcertStatus) VALUES (1,3,'2027-10-07' , 1500 , 'CANCELLED');
INSERT INTO concert (ArtistID, VenueID, ConcertDate , ConcertCapacity, ConcertStatus) VALUES (1,1,'2025-09-03' , 2500 , 'SCHEDULED');
INSERT INTO concert (ArtistID, VenueID, ConcertDate , ConcertCapacity, ConcertStatus) VALUES (1,2,'2025-09-30' , 15000 , 'SCHEDULED');
INSERT INTO concert (ArtistID, VenueID, ConcertDate , ConcertCapacity, ConcertStatus) VALUES (1,3,'2025-10-17' , 2500 , 'SCHEDULED');

UPDATE concert 
SET 
    ConcertStatus = 'SCHEDULED'
WHERE
    ConcertID = 1;	

