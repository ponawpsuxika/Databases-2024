SET @current_dba := 'admin'; -- an den uparxei kapoios DBA poy na einai logged in , den epitrepetai h allagh se pinakes 

INSERT INTO album (Title, ArtistID, GenreID , CompanyID , ProducerID ) VALUES 
('Whatever People Say I Am , That\'s What I\'m Not' , 1 , 1 , 25 , 38),
('Favourite Worst Nightmare' , 1 , 1, 25 , 48),
('Humbug' , 1,1 , 25 , 25),
('AM' , 1, 1, 25, 9),
('The Stone Roses', 2 , 11 , 6 , 9),
('Meat Is Murder' , 3 , 14, 4, 7),
('Kerplunk' , 6 , 12, 45 , 30),
('Ultraviolence' , 17 , 14, 7 , 15),
('Sumbarine' , 11 , 14 , 1 , 41),
('Suede' , 15 , 7, 12 , 7),
('Every Day Is Like Sunday' , 14, 18, 45 , 27),
('American Idiot' , 26 , 38 , 45 , 19),
('Kaiti Garbi - Best Of' , 29 , 3, 6 , 18),
('H atmosfaira hlektrismenh' , 7 , 8 , 17 , 38),
('Trifulla Tetrafulla' , 20 , 8, 19 , 25),
('Dominicana Latina' , 1 , 1 , 37 , 1),
('Oso Zw Mazw' , 7 , 12, 8 , 10),
('Mia Thalassa Galazia' , 12, 36 , 38 , 49);


select * from album;
