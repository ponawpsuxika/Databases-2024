SET @current_dba := 'admin'; -- an den uparxei kapoios DBA poy na einai logged in , den epitrepetai h allagh se pinakes 

INSERT INTO albumrelease (AlbumID, ReleaseDate, ReleaseType, ReleaseStatus, Packaging) VALUES
(1, '2022-01-15', 'LP', 'OFFICIAL', 'JEWEL CASE'),
(2, '2021-06-20', 'CD', 'PROMOTION', 'DIGIPAK'),
(3, '2020-09-05', 'MP3', 'OFFICIAL', 'NOT APPLICABLE'),
(4, '2022-11-12', 'CD', 'BOOTLEG', 'CARDBOARD SLEEVE'),
(5, '2021-03-25', 'LP', 'WITHDRAWN', 'BOOK'),
(6, '2020-12-01', 'MP3', 'CANCELLED', 'NOT APPLICABLE'),
(7, '2022-07-30', 'CD', 'OFFICIAL', 'DIGIPAK'),
(8, '2021-10-18', 'LP', 'PROMOTION', 'JEWEL CASE'),
(9, '2020-05-22', 'MP3', 'OFFICIAL', 'NOT APPLICABLE'),
(10, '2022-02-14', 'CD', 'WITHDRAWN', 'CARDBOARD SLEEVE'),
(1, '1985-04-23', 'LP', 'OFFICIAL', 'BOOK'),
(2, '1992-09-15', 'CD', 'OFFICIAL', 'JEWEL CASE'),
(3, '2000-06-01', 'MP3', 'PROMOTION', 'NOT APPLICABLE'),
(4, '1988-11-12', 'LP', 'OFFICIAL', 'CARDBOARD SLEEVE'),
(5, '1999-03-05', 'CD', 'BOOTLEG', 'DIGIPAK'),
(6, '2005-07-20', 'MP3', 'OFFICIAL', 'NOT APPLICABLE');


select * from albumrelease;

