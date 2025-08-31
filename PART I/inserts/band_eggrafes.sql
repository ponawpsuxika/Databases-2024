SET @current_dba := 'admin';


INSERT INTO band (BandName, FormationDate, DisbandDate, ArtistID) VALUES ('Arctic Monkeys', '2002-01-01', NULL , 1);
INSERT INTO band (BandName, FormationDate, DisbandDate, ArtistID) VALUES ('The Stone Roses' ,'1983-01-01' ,'2019-09-16',2);
INSERT INTO band (BandName, FormationDate, DisbandDate, ArtistID)VALUES  ('The Smiths' ,'1982-01-01' , '1987-01-01', 3);
INSERT INTO band (BandName, FormationDate, DisbandDate, ArtistID)VALUES  ('Radiohead', '1985-01-01', NULL, 4);
INSERT INTO band (BandName, FormationDate, DisbandDate, ArtistID)VALUES  ('The Rolling Stones', '1962-07-12', NULL, 5);
INSERT INTO band(BandName, FormationDate, DisbandDate, ArtistID) VALUES	 ('Green Day', '1987-01-01', NULL, 6);
INSERT INTO band (BandName, FormationDate, DisbandDate, ArtistID)VALUES  ('Pulp' ,'1978-01-01' , NULL, 7);
INSERT INTO band (BandName, FormationDate, DisbandDate, ArtistID)VALUES  ('Suede' ,'1989-01-01' , NULL, 8);
INSERT INTO band (BandName, FormationDate, DisbandDate, ArtistID)VALUES  ('ABBA' ,'1972-01-01' , '1982-01-01', 9);
INSERT INTO band (BandName, FormationDate, DisbandDate, ArtistID)VALUES  ('The Kinks' ,'1963-01-01' , '1997-01-01', 10);


select * from band;
