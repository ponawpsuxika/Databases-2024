import random
import datetime
import json

# Ρυθμίσεις
NUM_RECORDS = 600000
OUTPUT_FILE = "concert_history.json"

# Υποθετικά εύρη IDs
MAX_CONCERT_ID = 2000
MAX_ARTIST_ID = 500
MAX_VENUE_ID = 300

# Λίστα τυχαίων ονομάτων για First_Name και Last_Name
first_names = ["John", "Emily", "Michael", "Sarah", "David", "Laura", "Robert", "Anna", "James", "Sophia"]
last_names = ["Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Wilson", "Taylor", "Anderson"]

# Ημερομηνίες (2000 -> σήμερα)
start_date = datetime.date(2000, 1, 1)
end_date = datetime.date.today()
date_range_days = (end_date - start_date).days

records = []

for i in range(NUM_RECORDS):
    concert_id = random.randint(1, MAX_CONCERT_ID)
    artist_id = random.randint(1, MAX_ARTIST_ID)
    venue_id = random.randint(1, MAX_VENUE_ID)

    # Τυχαία ημερομηνία
    random_days = random.randint(0, date_range_days)
    concert_date = start_date + datetime.timedelta(days=random_days)

    tickets_sold = random.randint(0, 50000)
    concert_status = random.choice(["COMPLETED", "CANCELLED"])

    # Τυχαία ονόματα
    first_name = random.choice(first_names)
    last_name = random.choice(last_names)

    record = {
        "ConcertID": concert_id,
        "ArtistID": artist_id,
        "VenueID": venue_id,
        "ConcertDate": str(concert_date),
        "TicketsSold": tickets_sold,
        "ConcertStatus": concert_status,
        "First_Name": first_name,
        "Last_Name": last_name
    }
    records.append(record)

# Αποθήκευση σε JSON
with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
    json.dump(records, f, indent=2, ensure_ascii=False)

print(f"✅ Δημιουργήθηκαν {NUM_RECORDS} εγγραφές στο {OUTPUT_FILE}")
