select * from concert_history;

-- Test xronou me to index idx_tickets_sold
SET profiling = 1;
CALL GetArtistsByTicketRange(100, 50000);
SHOW PROFILES;
SET profiling = 0;

-- Check index usage
EXPLAIN SELECT * FROM concert_history WHERE TicketsSold BETWEEN 3000 AND 4000;

-- diagrafh the index
ALTER TABLE concert_history DROP INDEX idx_tickets_sold;
-- Test xwris index
SET profiling = 1;
CALL GetArtistsByTicketRange(3000, 4000);
SHOW PROFILES;
SET profiling = 0;

EXPLAIN SELECT * FROM concert_history WHERE TicketsSold BETWEEN 3000 AND 4000;

CREATE INDEX idx_tickets_sold ON concert_history(tickets_sold);
