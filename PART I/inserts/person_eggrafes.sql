SET @current_dba := 'admin'; -- an den uparxei kapoios DBA poy na einai logged in , den epitrepetai h allagh se pinakes 

INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Alex David' , 'Turner' , '1986-01-06' , 'United Kingdom' , NULL , 0 , 11);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Matthew' , 'Helders' , '1986-05-07' , 'United Kingdom' , 'Agile Beast' , 0 , 12);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('John Martin' , 'Maher' , '1966-10-31' , 'United Kingdom' , 'Johnny Marr' , 0 , 13);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Steven Patrick' , 'Morrissey' , '1959-05-22' , 'United Kingdom' , 'Morrissey' , 0 , 14);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Brett Lewis' , 'Anderson' , '1967-09-29' , 'United Kingdom' , NULL , 0 , 15);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Billie Joe' , 'Armstrong' , '1972-02-17' , 'USA' , NULL , 0 , 16);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Elizabeth' , 'Grant' , '1985-06-21' , 'USA' , 'Lana Del Rey' , 1 , 17);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Agneta' , 'Fältskog' , '1950-04-05' , 'Sweden' , 'Agnetha' , 0 , 18);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Peter' , 'Kinnes' , '1943-12-31' , 'United Kingdom' , 'Pete Quaife' , 0 , 19);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('David' , 'Jones' , '1947-01-08' , 'United Kingdom' , 'David Bowie' , 1 , 20);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Adrianne' , 'Lenker' , '1991-07-09' , 'USA' , NULL , 1 , 21);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Miles' , 'Kane' , '1986-03-17' , 'United Kingdom' , NULL , 1 , 22);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Ian' , 'Brown' , '1963-02-20' , 'United Kingdom' , NULL , 0 , 23);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('John ' , 'Mellor' , '1952-08-21' , 'United Kingdom' , 'Joe Strummer' , 0 , 24);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Damon' , 'Albarn' , '1968-03-23' , 'United Kingdom' , NULL , 0 , 25);
INSERT INTO person (FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID)VALUES ('Madonna' , 'Turner' , '1958-08-16' , 'USA' , 'Madonna' , 1 , 26);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Angela' , 'Trimble' , '1945-06-01' , 'USA' , 'Debbie Harry' , 0 , 27);
INSERT INTO person (FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID)VALUES ('Nelly Kim' , 'Furtado' , '1978-12-02' , 'Canada' , NULL , 1 , 28);
INSERT INTO person(FirstName, LastName, Birthdate, Country, Alias, isSoloArtist, ArtistID) VALUES ('Steve Thomas' , 'Lacy-Moya' , '1998-05-23' , 'USA' , 'Steve Lacy' , 1 , 29);

select * from person;
