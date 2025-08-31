SET @current_dba := 'admin';
select * from concert;

-- Insert
-- Case 1: Programmatismos neas sunaulias
CALL ConcertManagement(1, '2025-09-10', 'i'); -- ArtistID = 1 exei hdh 3 programmatismenes sunaulies -> error logw trigger
CALL ConcertManagement(12, '2025-09-30', 'i'); -- ArtistID = 12 den exei kamia -> success
CALL ConcertManagement(2,'2025-09-03' ,'i'); -- ArtistID = 2 den exei kamia -> success
-- Case 2: Programmatismos sunaulias thn hmera pou o artist exei ontws sunaulia 
CALL ConcertManagement(12, '2025-09-30', 'i'); -- ArtistID = 12  exei hdh -> Error 

-- Cancel
-- Case 3: Akurwsh sunaulias 
CALL ConcertManagement(1, '2025-09-03', 'c'); -- akurwsh sunaulias tou ArtistID = 1 me concertdate = '2025-09-05'
-- Case 4: Akurwsh hdh akurwmenhs sunaulias (prepei na leei "already cancelled")
CALL ConcertManagement(1, '2025-09-03', 'c');
CALL ConcertManagement(1, '2027-10-07', 'c');

-- Activate 
-- Case 5: Reschedule thn akurwmenh sunaulia 
CALL ConcertManagement(1, '2025-09-03', 'a'); -- prin eixame akurwsei thn sunaulia ekeinh thn hmera , kai twra prospathoume na thn energopoihsoume ksana
-- Case 6: Energopoihsh hdh energh sunaulia 
CALL ConcertManagement(1, '2025-09-03', 'a'); -- energopoioume to parapanw concert ksana

-- Akura Senaria
-- Case 7: Cancel sunaulia pou den uparxei
CALL ConcertManagement(101, '2025-09-20', 'c');
-- Case 8: Activate akurwmenh sunaulia pou den uparxei ousiastika
CALL ConcertManagement(101, '2025-09-20', 'a');
-- Case 9: Lathos epilogh
CALL ConcertManagement(101, '2025-09-10', 'x');


