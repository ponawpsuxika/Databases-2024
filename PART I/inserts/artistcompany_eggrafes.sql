SET @current_dba := 'admin'; -- an den uparxei kapoios DBA poy na einai logged in , den epitrepetai h allagh se pinakes 

INSERT INTO artistcompany (ArtistID, CompanyID, FromDate ,ToDate) VALUES
(1, 25,'2005-01-01', NULL),  -- arctic monkeys - domino records
(2, 11, '1991-01-01', '2019-09-16'), -- stone roses  , geffen 
(3, 4, '1982-01-01','1987-01-01'),  -- smiths , warner
(4, 9, '1985-01-01', NULL), -- radiohead , random
(5, 2, '1983-01-01', '1993-01-01'), -- rolling stones , columbia
(6, 16, '1994-01-01', NULL ), -- green day , reprise 
(7, 24, '2024-12-12', NULL), -- pulp, rough trade 
(8, 31, '1994-01-01' , '1999-01-01'), -- suede , random
(9, 48, '1972-05-31' , '1981-10-25'), -- abba , random
(10, 32, '1965-09-03' , '1969-02-17'), -- the kinks , random
 -- ------------------------------------------
(11, 25,'2005-01-01', NULL), -- alex turner , arctic monkeys
(12, 25,'2005-01-01', NULL), -- matt helders , arctic monkeys 
(13, 4, '1982-01-01','1987-01-01'), -- johnny marr  , smiths
(14,4, '1982-01-01','1987-01-01'), -- mozz , smiths
(15, 48, '1994-01-01' , '1999-01-01'), -- brett anderson , suede
(16, 16, '1994-01-01', NULL), -- billie joe , green day
(17, 43, '2011-01-01' ,  NULL), -- lana del rey , solo
(18, 48, '1972-05-31' , '1981-10-25'), -- agnetha  , abba
(19, 32, '1965-09-03' , '1969-02-17'), -- pete quaife , kinks
(20, 20, '1977-01-01', '2015-01-01'), -- bowie , solo
(21, 21, '2014-01-01',NULL), -- adrianne lenker , solo
(22, 22, '2011-01-01', '2023-01-01'), -- miles kane , solo
(23, 11, '1991-01-01', '2019-09-16'), -- ian brown , stone roses
(24, 24, '1980-01-01' , '1990-08-13'), -- joe strummer , solo
(25, 25, '1988-06-30' , '1996-01-21'), -- damon albarn , solo
(26, 26, '1976-12-31' , '2002-08-05'), -- madonna , solo
(27, 27, '2015-12-31', NULL), -- debbie harry , solo
(28, 28, '2005-03-07' , NULL); -- nelly furtado , solo




select * from artistcompany;