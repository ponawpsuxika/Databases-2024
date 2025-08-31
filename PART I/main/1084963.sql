DROP DATABASE IF EXISTS project2024;
CREATE DATABASE project2024;
USE project2024;

-- Table: artist
CREATE TABLE artist (
    ArtistID INT AUTO_INCREMENT PRIMARY KEY,
    ArtistType ENUM('PERSON','BAND','ORCHESTRA','CHOIR','VIRTUAL','OTHER') NOT NULL
);

-- Table: person
CREATE TABLE person (
    PersonID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(80) NOT NULL,
    Birthdate DATE NOT NULL,
    Country VARCHAR(30) NOT NULL,
    Alias VARCHAR(50),
    isSoloArtist TINYINT(1) NOT NULL,
    ArtistID INT,
    FOREIGN KEY (ArtistID)
        REFERENCES artist (ArtistID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table: band
CREATE TABLE band (
    BandID INT AUTO_INCREMENT PRIMARY KEY,
    BandName VARCHAR(50) NOT NULL,
    FormationDate DATE NOT NULL,
    DisbandDate DATE,
    ArtistID INT,
    FOREIGN KEY (ArtistID)
        REFERENCES artist (ArtistID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table: bandmember
CREATE TABLE bandmember (
    BandID INT,
    PersonID INT,
    FromDate DATE NOT NULL,
    ToDate DATE,
    PRIMARY KEY (BandID , PersonID),
    FOREIGN KEY (BandID)
        REFERENCES band (BandID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (PersonID)
        REFERENCES person (PersonID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table: recordcompany
CREATE TABLE recordcompany (
    CompanyID INT AUTO_INCREMENT PRIMARY KEY,
    CompanyName VARCHAR(80) NOT NULL,
    Address VARCHAR(150),
    Phone VARCHAR(20),
    Email VARCHAR(40) NOT NULL,
    BeginDate DATE NOT NULL,
    EndDate DATE
);

-- Table: artistcompany
-- This table right here explains to which record company an artist is assigned 
CREATE TABLE artistcompany (
    ArtistID INT NOT NULL,
    CompanyID INT NOT NULL,
    FromDate DATE NOT NULL,
    ToDate DATE,
    PRIMARY KEY (ArtistID , CompanyID),
    FOREIGN KEY (ArtistID) REFERENCES artist (ArtistID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (CompanyID) REFERENCES recordcompany (CompanyID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table: producer
CREATE TABLE producer (
    ProducerID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(80) NOT NULL,
    NumberOfProductions INT
);

-- Table: producercompany
-- This table right here explains to which record company an artist is assigned 
CREATE TABLE producercompany (
    ProducerID INT NOT NULL,
    CompanyID INT NOT NULL,
    ToDate DATE,
    PRIMARY KEY (ProducerID , CompanyID),
    FOREIGN KEY (ProducerID)
        REFERENCES producer (ProducerID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (CompanyID)
        REFERENCES recordcompany (CompanyID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table: genre
CREATE TABLE genre (
    GenreID INT AUTO_INCREMENT PRIMARY KEY,
    GenreName VARCHAR(30) NOT NULL
);

-- Table: album
CREATE TABLE album (
    AlbumID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(100) NOT NULL,
    ArtistID INT NOT NULL,
    GenreID INT NOT NULL,
    CompanyID INT NULL,
    ProducerID INT NOT NULL,
    FOREIGN KEY (ArtistID)
        REFERENCES artist (ArtistID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (GenreID)
        REFERENCES genre (GenreID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (CompanyID)
        REFERENCES recordcompany (CompanyID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ProducerID)
        REFERENCES producer (ProducerID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table: track
CREATE TABLE track (
    TrackID INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(100) NOT NULL,
    AlbumID INT NOT NULL,
    TrackLength TIME NOT NULL,
    TrackNo INT,
    Lyrics TEXT CHARACTER SET utf8mb4,
    FOREIGN KEY (AlbumID)
        REFERENCES album (AlbumID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Table: albumrelease
CREATE TABLE albumrelease (
    ReleaseID INT AUTO_INCREMENT PRIMARY KEY,
    AlbumID INT NOT NULL,
    ReleaseDate DATE NOT NULL,
    ReleaseType ENUM('LP', 'CD', 'MP3') NOT NULL,
    ReleaseStatus ENUM('OFFICIAL', 'PROMOTION', 'BOOTLEG', 'WITHDRAWN', 'CANCELLED') NOT NULL,
    Packaging ENUM('BOOK', 'CARDBOARD SLEEVE', 'DIGIPAK', 'JEWEL CASE', 'NOT APPLICABLE') NOT NULL,
    FOREIGN KEY (AlbumID)
        REFERENCES album (AlbumID)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Apart from the main DB , we create additional tables which are useful and needed for the next questions
CREATE TABLE venue (
    VenueID INT AUTO_INCREMENT PRIMARY KEY,
    VenueName VARCHAR(100) NOT NULL,
    Capacity INT NOT NULL,
    CompletedConcerts INT,
    YearsOperating INT,
    IsAvailable TINYINT(1) DEFAULT 1
);

-- Table: concert
CREATE TABLE concert (
    ConcertID INT AUTO_INCREMENT PRIMARY KEY,
    ArtistID INT NOT NULL,
    VenueID INT NULL,
    ConcertDate DATE NOT NULL,
    ConcertCapacity INT,
    TicketsSold INT DEFAULT 0,
    ConcertStatus ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED') NOT NULL,
    IsArchived TINYINT(1) DEFAULT 1,
    FOREIGN KEY (ArtistID)
        REFERENCES artist (ArtistID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (VenueID)
        REFERENCES venue (VenueID)
        ON DELETE CASCADE ON UPDATE CASCADE
);


-- Table: concert_history
CREATE TABLE concert_history (
    HistoricalID INT AUTO_INCREMENT PRIMARY KEY,
    First_Name VARCHAR (50) NOT NULL, 
    Last_Name VARCHAR (50) NOT NULL ,
    ConcertID INT NOT NULL,
    ArtistID INT NOT NULL,
    VenueID INT NOT NULL,
    ConcertDate DATE NOT NULL,
    TicketsSold INT NOT NULL,
    ConcertStatus ENUM('COMPLETED', 'CANCELLED') NOT NULL
);

-- Table: dba
-- This table is used to keep track of who has access to the DB and manages tables and records 
CREATE TABLE dba (
    DBA_ID INT AUTO_INCREMENT PRIMARY KEY,
    Username CHAR(15) UNIQUE NOT NULL,
    Start_Date DATE NOT NULL,
    End_Date DATE DEFAULT NULL
);


-- Table: dba_log
-- This is used to keep a log of all the actions made on the DB 
-- We keep a track of the logged in user and log exactly what they change , where they make that change and the time they do.
-- (afto to erwthma einai akoma kalutero sto Java implementation)
CREATE TABLE dba_log (
    LogID INT AUTO_INCREMENT PRIMARY KEY,
    ActionDate DATETIME NOT NULL DEFAULT NOW(),
    DBA_Username CHAR(15) NOT NULL,
    log_action VARCHAR(255),
    FOREIGN KEY (DBA_Username)
        REFERENCES dba (Username)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- Ευρετήρια (Indexes)
-- Index για την GetArtistsByTicketRange (διάστημα εισιτηρίων)
CREATE INDEX idx_tickets_sold ON concert_history (TicketsSold);

-- Index για την GetConcertDatesByVenue (χώρος διεξαγωγής)
CREATE INDEX idx_venue_id ON concert_history (VenueID);

-- Σύνθετο index για βελτιστοποίηση JOINs με artist πίνακα (afto tha xreiastei sto gui)
-- CREATE INDEX idx_artist_tickets ON concert_history (ArtistID, TicketsSold);
-- ALTER TABLE concert_history DROP INDEX idx_artist_tickets;

-- Index για ημερομηνία (χρήσιμο για χρονολογικές αναζητήσεις)
-- CREATE INDEX idx_concert_date ON concert_history (ConcertDate);
-- ALTER TABLE concert_history DROP INDEX idx_concert_date;


-- Απαίτηση 3.1.2.2 
/*Αν βρεθούν παραπάνω από ένας κατάλληλοι χώροι διεξαγωγής για την
συναυλία θα επιλέγεται αυτός με τη μεγαλύτερη βαθμολογία, η οποία θα
υπολογίζεται σύμφωνα με τα κριτήρια, όπως φαίνονται στον παρακάτω
πίνακα.
*/

DELIMITER $

DROP PROCEDURE IF EXISTS AssignBestVenue$
CREATE PROCEDURE AssignBestVenue(IN concert_id INT, IN required_capacity INT)
BEGIN
    DECLARE selected_venue INT DEFAULT NULL;
    DECLARE venue_score INT DEFAULT 0;
    DECLARE max_score INT DEFAULT 0;

    -- Declare variables to use for our cursor
    DECLARE v_venue_id INT;
    DECLARE v_capacity INT;
    DECLARE v_completed_concerts INT;
    DECLARE v_years_operating INT;
    DECLARE v_current_score INT;
    DECLARE done INT DEFAULT FALSE;

    -- This cursor scans every free venue , calculates each one's score  (depending on what the exercise wants) 
    -- and then assigns the best one (the one with the best score) for the concert
    DECLARE venue_cursor CURSOR FOR
        SELECT VenueID, Capacity, CompletedConcerts, YearsOperating
        FROM venue
        WHERE IsAvailable = 1 
        AND Capacity >= (required_capacity * 1.1)  -- 10% more capacity
        AND VenueID NOT IN (
            SELECT VenueID FROM concert 
            WHERE ConcertStatus = 'SCHEDULED' 
            AND VenueID IS NOT NULL
        ); -- Exclude venues with scheduled concerts , we need free venues to have our concerts

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- Initialize max score (simple iteration to find the best score)
    SET max_score = 0; 

    OPEN venue_cursor; 

    venue_loop: LOOP
        FETCH venue_cursor INTO v_venue_id, v_capacity, v_completed_concerts, v_years_operating;
        
        IF done THEN
            LEAVE venue_loop;
        END IF;

        -- Calculate score according to the table:
        -- For every 1,000 people capacity -> 1 point
        -- For every 100 completed concerts -> 3 points  
        -- For every year of operation -> 2 points
        SET v_current_score = 
            FLOOR(v_capacity / 1000) * 1 +
            FLOOR(v_completed_concerts / 100) * 3 +
            v_years_operating * 2;

        -- Select venue with highest score
        IF v_current_score > max_score THEN
            SET max_score = v_current_score;
            SET selected_venue = v_venue_id;
        END IF;

    END LOOP;

    CLOSE venue_cursor;

    -- If no venue is found, print an error so the user can know what went wrong and search again 
    IF selected_venue IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No suitable venue found for the concert with required capacity.';
    END IF;

    -- Update the concert with the selected venue
    UPDATE concert
    SET VenueID = selected_venue
    WHERE ConcertID = concert_id;

    -- Mark the venue as unavailable 
    UPDATE venue
    SET IsAvailable = 0
    WHERE VenueID = selected_venue;

    -- Return selected venue info for confirmation
    SELECT 
        selected_venue as SelectedVenueID,
        max_score as VenueScore,
        v.VenueName,
        v.Capacity
    FROM venue v 
    WHERE v.VenueID = selected_venue;

END$



-- trigger το οποιο ειναι χρησιμο για να εχω το ιστορικο συναυλιων σε περιπτωση που αλλαξει κατι στο status
DELIMITER $$

CREATE PROCEDURE archive_concerts_proc()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_ConcertID INT;
    DECLARE v_ArtistID INT;
    DECLARE v_VenueID INT;
    DECLARE v_ConcertDate DATE;
    DECLARE v_TicketsSold INT;
    DECLARE v_ConcertStatus VARCHAR(20);

    -- Cursor για όλες τις συναυλίες που πρέπει να αρχειοθετηθούν
    DECLARE cur CURSOR FOR
        SELECT ConcertID, ArtistID, VenueID, ConcertDate, TicketsSold, ConcertStatus
        FROM concert
        WHERE ConcertStatus IN ('COMPLETED', 'CANCELLED')
          AND IsArchived = FALSE;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO v_ConcertID, v_ArtistID, v_VenueID, v_ConcertDate, v_TicketsSold, v_ConcertStatus;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- Εισαγωγή στο ιστορικό
        INSERT INTO concert_history (
            ConcertID,
            ArtistID,
            VenueID,
            ConcertDate,
            TicketsSold,
            ConcertStatus
        )
        VALUES (
            v_ConcertID,
            v_ArtistID,
            v_VenueID,
            v_ConcertDate,
            v_TicketsSold,
            v_ConcertStatus
        );

        -- Κάνε το venue διαθέσιμο
        UPDATE venue
        SET IsAvailable = 1
        WHERE VenueID = v_VenueID;

        -- Αν ήταν COMPLETED, αύξησε completed count
        IF v_ConcertStatus = 'COMPLETED' THEN
            UPDATE venue
            SET CompletedConcerts = CompletedConcerts + 1
            WHERE VenueID = v_VenueID;
        END IF;

        -- Μαρκάρισμα ως αρχειοθετημένο
        UPDATE concert
        SET IsArchived = TRUE
        WHERE ConcertID = v_ConcertID;
    END LOOP;

    CLOSE cur;
END$$

DELIMITER ;



-- Απαίτηση 3.1.3.1 
-- Stored procedure η οποία παίρνει ως όρισμα το id ενός χώρου διεξαγωγής
-- και θα υπολογίζει και θα επιστρέφει το βαθμό του με βάση τον Πίνακα 2 της
-- Απαίτησης 3.1.2.2.

DROP PROCEDURE IF EXISTS CalculateVenueScore$
DELIMITER $
CREATE PROCEDURE CalculateVenueScore(IN venue_id INT) -- here, we use a variable named 'venue_id' , we type in the exact id we wanna search for 
														-- and then we fetch the results where venue_id = VenueID from the venue table	
BEGIN -- fetch the exact info the venue table holds for this specific 'venue_id' we are searching for
    SELECT 
        VenueID,
        VenueName,
        Capacity,
        CompletedConcerts,
        YearsOperating,
        IsAvailable,
        
        -- Calculate the exact score as the table explains
        (FLOOR(Capacity / 1000) * 1 + 
         FLOOR(CompletedConcerts / 100) * 3 + 
         YearsOperating * 2) AS VenueScore
    FROM venue
    WHERE VenueID = venue_id; 
END$
DELIMITER ;




-- Απαίτηση 3.1.3.2 
-- Stored procedure στην οποία θα δίνονται ως ορίσματα εισόδου το id ενός
-- καλλιτέχνη, μια ημερομηνία και ένας χαρακτήρας (‘i’, ‘c’ ή ‘a’). (πιο αναλυτικά στην εκφώνηση)

DELIMITER //

CREATE PROCEDURE ConcertManagement (
    IN p_ArtistID INT,
    IN p_ConcertDate DATE,
    IN p_Action CHAR(1)
)
BEGIN
    DECLARE v_ConcertID INT;
    DECLARE v_Status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED');
    
SELECT 
    ConcertID, ConcertStatus
INTO v_ConcertID , v_Status FROM
    concert
WHERE
    ArtistID = p_ArtistID
        AND ConcertDate = p_ConcertDate
LIMIT 1;
    
    -- Actions based on our needs
    IF p_Action = 'i' THEN
        IF v_ConcertID IS NOT NULL THEN
            IF v_Status = 'SCHEDULED' THEN
                SELECT 'There is a concert already scheduled for this date' AS Message;
            ELSEIF v_Status = 'CANCELLED' THEN
                SELECT 'This date\'s concert has already been cancelled before' AS Message;
            ELSE
                SELECT 'Concert is completed' AS Message;
            END IF;
        ELSE
            -- Schedule new concert (VenueID = 1, ConcertCapacity = 1000)
            -- Check if venue exists and is available
            IF EXISTS(SELECT 1 FROM venue WHERE VenueID = 1 AND IsAvailable = 1) THEN
                INSERT INTO concert (ArtistID, VenueID, ConcertDate, ConcertCapacity, ConcertStatus)
                VALUES (p_ArtistID, 1, p_ConcertDate, 1000, 'SCHEDULED');
                
                SELECT 'Concert has been scheduled' AS Message;
            ELSE
                SELECT 'Venue is not available for scheduling' AS Message;
            END IF;
        END IF;
        
    ELSEIF p_Action = 'c' THEN
        IF v_ConcertID IS NULL THEN
            SELECT 'No concert found for this date.' AS Message;
        ELSE
            IF v_Status = 'CANCELLED' THEN
                SELECT 'Concert already cancelled' AS Message;
            ELSEIF v_Status = 'SCHEDULED' THEN
                UPDATE concert
                SET ConcertStatus = 'CANCELLED'
                WHERE ConcertID = v_ConcertID;
SELECT 'Concert cancelled successfully' AS Message;
            ELSE
                SELECT 'Concert already completed and can\'t be cancelled.' AS Message;
            END IF;
        END IF;
        
    ELSEIF p_Action = 'a' THEN
        IF v_ConcertID IS NULL THEN
            SELECT 'There is no cancelled concert' AS Message;
        ELSE
            IF v_Status = 'CANCELLED' THEN
                UPDATE concert
                SET ConcertStatus = 'SCHEDULED'
                WHERE ConcertID = v_ConcertID;
SELECT 'Cancelled concert is back up again' AS Message;
            ELSEIF v_Status = 'SCHEDULED' THEN
                SELECT 'Concert is active' AS Message;
            ELSE
                SELECT 'Concert is already completed' AS Message;
            END IF;
        END IF;
        
    ELSE
        SELECT 'Invalid type of action. Please only use:  ''i'', ''c'' or ''a''.' AS Message;
    END IF;
END //

DELIMITER ;




-- Απαίτηση 3.1.3.3  
-- Stored procedure στην οποία δίνεται ως ορίσματα εισόδου το id μιας
-- συναυλίας και απαιτούμενη χωρητικότητα για τη συναυλία και επιστρέφει σε
-- μια μεταβλητή εξόδου το id χώρου διεξαγωγής και σε μια δεύτερη, την
-- χωρητικότητα του.

DELIMITER $$
DROP PROCEDURE IF EXISTS GetConcertVenueInfo$$
CREATE PROCEDURE GetConcertVenueInfo(
    IN concert_id INT,
    IN required_capacity INT,
    OUT venue_id INT,
    OUT venue_capacity INT
)
BEGIN
    DECLARE concert_exists INT DEFAULT 0;
    DECLARE concert_status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED');
    DECLARE assigned_venue_id INT DEFAULT NULL;
    
    -- Έλεγχος αν υπάρχει η συναυλία και η κατάστασή της
    SELECT COUNT(*), ConcertStatus, VenueID
    INTO concert_exists, concert_status, assigned_venue_id
    FROM concert
    WHERE ConcertID = concert_id;
    
    -- Αν δεν υπάρχει συναυλία με το συγκεκριμένο ID ή είναι ακυρωμένη
    IF concert_exists = 0 OR concert_status = 'CANCELLED' THEN
        SET venue_id = NULL;
        SET venue_capacity = 0;
    ELSE
        -- Αν έχει ήδη κλειστεί χώρος διεξαγωγής για τη συγκεκριμένη συναυλία
        IF assigned_venue_id IS NOT NULL THEN
            -- Επιστρέφουμε το ID και την χωρητικότητα του ήδη κλεισμένου χώρου
            SELECT assigned_venue_id, v.Capacity
            INTO venue_id, venue_capacity
            FROM venue v
            WHERE v.VenueID = assigned_venue_id;
        ELSE
            -- Αν δεν έχει κλειστεί χώρος, βρίσκουμε τον καλύτερο διαθέσιμο
            -- χρησιμοποιώντας την ίδια λογική με την AssignBestVenue
            CALL AssignBestVenueForConcert(concert_id, required_capacity, venue_id, venue_capacity);
        END IF;
    END IF;
END$$

-- Βοηθητική stored procedure για εύρεση καλύτερου χώρου
DROP PROCEDURE IF EXISTS AssignBestVenueForConcert$$
CREATE PROCEDURE AssignBestVenueForConcert(
    IN concert_id INT,
    IN required_capacity INT,
    OUT selected_venue_id INT,
    OUT selected_venue_capacity INT
)
BEGIN
    DECLARE max_score INT DEFAULT 0;
    DECLARE v_venue_id INT;
    DECLARE v_capacity INT;
    DECLARE v_completed_concerts INT;
    DECLARE v_years_operating INT;
    DECLARE v_current_score INT;
    DECLARE done INT DEFAULT FALSE;

    -- Cursor για εύρεση κατάλληλων χώρων
    DECLARE venue_cursor CURSOR FOR
        SELECT VenueID, Capacity, CompletedConcerts, YearsOperating
        FROM venue
        WHERE IsAvailable = 1 
        AND Capacity >= (required_capacity * 1.1)  -- 10% περισσότερη χωρητικότητα
        AND VenueID NOT IN (
            SELECT VenueID FROM concert 
            WHERE ConcertStatus = 'SCHEDULED' 
            AND VenueID IS NOT NULL
            AND ConcertID != concert_id  -- Εξαιρούμε την τρέχουσα συναυλία
        );

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- Αρχικοποίηση
    SET selected_venue_id = NULL;
    SET selected_venue_capacity = 0;
    SET max_score = 0;

    OPEN venue_cursor;

    venue_loop: LOOP
        FETCH venue_cursor INTO v_venue_id, v_capacity, v_completed_concerts, v_years_operating;
        
        IF done THEN
            LEAVE venue_loop;
        END IF;

        -- Υπολογισμός βαθμολογίας
        SET v_current_score = 
            FLOOR(v_capacity / 1000) * 1 +
            FLOOR(v_completed_concerts / 100) * 3 +
            v_years_operating * 2;

        -- Επιλογή χώρου με μεγαλύτερη βαθμολογία
        IF v_current_score > max_score THEN
            SET max_score = v_current_score;
            SET selected_venue_id = v_venue_id;
            SET selected_venue_capacity = v_capacity;
        END IF;

    END LOOP;

    CLOSE venue_cursor;
    
    -- Αν δεν βρέθηκε κατάλληλος χώρος
    IF selected_venue_id IS NULL THEN
        SET selected_venue_capacity = 0;
    END IF;
END$$

DELIMITER ;



-- Απαίτηση 3.1.3.4 
-- α) Stored procedure για εύρεση καλλιτεχνών βάσει πλήθους εισιτηρίων
-- We use the index 'idx_tickets_sold' for quick search in the ticket range

DELIMITER $$

DROP PROCEDURE IF EXISTS GetArtistsByTicketRange$$
CREATE PROCEDURE GetArtistsByTicketRange(
    IN min_tickets INT,
    IN max_tickets INT
)
BEGIN
    SELECT First_Name, Last_Name, TicketsSold
    FROM concert_history
    WHERE TicketsSold BETWEEN min_tickets AND max_tickets
    ORDER BY First_Name, Last_Name;
END$$

DELIMITER ;



-- TΟ ΠΑΡΑΚΑΤΩ STORED PROCEDURE ΔΗΜΙΟΥΡΓΗΘΗΚΕ ΑΛΛΑ ΔΕΝ ΜΠΟΡΟΥΣΑ ΝΑ ΤΟ ΕΚΤΕΛΕΣΩ 
-- ΓΙΑ ΤΟΝ ΕΛΕΓΧΟ ΤΟΥ INDEX ΚΑΘΩΣ ΑΠΑΙΤΟΥΣΕ ΤΗΝ ΕΙΣΑΓΩΓΗ ΠΟΛΛΩΝ ΕΓΓΡΑΦΩΝ ΚΑΙ ΣΤΟΝ ΠΙΝΑΚΑ VENUES 
-- ΚΑΤΙ ΠΟΥ ΗΤΑΝ ΔΥΣΚΟΛΟ ΕΠΕΙΔΗ ΤΟ ΠΕΡΙΒΑΛΛΟΝ ΤΗΣ SQL ΕΚΑΝΕ HANG ΣΥΝΕΧΩΣ ΚΑΤΑ ΤΗΝ ΕΙΣΑΓΩΓΗ ΠΟΛΛΩΝ ΕΓΓΡΑΦΩΝ 

/* β) Stored procedure στην οποία δίνεται ως παράμετρος εισόδου το όνομα
-- ενός χώρου διεξαγωγής συναυλιών και εμφανίζει τις ημερομηνίες που έγιναν
-- συναυλίες στον συγκεκριμένο χώρο
-- We use the previously created index named 'idx_venue_id' for a quick search by venue 
-- DELIMITER $$
-- DROP PROCEDURE IF EXISTS GetConcertDatesByVenue$$
CREATE PROCEDURE GetConcertDatesByVenue(
    IN venue_name VARCHAR(100)
)
BEGIN
    -- Αναζήτηση στον πίνακα ιστορικού με χρήση του index idx_venue_id
    SELECT 
        v.VenueName,
        ch.ConcertDate,
        CASE 
            WHEN a.ArtistType = 'PERSON' THEN CONCAT(p.FirstName, ' ', p.LastName)
            WHEN a.ArtistType = 'BAND' THEN b.BandName
        END AS ArtistName,
        ch.ConcertStatus,
        ch.TicketsSold,
        'HISTORICAL' AS Source
    FROM concert_history ch
    INNER JOIN venue v ON ch.VenueID = v.VenueID
    INNER JOIN artist a ON ch.ArtistID = a.ArtistID
    LEFT JOIN person p ON a.ArtistID = p.ArtistID AND a.ArtistType = 'PERSON'
    LEFT JOIN band b ON a.ArtistID = b.ArtistID AND a.ArtistType = 'BAND'
    WHERE v.VenueName = venue_name

    
    ORDER BY ConcertDate;
END$$

DELIMITER ;

*/

-- Απαίτηση 3.1.4.1 
-- Trigger που θα ενημερώνουν το σχετικό πίνακα καταγραφής ενεργειών
-- (log) για κάθε ενέργεια εισαγωγής, ενημέρωσης ή διαγραφής στους πίνακες
-- person, band, album, concert, venue με την ημερομηνία και ώρα και το
-- username του DBA που την εκτέλεσε.

SET @current_dba := 'DBA_USERNAME'; -- we set the current DBA manually 

DELIMITER $$

-- Artist change log
DROP TRIGGER IF EXISTS artist_log_insert$$
CREATE TRIGGER artist_log_insert
AFTER INSERT ON artist
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Inserted data in table: artist');
END$$

DROP TRIGGER IF EXISTS artist_log_update$$
CREATE TRIGGER artist_log_update
AFTER UPDATE ON artist
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Modified data in table: artist');
END$$

DROP TRIGGER IF EXISTS artist_log_delete$$
CREATE TRIGGER artist_log_delete
AFTER DELETE ON artist
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Deleted data in table: artist');
END$$

-- Table 'person' changes log
DROP TRIGGER IF EXISTS person_log_insert$$
CREATE TRIGGER person_log_insert
AFTER INSERT ON person
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Inserted data in table: person');
END$$

DROP TRIGGER IF EXISTS person_log_update$$
CREATE TRIGGER person_log_update
AFTER UPDATE ON person
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Modified data in table: person');
END$$

DROP TRIGGER IF EXISTS person_log_delete$$
CREATE TRIGGER person_log_delete
AFTER DELETE ON person
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Deleted data in table: person');
END$$

-- Band changes log

DROP TRIGGER IF EXISTS band_log_insert$$
CREATE TRIGGER band_log_insert
AFTER INSERT ON band
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Inserted data in table: band');
END$$

DROP TRIGGER IF EXISTS band_log_update$$
CREATE TRIGGER band_log_update
AFTER UPDATE ON band
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Modified data in table: band');
END$$

DROP TRIGGER IF EXISTS band_log_delete$$
CREATE TRIGGER band_log_delete
AFTER DELETE ON band
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Deleted data in table: band');
END$$

-- Album changes log

DROP TRIGGER IF EXISTS album_log_insert$$
CREATE TRIGGER album_log_insert
AFTER INSERT ON album
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Inserted data in table: album');
END$$

DROP TRIGGER IF EXISTS album_log_update$$
CREATE TRIGGER album_log_update
AFTER UPDATE ON album
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Modified data in table: album');
END$$

DROP TRIGGER IF EXISTS album_log_delete$$
CREATE TRIGGER album_log_delete
AFTER DELETE ON album
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Deleted data in table: album');
END$$

-- Concert changes log

DROP TRIGGER IF EXISTS concert_log_insert$$
CREATE TRIGGER concert_log_insert
AFTER INSERT ON concert
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Inserted data in table: concert');
END$$

DROP TRIGGER IF EXISTS concert_log_update$$
CREATE TRIGGER concert_log_update
AFTER UPDATE ON concert
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Modified data in table: concert');
END$$

DROP TRIGGER IF EXISTS concert_log_delete$$
CREATE TRIGGER concert_log_delete
AFTER DELETE ON concert
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Deleted data in table: concert');
END$$

-- Venue changes log

DROP TRIGGER IF EXISTS venue_log_insert$$
CREATE TRIGGER venue_log_insert
AFTER INSERT ON venue
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Inserted data in table: venue');
END$$

DROP TRIGGER IF EXISTS venue_log_update$$
CREATE TRIGGER venue_log_update
AFTER UPDATE ON venue
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Modified data in table: venue');
END$$

DROP TRIGGER IF EXISTS venue_log_delete$$
CREATE TRIGGER venue_log_delete
AFTER DELETE ON venue
FOR EACH ROW
BEGIN
    INSERT INTO dba_log (ActionDate, DBA_Username, log_action)
    VALUES (NOW(), @current_dba, 'Deleted data in table: venue');
END$$

DELIMITER ;



-- Απαίτηση 3.1.4.2 
-- Trigger ο οποίος θα αποτρέπει τον προγραμματισμό νέας συναυλίας, αν η
-- ημερομηνία που γίνεται ο προγραμματισμός της συναυλίας απέχει λιγότερο
-- από πέντε μέρες από την ημερομηνία διεξαγωγής της ή αν ο συγκεκριμένος
-- καλλιτέχνης έχει ήδη τρεις προγραμματισμένες συναυλίες.

-- Prevent excessive concert scheduling 
DELIMITER $$
DROP TRIGGER IF EXISTS prevent_too_many_concerts$$
CREATE TRIGGER prevent_too_many_concerts
BEFORE INSERT ON concert
FOR EACH ROW
BEGIN
    DECLARE scheduled_count INT;
    SELECT COUNT(*) INTO scheduled_count
    FROM concert
    WHERE ArtistID = NEW.ArtistID AND ConcertStatus = 'SCHEDULED';
    
    IF scheduled_count >= 3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Cannot schedule a new concert: Artist already has 3 scheduled concerts.';
    END IF;
END$$

-- Prevent scheduling less than 5 days in advance
DROP TRIGGER IF EXISTS prevent_last_minute_scheduling$$
CREATE TRIGGER prevent_last_minute_scheduling
BEFORE INSERT ON concert
FOR EACH ROW
BEGIN
    -- Error checking only when concert != cancelled or concert!= completed
    IF NEW.ConcertStatus IN ('SCHEDULED') THEN
        IF DATEDIFF(NEW.ConcertDate, CURDATE()) < 5 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot schedule a concert less than 5 days in advance.';
        END IF;
    END IF;
END$$



-- Απαίτηση 3.1.4.3 
-- Trigger ο οποίος θα αποτρέπει την ακύρωση μιας συναυλίας, αν η
-- ημερομηνία που γίνεται η ακύρωση απέχει λιγότερο από τρεις μέρες από
-- την ημερομηνία διεξαγωγής της. Επίσης, θα αποτρέπει τον
-- επαναπρογραμματισμό ακυρωμένης συναυλίας αν ο συγκεκριμένος
-- καλλιτέχνης έχει ήδη τρεις προγραμματισμένες συναυλίες.
DELIMITER $$
DROP TRIGGER IF EXISTS prevent_late_cancellation_and_reschedule$$
CREATE TRIGGER prevent_late_cancellation_and_reschedule
BEFORE UPDATE ON concert
FOR EACH ROW
BEGIN
DECLARE scheduled_count INT;
    -- Cannot cancel 3 days before the concert , error checking
    IF NEW.ConcertStatus = 'CANCELLED' AND OLD.ConcertStatus != 'CANCELLED' THEN
        IF DATEDIFF(NEW.ConcertDate, CURDATE()) < 3 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot cancel a concert less than 3 days before the event.';
        END IF;
    END IF;

    -- Same as above with the excessive scheduling
    IF OLD.ConcertStatus = 'CANCELLED' AND NEW.ConcertStatus != 'CANCELLED' THEN

        SELECT COUNT(*)
        INTO scheduled_count
        FROM concert
        WHERE ArtistID = NEW.ArtistID
          AND ConcertStatus = 'SCHEDULED'
          AND ConcertID != NEW.ConcertID;

        -- If the artist already has 3 concerts scheduled.. you can't schedule a new one
        IF scheduled_count >= 3 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot reschedule a cancelled concert because the artist already has 3 scheduled concerts.';
        END IF;
    END IF;

END$$

DELIMITER ;




-- Miscellaneous
-- Just a quick check on whether the DBA we want to use exists or not 

DELIMITER $$
CREATE PROCEDURE set_dba_user(IN input_username CHAR(15))
BEGIN
    DECLARE end_date_val DATE;
    DECLARE user_exists INT DEFAULT 0;
    
    SELECT COUNT(*) INTO user_exists
    FROM dba
    WHERE Username = input_username;
    
    IF user_exists = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'User does not exist in DBA table.';
    ELSE
        SELECT End_Date INTO end_date_val
        FROM dba
        WHERE Username = input_username;
        
        IF end_date_val IS NOT NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'User is no longer affiliated with us (End_Date: NOT NULL). Cannot use this DBA.';
        ELSE
            SELECT 'User is active. Continue' AS Message;
        END IF;
    END IF;
END$$

DELIMITER ;