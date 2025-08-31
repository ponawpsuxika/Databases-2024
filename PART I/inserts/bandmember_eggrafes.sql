SET @current_dba := 'admin'; -- an den uparxei kapoios DBA poy na einai logged in , den epitrepetai h allagh se pinakes 


INSERT INTO bandmember (BandID, PersonID, FromDate, ToDate) VALUES (1,1,'2002-01-01' , NULL); -- alex turner
INSERT INTO bandmember (BandID, PersonID, FromDate, ToDate) VALUES (1,2,'2002-01-01' , NULL); -- matt helders
INSERT INTO bandmember (BandID, PersonID, FromDate, ToDate) VALUES (3,3,'1982-01-01' , '1987-01-01'); -- johnny marr
INSERT INTO bandmember (BandID, PersonID, FromDate, ToDate) VALUES (3,4,'1982-01-01' , '1987-01-01'); -- mozz
INSERT INTO bandmember (BandID, PersonID, FromDate, ToDate) VALUES (8,5,'1989-01-01' , NULL); -- brett anderson
INSERT INTO bandmember (BandID, PersonID, FromDate, ToDate) VALUES (6,6,'1987-01-01' , NULL); -- Billie Joe

select * from bandmember;