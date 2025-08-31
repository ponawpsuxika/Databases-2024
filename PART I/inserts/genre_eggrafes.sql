SET @current_dba := 'admin'; -- an den uparxei kapoios DBA poy na einai logged in , den epitrepetai h allagh se pinakes 

INSERT INTO genre (GenreName) VALUES 
('Rock'), ('Pop'), ('Jazz'), ('Classical'), ('Hip Hop'), 
('Country'), ('Blues'), ('Electronic'), ('Folk'), ('R&B'),
('Reggae'), ('Punk'), ('Metal'), ('Alternative'), ('Indie'),
('Soul'), ('Funk'), ('Disco'), ('House'), ('Techno'),
('Trance'), ('Dubstep'), ('Ambient'), ('New Age'), ('World'),
('Latin'), ('Salsa'), ('Bossa Nova'), ('Flamenco'), ('Celtic'),
('Gospel'), ('Spiritual'), ('Opera'), ('Symphony'), ('Chamber'),
('Bluegrass'), ('Rockabilly'), ('Swing'), ('Bebop'), ('Fusion'),
('Progressive'), ('Psychedelic'), ('Grunge'), ('Emo'), ('Hardcore'),
('Ska'), ('Reggaeton'), ('Trap'), ('Lo-Fi'), ('Experimental');

select * from genre;