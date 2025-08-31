package dbmanager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.Map;
import java.sql.Date;

public class DatabaseUI extends JFrame {
    private JComboBox<String> tableSelector;
    private JTable table;
    private DefaultTableModel tableModel;
    private Connection conn;

    // DBA Management fields
    private String currentDBAUsername;
    private Integer currentDBAId;
    private JLabel dbaStatusLabel;

    public DatabaseUI() {
        setTitle("Διαχείριση Βάσης Δεδομένων");
        setSize(2002, 1181);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
        }

        connectToDB();

        if (!authenticateDBA()) {
            System.exit(0);
            return;
        }

        setupGUI();
    }

    private void setupGUI() {
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel dbaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dbaStatusLabel = new JLabel("Welcome \n" + currentDBAUsername + " (ID: " + currentDBAId + ")");
        dbaStatusLabel.setForeground(Color.RED);
        dbaStatusLabel.setFont(dbaStatusLabel.getFont().deriveFont(Font.BOLD));

        // Κουμπιά για διαχείριση DBA
        JButton logoutBtn = new JButton("Αλλαγή DBA");
        logoutBtn.addActionListener(e -> changeDBA());
        JButton manageDBABtn = new JButton("Διαχείριση DBA");
        manageDBABtn.addActionListener(e -> manageDBAUsers());
        // Εμφάνιση διαθέσιμων χώρων και βαθμολογίες τους
        JButton viewVenuesBtn = new JButton("Διαθέσιμοι Χώροι & Βαθμολογίες");
        viewVenuesBtn.addActionListener(e -> showVenuesWithScores());

        // Κουμπιά για εμφάνιση σχέσεων
        JButton viewProducerCompanyBtn = new JButton("Παραγωγοί - Δισκογραφικές");
        viewProducerCompanyBtn.addActionListener(e -> showProducerCompanyRelations());

        JButton viewBandMembersBtn = new JButton("Μέλη Συγκροτημάτων");
        viewBandMembersBtn.addActionListener(e -> showBandMembers());

        JButton viewArtistCompanyBtn = new JButton("Καλλιτέχνες - Δισκογραφικές");
        viewArtistCompanyBtn.addActionListener(e -> showArtistCompanyRelations());

        // Album relation buttons
        JButton viewAlbumArtistBtn = new JButton("Album - Καλλιτέχνης");
        viewAlbumArtistBtn.addActionListener(e -> showAlbumArtistRelations());

        JButton viewAlbumReleasesBtn = new JButton("Album - Releases");
        viewAlbumReleasesBtn.addActionListener(e -> showAlbumReleases());

        JButton viewAlbumTracksBtn = new JButton("Album - Tracks");
        viewAlbumTracksBtn.addActionListener(e -> showAlbumTracks());

        dbaPanel.add(dbaStatusLabel);
        dbaPanel.add(logoutBtn);
        dbaPanel.add(manageDBABtn);
        dbaPanel.add(viewVenuesBtn);
        dbaPanel.add(viewProducerCompanyBtn);
        dbaPanel.add(viewBandMembersBtn);
        dbaPanel.add(viewArtistCompanyBtn);
        dbaPanel.add(viewAlbumArtistBtn);
        dbaPanel.add(viewAlbumReleasesBtn);
        dbaPanel.add(viewAlbumTracksBtn);

        topPanel.add(dbaPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());

        tableSelector = new JComboBox<>();
        tableSelector.addActionListener(e -> {
            String sel = (String) tableSelector.getSelectedItem();
            if (sel != null && !sel.startsWith("---")) {
                loadTableData(sel);
            }
        });

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);

        // Κουμπιά για διαχείριση των εγγραφών στους πίνακες της ΒΔ
        JPanel controls = new JPanel();
        JButton insertBtn = new JButton("Εισαγωγή");
        insertBtn.addActionListener(e -> insertRow()); // χρήση της συνάρτησης insertRow για να γίνει εισαγωγή των
                                                       // δεδομένων
        JButton updateBtn = new JButton("Ενημέρωση");
        updateBtn.addActionListener(e -> updateRow()); // χρήση της συνάρτησης updateRow για να γίνει ενημέρωση των
                                                       // δεδομένων
        JButton deleteBtn = new JButton("Διαγραφή");
        deleteBtn.addActionListener(e -> deleteRow()); // χρήση της συνάρτησης deleteRow για να γίνει διαγραφή των
                                                       // δεδομένων

        controls.add(new JLabel("Πίνακας:"));
        controls.add(tableSelector); // Εμφάνιση πινάκων που υπάρχουν στην ΒΔ
        controls.add(insertBtn); // κουμπί εισαγωγής
        controls.add(updateBtn); // κουμπί ενημέρωσης
        controls.add(deleteBtn); // κουμπί διαγραφής

        mainPanel.add(controls, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::loadTables);
    }

    // Σύνδεση στη ΒΔ (εισαγωγή των credentials manually)
    private void connectToDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Ο MySQL JDBC driver δεν βρέθηκε: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } // Αναγκαίο σε περίπτωση που ο χρήστης δεν εχει εγκατεστημένο τον JDBC driver

        // Εισαγωγή των credentials για σύνδεση στην DB (υπάρχουν ήδη προκαθορισμένα
        // αυτά που χρησιμοποιώ εγώ)
        while (true) {
            String host = JOptionPane.showInputDialog(this, "MySQL Host:", "localhost");
            if (host == null)
                System.exit(0);
            String port = JOptionPane.showInputDialog(this, "Port:", "3306");
            if (port == null)
                System.exit(0);
            String dbName = JOptionPane.showInputDialog(this, "Όνομα βάσης δεδομένων:", "project2024_gui");
            if (dbName == null)
                System.exit(0);
            String user = JOptionPane.showInputDialog(this, "Username:", "root");
            if (user == null)
                System.exit(0);
            JPasswordField pf = new JPasswordField();
            int okCxl = JOptionPane.showConfirmDialog(this, pf, "Εισάγετε password", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (okCxl != JOptionPane.OK_OPTION)
                System.exit(0);
            String password = new String(pf.getPassword());

            // error handling
            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName
                    + "?serverTimezone=UTC&allowPublicKeyRetrieval=true";
            try {
                conn = DriverManager.getConnection(url, user, password);
                break;
            } catch (SQLException e) {
                int retry = JOptionPane.showConfirmDialog(this,
                        "Σφάλμα σύνδεσης με τη βάση δεδομένων:\n" + e.getMessage() + "\n\nΘέλετε να δοκιμάσετε ξανά;",
                        "Σφάλμα σύνδεσης",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE);

                if (retry != JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(this, "Το πρόγραμμα θα τερματίσει.", "Έξοδος",
                            JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
        }
    }

    // Θέτουμε session variable για τον τρέχοντα DBA ώστε να μπορούν τα triggers να
    // το διαβάσουν
    // Αυτό είχε αναφερθεί και στο μέρος Α όπου δουλεύαμε στο MySQL Workbench
    private void setSessionDBA(String username) {
        if (conn == null || username == null)
            return;
        try (Statement s = conn.createStatement()) {
            String safe = username.replace("'", "''");
            s.execute("SET @current_dba = '" + safe + "'");
        } catch (SQLException e) {
            System.err.println("Δεν έγινε η ανάθεση της session variable: " + e.getMessage());
        }
    }

    // Αυθεντικοποίηση DBA
    private boolean authenticateDBA() {
        while (true) {
            String username = JOptionPane.showInputDialog(this,
                    "Εισάγετε το Username του DBA:",
                    "Αυθεντικοποίηση DBA",
                    JOptionPane.QUESTION_MESSAGE);

            if (username == null)
                return false;
            username = username.trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Παρακαλώ εισάγετε έγκυρο username.",
                        "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            try (PreparedStatement ps = conn
                    .prepareStatement("SELECT DBA_ID, Username FROM dba WHERE Username = ? AND End_Date IS NULL")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentDBAId = rs.getInt("DBA_ID");
                        currentDBAUsername = rs.getString("Username");
                        setSessionDBA(currentDBAUsername);
                        JOptionPane.showMessageDialog(this,
                                "Καλώς ήρθατε " + currentDBAUsername + "!\n",
                                "Επιτυχής είσοδος", JOptionPane.INFORMATION_MESSAGE);
                        return true;
                    }
                }

                // Σε περίπτωση που δεν υπάρχει ενεργός DBA , δημιουργούμε νέο και κάνουμε login
                // με αυτόν
                try (Statement s2 = conn.createStatement();
                        ResultSet cntRs = s2.executeQuery("SELECT COUNT(*) FROM dba WHERE End_Date IS NULL")) {
                    int activeCount = 0;
                    if (cntRs.next())
                        activeCount = cntRs.getInt(1);
                    if (activeCount == 0) {
                        int create = JOptionPane.showConfirmDialog(this,
                                "Δεν υπάρχει ενεργός DBA. Θέλετε να δημιουργήσετε έναν διαχειριστή τώρα?",
                                "Δημιουργία Διαχειριστή",
                                JOptionPane.YES_NO_OPTION);
                        if (create == JOptionPane.YES_OPTION) {
                            addNewDBA();
                            if (currentDBAUsername != null) {
                                JOptionPane.showMessageDialog(this,
                                        "Καλώς ήρθατε " + currentDBAUsername
                                                + "!",
                                        "Επιτυχής είσοδος", JOptionPane.INFORMATION_MESSAGE);
                                return true;
                            }
                        }
                    }
                }
                // Σε περίπτωση που δεν βρεθεί κάποιος ενεργός DBA, το εμφανίζουμε στον χρήστη
                JOptionPane.showMessageDialog(this, "Δεν βρέθηκε ενεργός DBA με username: " + username, "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Σφάλμα κατά την επαλήθευση: " + e.getMessage(), "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Συνάρτηση για αλλαγή του DBA
    private void changeDBA() {
        int result = JOptionPane.showConfirmDialog(this,
                "Θέλετε να αλλάξετε DBA? ",
                "Αλλαγή DBA",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            currentDBAId = null; // μεταβλητή για να λάβει το ID του DBA που θέλει να συνδεθεί
            currentDBAUsername = null; // μεταβλητή για να λάβει το username του DBA που θέλει να συνδεθεί

            // αν έχουμε επιτυχή αυθεντικοποίηση
            if (authenticateDBA()) {
                dbaStatusLabel.setText("DBA: " + currentDBAUsername + " (ID: " + currentDBAId + ")");
                JOptionPane.showMessageDialog(this,
                        "Επιτυχής αλλαγή DBA σε: " + currentDBAUsername,
                        "Αλλαγή DBA",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.exit(0);
            }
        }
    }

    // Συναρτηση για την διαχειριση των DBA χρηστών
    private void manageDBAUsers() {
        String[] options = { "Προσθήκη νέου DBA", "Προβολή όλων των DBA", "Διαγραφή DBA", "Ακύρωση" };
        int choice = JOptionPane.showOptionDialog(this,
                "Τι θέλετε να κάνετε;",
                "Διαχείριση DBA Χρηστών",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0:
                addNewDBA(); // Προσθήκη νέου DBA
                break;
            case 1:
                viewAllDBAs(); // Προβολή όλων των DBA
                break;
            case 2:
                deleteDBA(); // Διαγραφή DBA
                break;
            default:
                break;
        }
    }

    // Προσθήκη νέου DBA
    private void addNewDBA() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        JTextField usernameField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        // Δημιουργουμε πεδια για την εισαγωγη των δεδομενων του DBA .
        // Ο,τι γράφουμε , περνάει στην ΒΔ project2024_gui
        // στον πινακα 'dba' που περιεχει τα νεα μας πεδια ( αυτή είναι η διαφορά με την
        // δομή της βάσης της 1ης φάσης)
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Όνομα:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Επώνυμο:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Προσθήκη νέου DBA", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION)
            return;

        String username = usernameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();

        // Αν ο χρήστης παραλείψει να βάλει username εμφαν΄ίζουμε σφάλμα
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Το username είναι υποχρεωτικό!",
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Εδώ ελέγχουμε αν υπάρχει το username στον πίνακα DBA με την χρήση sql query.
        // Αν υπάρχει ήδη, τότε το εμφανίζουμε στον χρήστη και τον προτρέπουμε να
        // επιλέξει
        // άλλο
        try (PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM dba WHERE Username = ?")) {
            checkStmt.setString(1, username);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Το username '" + username + "' υπάρχει ήδη!",
                            "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα βάσης δεδομένων: " + e.getMessage(), "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Προετοιμάζουμε την εισαγωγή των δεδομένων στον πίνακα της βάσης. Ωστόσο
        // κάνουμε και έναν έλεγχο αν έχουν προστεθεί σωστά τα πεδία ,
        // διότι άνθρωποι είμαστε. Αν υπάρχει κάτι κενό ,
        // τότε εμφανίζουμε σφάλμα και δεν εισάγουμε κάτι στον πίνακα
        try (PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO dba (Username, FirstName, LastName, Email, Start_Date) VALUES (?, ?, ?, ?, CURDATE())",
                Statement.RETURN_GENERATED_KEYS)) {

            insertStmt.setString(1, username);
            insertStmt.setString(2, firstName.isEmpty() ? null : firstName);
            insertStmt.setString(3, lastName.isEmpty() ? null : lastName);
            insertStmt.setString(4, email.isEmpty() ? null : email);

            int rowsAffected = insertStmt.executeUpdate();
            if (rowsAffected <= 0) {
                JOptionPane.showMessageDialog(this, "Σφάλμα κατά την προσθήκη του DBA!", "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (ResultSet gk = insertStmt.getGeneratedKeys()) {
                if (gk != null && gk.next())
                    currentDBAId = gk.getInt(1);
            }

            if (currentDBAId == null || currentDBAId == 0) {
                try (PreparedStatement q = conn.prepareStatement("SELECT DBA_ID FROM dba WHERE Username = ? LIMIT 1")) {
                    q.setString(1, username);
                    try (ResultSet r2 = q.executeQuery()) {
                        if (r2.next())
                            currentDBAId = r2.getInt(1);
                    }
                }
            }
            // Εφόσον έχουμε το currentDBAId, μπορούμε να το χρησιμοποιήσουμε τώρα γι' αυτό
            // και το θέτουμε στην μεταβλητή currentDBAUsername που δημιουργήσαμε νωρίτερα
            currentDBAUsername = username;
            setSessionDBA(currentDBAUsername);

            // Επιτυχία προσθήκης και σφάλμα αν υπήρξε πρόβλημα στην ΒΔ
            JOptionPane.showMessageDialog(this, "Ο DBA '" + username + "' προστέθηκε επιτυχώς!", "Επιτυχία",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα βάσης δεδομένων: " + e.getMessage(), "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewAllDBAs() {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT DBA_ID, Username, FirstName, LastName, Email, Start_Date, End_Date FROM dba ORDER BY Username")) {

            StringBuilder dbaList = new StringBuilder();
            dbaList.append("Λίστα όλων των DBA:\n\n");
            dbaList.append(String.format("%-5s %-15s %-15s %-15s %-25s %-12s %-12s\n",
                    "ID", "Username", "Όνομα", "Επώνυμο", "Email", "Start_Date", "End_Date"));
            for (int i = 0; i < 100; i++)
                dbaList.append("─");
            dbaList.append("\n");

            while (rs.next()) {
                dbaList.append(String.format("%-5d %-15s %-15s %-15s %-25s %-12s %-12s\n",
                        rs.getInt("DBA_ID"),
                        rs.getString("Username") != null ? rs.getString("Username") : "",
                        rs.getString("FirstName") != null ? rs.getString("FirstName") : "",
                        rs.getString("LastName") != null ? rs.getString("LastName") : "",
                        rs.getString("Email") != null ? rs.getString("Email") : "",
                        rs.getDate("Start_Date") != null ? rs.getDate("Start_Date").toString() : "",
                        rs.getDate("End_Date") != null ? rs.getDate("End_Date").toString() : "Ενεργός"));
            }

            JTextArea textArea = new JTextArea(dbaList.toString());
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(800, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Λίστα DBA", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα κατά την ανάκτηση των DBA: " + e.getMessage(), "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Διαγραφή ενός DBA από την ΒΔ
    private void deleteDBA() {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT DBA_ID, Username FROM dba WHERE DBA_ID != " + currentDBAId + " ORDER BY Username")) {

            ArrayList<String> dbaOptions = new ArrayList<>();
            java.util.Map<String, Integer> usernameToId = new java.util.HashMap<>();

            while (rs.next()) {
                String username = rs.getString("Username");
                int id = rs.getInt("DBA_ID");
                dbaOptions.add(username + " (ID: " + id + ")");
                usernameToId.put(username + " (ID: " + id + ")", id);
            }

            // αν δεν υπαρχουν αλλοι χρηστες στον πινακα περα απο εμας που ειμαστε logged in
            if (dbaOptions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Δεν υπάρχουν άλλοι DBA για διαγραφή!", "Πληροφορία",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            // Εμφανιση παραθυρου διαλογου ωστε να επιλεξουμε τον χρηστη για διαγραφη
            String selected = (String) JOptionPane.showInputDialog(this,
                    "Επιλέξτε DBA για διαγραφή:\n(Δεν μπορείτε να διαγράψετε τον εαυτό σας)",
                    "Διαγραφή DBA",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    dbaOptions.toArray(),
                    dbaOptions.get(0));

            if (selected != null) {
                int selectedId = usernameToId.get(selected);

                // παραθυρο επιβεβαιωσης
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Είστε σίγουροι ότι θέλετε να διαγράψετε τον DBA: " + selected + "?\n" +
                                "Αυτή η ενέργεια θα ορίσει End_Date στον DBA και δεν μπορεί να αναιρεθεί!",
                        "Επιβεβαίωση Διαγραφής",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                // καλουμε το συγκεκριμενο sql query ωστε να κανουμε
                // ενημερωση του πινακα.
                // για την διαγραφη αυτο που κανουμε ουσιαστικα ειναι να ορισουμε ως end date
                // την τωρινη στιγμη με την συναρτηση NOW()
                // ετσι παυει να ειναι ενεργος ο χρηστης , αρα δεν μπορει να χρησιμοποιηθει
                // ξανα στην ΒΔ
                if (confirm == JOptionPane.YES_OPTION) {
                    try (PreparedStatement updateStmt = conn
                            .prepareStatement("UPDATE dba SET End_Date = CURDATE() WHERE DBA_ID = ?")) {
                        updateStmt.setInt(1, selectedId);
                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Ο DBA απενεργοποιήθηκε επιτυχώς!", "Επιτυχία",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "Σφάλμα κατά την απενεργοποίηση του DBA!", "Σφάλμα",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα βάσης δεδομένων: " + e.getMessage(), "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Simple logging helper (keeps previous behavior)
    private void logDBAAction(String action, String tableName, String details) {
        if (currentDBAId == null || currentDBAUsername == null)
            return;
        try (PreparedStatement ps = conn
                .prepareStatement("INSERT INTO dba_log (ActionDate, dba_username, log_action) VALUES (NOW(), ?, ?)")) {
            ps.setString(1, currentDBAUsername);
            ps.setString(2, action + " on " + tableName + ": " + details);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to log DBA action: " + e.getMessage());
        }
    }

    // Φορτωση ολων των πινακων απο την ΒΔ
    private void loadTables() {
        try {
            if (conn == null)
                return;
            tableSelector.removeAllItems();

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(
                            "SELECT table_name FROM information_schema.tables " +
                                    "WHERE table_schema = 'project2024_gui' AND table_type = 'BASE TABLE' " +
                                    "ORDER BY table_name")) {
                while (rs.next()) {
                    tableSelector.addItem(rs.getString("table_name"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα φόρτωσης πινάκων: " + e.getMessage());
        }
    }

    // Κλήση ολων των διαθεσιμων χώρων (απο την SQL)
    private void showVenuesWithScores() {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT VenueID, VenueName, Capacity, CompletedConcerts, YearsOperating FROM venue WHERE IsAvailable = 1")) {
            try (ResultSet rs = ps.executeQuery()) {
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("VenueID");
                model.addColumn("VenueName");
                model.addColumn("Capacity");
                model.addColumn("CompletedConcerts");
                model.addColumn("YearsOperating");
                model.addColumn("VenueScore");

                while (rs.next()) {
                    int id = rs.getInt("VenueID");
                    String name = rs.getString("VenueName");
                    int cap = rs.getInt("Capacity");
                    int comp = rs.getInt("CompletedConcerts");
                    int years = rs.getInt("YearsOperating");

                    Integer score = null;
                    // εδω αξιοποιουμε το stored procedure που ειχαμε δημιουργησει για την απαιτηση
                    // που αφορα το σκορ των χωρων
                    // το καλουμε και το θετουμε ως venue score και το εμφανιζουμε
                    try (CallableStatement cs = conn.prepareCall("{ CALL CalculateVenueScore(?) }")) {
                        cs.setInt(1, id);
                        boolean has = cs.execute();
                        if (has) {
                            try (ResultSet prs = cs.getResultSet()) {
                                if (prs.next())
                                    score = prs.getInt("VenueScore");
                            }
                        }
                    } catch (SQLException ex) {
                        score = (cap / 1000) * 1 + (comp / 100) * 3 + years * 2;
                    }

                    model.addRow(new Object[] { id, name, cap, comp, years, score });
                }
                // η εμφανιση των παραπανω γινεται σε table γιαυτο και χρησιμοποιουμε το JTable
                JTable vTable = new JTable(model);
                JScrollPane pane = new JScrollPane(vTable);
                vTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                JPanel panel = new JPanel(new BorderLayout());
                panel.add(pane, BorderLayout.CENTER);

                // Νεα οθονη που εμφανιζει στοιχεια συναυλιων
                JPanel btns = new JPanel();
                JButton detailsBtn = new JButton("Προβολή Στοιχείων & Συναυλιών");
                JButton closeBtn = new JButton("Κλείσιμο");
                btns.add(detailsBtn);
                btns.add(closeBtn);
                panel.add(btns, BorderLayout.SOUTH);

                JDialog dialog = new JDialog(this, "Διαθέσιμοι Χώροι & Βαθμολογίες", true);
                dialog.getContentPane().add(panel);
                dialog.setSize(800, 400);
                dialog.setLocationRelativeTo(this);

                detailsBtn.addActionListener(ev -> {
                    int sel = vTable.getSelectedRow();
                    if (sel == -1) { // σε περιπτωση που δεν επιλεγει ενας χωρος για να δουμε τι συναυλιες υπαρχουν
                                     // (απο το ιστορικο συναυλιων τις αντλουμε)
                        JOptionPane.showMessageDialog(dialog, "Επιλέξτε ένα venue πρώτα.", "Προσοχή",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    int venueId = (Integer) vTable.getValueAt(sel, 0);
                    showVenueDetails(venueId);
                });

                closeBtn.addActionListener(ev -> dialog.dispose());

                dialog.setVisible(true);
            } // error handling
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα κατά την ανάκτηση χώρων: " + e.getMessage(), "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBandMembers() {
        try {

            String checkTablesQuery = "SELECT TABLE_NAME FROM information_schema.TABLES " +
                    "WHERE TABLE_SCHEMA = 'project2024_gui' AND TABLE_NAME IN ('band', 'bandmember', 'person')";

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(checkTablesQuery)) {

                java.util.List<String> existingTables = new java.util.ArrayList<>();
                while (rs.next()) {
                    existingTables.add(rs.getString("TABLE_NAME"));
                }

                if (!existingTables.contains("band") || !existingTables.contains("person")) {
                    JOptionPane.showMessageDialog(this,
                            "Οι πίνακες band ή person δεν υπάρχουν στη βάση δεδομένων.",
                            "Σφάλμα",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Query για να κανουμε fetch τα bandmembers
            String membersQuery = "SELECT b.BandName, CONCAT(p.FirstName, ' ', p.LastName) as MemberName, " +
                    "bm.FromDate, bm.ToDate " +
                    "FROM band b " +
                    "INNER JOIN bandmember bm ON b.BandID = bm.BandID " +
                    "INNER JOIN person p ON bm.PersonID = p.PersonID " +
                    "ORDER BY b.BandName, bm.FromDate";

            // εμφανιζουμε τα αποτελεσματα σε table
            DefaultTableModel membersModel = new DefaultTableModel(
                    new String[] { "Συγκρότημα", "Μέλος", "Από", "Έως" }, 0);

            // εισαγουμε τα δεδομενα που αντλησαμε απο τους πινακες
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(membersQuery)) {

                while (rs.next()) {
                    String bandName = rs.getString("BandName");
                    String memberName = rs.getString("MemberName");
                    java.sql.Date fromDate = rs.getDate("FromDate");
                    java.sql.Date toDate = rs.getDate("ToDate");

                    String fromDateStr = fromDate != null ? fromDate.toString() : "";
                    String toDateStr = toDate != null ? toDate.toString() : "Ενεργό μέλος";

                    membersModel.addRow(new Object[] {
                            bandName,
                            memberName,
                            fromDateStr,
                            toDateStr
                    });
                }
            }

            // Δημιουργια JTable για εμφανιση
            JTable membersTable = new JTable(membersModel);

            // σκρολ
            JScrollPane scrollPane = new JScrollPane(membersTable);
            scrollPane.setPreferredSize(new Dimension(800, 400));

            scrollPane.setBorder(BorderFactory.createTitledBorder("Μέλη Συγκροτημάτων"));

            JDialog dialog = new JDialog(this, "Μέλη Συγκροτημάτων", true);
            dialog.getContentPane().add(scrollPane);
            dialog.setSize(900, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά την ανάκτηση των μελών: " + e.getMessage(),
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ΙΔΙΑ ΛΟΓΙΚΗ ΜΕ showBandMembers
    private void showArtistCompanyRelations() {
        try {

            String checkTablesQuery = "SELECT TABLE_NAME FROM information_schema.TABLES " +
                    "WHERE TABLE_SCHEMA = 'project2024_gui' AND TABLE_NAME IN ('artist', 'artistcompany', 'recordcompany')";

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(checkTablesQuery)) {

                java.util.List<String> existingTables = new java.util.ArrayList<>();
                while (rs.next()) {
                    existingTables.add(rs.getString("TABLE_NAME"));
                }

                if (!existingTables.contains("artist") || !existingTables.contains("recordcompany")) {
                    JOptionPane.showMessageDialog(this,
                            "Οι πίνακες artist ή recordcompany δεν υπάρχουν στη βάση δεδομένων.",
                            "Σφάλμα",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String relationshipQuery = "SELECT " +
                    "CASE " +
                    "  WHEN a.ArtistType = 'PERSON' THEN CONCAT(p.FirstName, ' ', p.LastName) " +
                    "  WHEN a.ArtistType = 'BAND' THEN b.BandName " +
                    "  ELSE a.ArtistType " +
                    "END AS ArtistName, " +
                    "rc.CompanyName, ac.FromDate, ac.ToDate " +
                    "FROM artist a " +
                    "INNER JOIN artistcompany ac ON a.ArtistID = ac.ArtistID " +
                    "INNER JOIN recordcompany rc ON ac.CompanyID = rc.CompanyID " +
                    "LEFT JOIN person p ON a.ArtistID = p.ArtistID AND a.ArtistType = 'PERSON' " +
                    "LEFT JOIN band b ON a.ArtistID = b.ArtistID AND a.ArtistType = 'BAND' " +
                    "ORDER BY ArtistName, rc.CompanyName";

            DefaultTableModel relationshipModel = new DefaultTableModel(
                    new String[] { "Καλλιτέχνης", "Εταιρία", "Από", "Έως" }, 0);

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(relationshipQuery)) {

                while (rs.next()) {
                    String artistName = rs.getString("ArtistName");
                    String companyName = rs.getString("CompanyName");
                    java.sql.Date fromDate = rs.getDate("FromDate");
                    java.sql.Date toDate = rs.getDate("ToDate");

                    String fromDateStr = fromDate != null ? fromDate.toString() : "";
                    String toDateStr = toDate != null ? toDate.toString() : "Ενεργή συνεργασία";

                    relationshipModel.addRow(new Object[] {
                            artistName,
                            companyName,
                            fromDateStr,
                            toDateStr
                    });
                }
            }

            JTable relationshipTable = new JTable(relationshipModel);

            JScrollPane scrollPane = new JScrollPane(relationshipTable);
            scrollPane.setPreferredSize(new Dimension(800, 400));

            scrollPane.setBorder(BorderFactory.createTitledBorder("Συνεργασίες Καλλιτεχνών - Δισκογραφικών Εταιριών"));

            JDialog dialog = new JDialog(this, "Συνεργασίες Καλλιτεχνών - Δισκογραφικών Εταιριών", true);
            dialog.getContentPane().add(scrollPane);
            dialog.setSize(900, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά την ανάκτηση των συνεργασιών: " + e.getMessage(),
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ΙΔΙΑ ΛΟΓΙΚΗ ΜΕ showBandMembers
    private void showAlbumArtistRelations() {
        try {
            String check = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'project2024_gui' AND TABLE_NAME IN ('album','artist','person','band','genre')";
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(check)) {
                java.util.Set<String> existing = new java.util.HashSet<>();
                while (rs.next())
                    existing.add(rs.getString("TABLE_NAME"));
                if (!existing.contains("album") || !existing.contains("artist")) {
                    JOptionPane.showMessageDialog(this, "Οι πίνακες album ή artist δεν υπάρχουν.", "Σφάλμα",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String q = "SELECT a.AlbumID, a.Title, " +
                    "CASE WHEN ar.ArtistType='PERSON' THEN CONCAT(p.FirstName,' ',p.LastName) WHEN ar.ArtistType='BAND' THEN b.BandName ELSE ar.ArtistType END AS ArtistName, "
                    +
                    "g.GenreName, rc.CompanyName, CONCAT(pr.FirstName,' ',pr.LastName) AS ProducerName " +
                    "FROM album a " +
                    "INNER JOIN artist ar ON a.ArtistID = ar.ArtistID " +
                    "LEFT JOIN person p ON ar.ArtistID = p.ArtistID AND ar.ArtistType='PERSON' " +
                    "LEFT JOIN band b ON ar.ArtistID = b.ArtistID AND ar.ArtistType='BAND' " +
                    "LEFT JOIN genre g ON a.GenreID = g.GenreID " +
                    "LEFT JOIN recordcompany rc ON a.CompanyID = rc.CompanyID " +
                    "LEFT JOIN producer pr ON a.ProducerID = pr.ProducerID " +
                    "ORDER BY a.Title";

            DefaultTableModel model = new DefaultTableModel(
                    new String[] { "AlbumID", "Title", "Artist", "Genre", "Company", "Producer" }, 0);
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(q)) {
                while (rs.next()) {
                    model.addRow(new Object[] { rs.getInt("AlbumID"), rs.getString("Title"), rs.getString("ArtistName"),
                            rs.getString("GenreName"), rs.getString("CompanyName"), rs.getString("ProducerName") });
                }
            }

            JTable t = new JTable(model);
            JScrollPane sp = new JScrollPane(t);
            sp.setPreferredSize(new Dimension(900, 400));
            sp.setBorder(BorderFactory.createTitledBorder("Συσχετίσεις Album - Καλλιτέχνη"));
            JDialog d = new JDialog(this, "Album ↔ Καλλιτέχνης", true);
            d.getContentPane().add(sp);
            d.setSize(1000, 500);
            d.setLocationRelativeTo(this);
            d.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα ανάκτησης album-artist: " + e.getMessage(), "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ΙΔΙΑ ΛΟΓΙΚΗ ΜΕ showBandMembers
    private void showAlbumReleases() {
        try {
            String check = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'project2024_gui' AND TABLE_NAME IN ('album','albumrelease')";
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(check)) {
                java.util.Set<String> existing = new java.util.HashSet<>();
                while (rs.next())
                    existing.add(rs.getString("TABLE_NAME"));
                if (!existing.contains("album") || !existing.contains("albumrelease")) {
                    JOptionPane.showMessageDialog(this, "Οι πίνακες album ή albumrelease δεν υπάρχουν.", "Σφάλμα",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String q = "SELECT ar.ReleaseID, a.Title AS AlbumTitle, ar.ReleaseDate, ar.ReleaseType, ar.ReleaseStatus, ar.Packaging "
                    +
                    "FROM albumrelease ar INNER JOIN album a ON ar.AlbumID = a.AlbumID ORDER BY a.Title, ar.ReleaseDate";

            DefaultTableModel model = new DefaultTableModel(
                    new String[] { "ReleaseID", "Album", "ReleaseDate", "Type", "Status", "Packaging" }, 0);
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(q)) {
                while (rs.next()) {
                    model.addRow(new Object[] { rs.getInt("ReleaseID"), rs.getString("AlbumTitle"),
                            rs.getDate("ReleaseDate"), rs.getString("ReleaseType"), rs.getString("ReleaseStatus"),
                            rs.getString("Packaging") });
                }
            }

            JTable t = new JTable(model);
            JScrollPane sp = new JScrollPane(t);
            sp.setPreferredSize(new Dimension(900, 400));
            sp.setBorder(BorderFactory.createTitledBorder("Album Releases"));
            JDialog d = new JDialog(this, "Album ↔ Releases", true);
            d.getContentPane().add(sp);
            d.setSize(1000, 500);
            d.setLocationRelativeTo(this);
            d.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα ανάκτησης album-releases: " + e.getMessage(), "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ΙΔΙΑ ΛΟΓΙΚΗ ΜΕ showBandMembers
    private void showAlbumTracks() {
        try {
            String check = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'project2024_gui' AND TABLE_NAME IN ('album','track')";
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(check)) {
                java.util.Set<String> existing = new java.util.HashSet<>();
                while (rs.next())
                    existing.add(rs.getString("TABLE_NAME"));
                if (!existing.contains("album") || !existing.contains("track")) {
                    JOptionPane.showMessageDialog(this, "Οι πίνακες album ή track δεν υπάρχουν.", "Σφάλμα",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String q = "SELECT t.TrackID, a.Title AS AlbumTitle, t.Title AS TrackTitle, t.TrackLength, t.TrackNo " +
                    "FROM track t INNER JOIN album a ON t.AlbumID = a.AlbumID ORDER BY a.Title, t.TrackNo";

            DefaultTableModel model = new DefaultTableModel(
                    new String[] { "TrackID", "Album", "Track Title", "Length", "TrackNo" }, 0);
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(q)) {
                while (rs.next()) {
                    model.addRow(new Object[] { rs.getInt("TrackID"), rs.getString("AlbumTitle"),
                            rs.getString("TrackTitle"), rs.getTime("TrackLength"), rs.getInt("TrackNo") });
                }
            }

            JTable t = new JTable(model);
            JScrollPane sp = new JScrollPane(t);
            sp.setPreferredSize(new Dimension(900, 400));
            sp.setBorder(BorderFactory.createTitledBorder("Album Tracks"));
            JDialog d = new JDialog(this, "Album ↔ Tracks", true);
            d.getContentPane().add(sp);
            d.setSize(1000, 500);
            d.setLocationRelativeTo(this);
            d.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα ανάκτησης album-tracks: " + e.getMessage(), "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ΙΔΙΑ ΛΟΓΙΚΗ ΜΕ showBandMembers
    private void showProducerCompanyRelations() {
        try {
            // Check if tables exist
            String checkTablesQuery = "SELECT TABLE_NAME FROM information_schema.TABLES " +
                    "WHERE TABLE_SCHEMA = 'project2024_gui' AND TABLE_NAME IN ('producer', 'producercompany', 'recordcompany')";

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(checkTablesQuery)) {

                java.util.List<String> existingTables = new java.util.ArrayList<>();
                while (rs.next()) {
                    existingTables.add(rs.getString("TABLE_NAME"));
                }

                if (!existingTables.contains("producer") || !existingTables.contains("recordcompany")) {
                    JOptionPane.showMessageDialog(this,
                            "Οι πίνακες producer ή recordcompany δεν υπάρχουν στη βάση δεδομένων.",
                            "Σφάλμα",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String collaborationQuery = "SELECT p.ProducerID, CONCAT(p.FirstName, ' ', p.LastName) as ProducerName, " +
                    "rc.CompanyName, pc.ToDate " +
                    "FROM producer p " +
                    "INNER JOIN producercompany pc ON p.ProducerID = pc.ProducerID " +
                    "INNER JOIN recordcompany rc ON pc.CompanyID = rc.CompanyID " +
                    "ORDER BY p.LastName, p.FirstName, rc.CompanyName";

            DefaultTableModel collaborationModel = new DefaultTableModel(
                    new String[] { "Παραγωγός", "Εταιρία", "Έως Ημερομηνία" }, 0);

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(collaborationQuery)) {

                while (rs.next()) {
                    String producerName = rs.getString("ProducerName");
                    String companyName = rs.getString("CompanyName");
                    java.sql.Date toDate = rs.getDate("ToDate");
                    String toDateStr = toDate != null ? toDate.toString() : "Ενεργή";

                    collaborationModel.addRow(new Object[] {
                            producerName,
                            companyName,
                            toDateStr
                    });
                }
            }

            JTable collaborationTable = new JTable(collaborationModel);

            JScrollPane scrollPane = new JScrollPane(collaborationTable);
            scrollPane.setPreferredSize(new Dimension(800, 400));

            scrollPane.setBorder(BorderFactory.createTitledBorder("Συνεργασίες Παραγωγών - Δισκογραφικών Εταιριών"));

            JDialog dialog = new JDialog(this, "Συνεργασίες Παραγωγών και Εταιριών", true);
            dialog.getContentPane().add(scrollPane);
            dialog.setSize(900, 500);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά την ανάκτηση των συνεργασιών: " + e.getMessage(),
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showVenueDetails(int venueId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT VenueID, VenueName, Capacity, CompletedConcerts, YearsOperating, IsAvailable FROM venue WHERE VenueID = ?")) {
            ps.setInt(1, venueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Το venue δεν βρέθηκε.", "Πληροφορία",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                String venueName = rs.getString("VenueName");
                int capacity = rs.getInt("Capacity");
                int completed = rs.getInt("CompletedConcerts");
                int years = rs.getInt("YearsOperating");

                Integer score = null;
                try (CallableStatement cs = conn.prepareCall("{ CALL CalculateVenueScore(?) }")) {
                    cs.setInt(1, venueId);
                    boolean has = cs.execute();
                    if (has) {
                        try (ResultSet prs = cs.getResultSet()) {
                            if (prs.next())
                                score = prs.getInt("VenueScore");
                        }
                    }
                } catch (SQLException ex) {
                    score = (capacity / 1000) * 1 + (completed / 100) * 3 + years * 2;
                }

                DefaultTableModel concertsModel = new DefaultTableModel();
                concertsModel.addColumn("ConcertDate");
                concertsModel.addColumn("ArtistName");
                concertsModel.addColumn("Status");
                concertsModel.addColumn("TicketsSold");

                try (CallableStatement cs2 = conn.prepareCall("{ CALL GetConcertDatesByVenue(?) }")) {
                    cs2.setString(1, venueName);
                    boolean has = cs2.execute();
                    if (has) {
                        try (ResultSet crs = cs2.getResultSet()) {
                            while (crs.next()) {
                                Date d = crs.getDate("ConcertDate");
                                String artist = crs.getString("ArtistName");
                                String status = crs.getString("ConcertStatus");
                                int sold = crs.getInt("TicketsSold");
                                concertsModel
                                        .addRow(new Object[] { d != null ? d.toString() : "", artist, status, sold });
                            }
                        }
                    }
                } catch (SQLException ex) {
                    try (PreparedStatement q = conn.prepareStatement(
                            "SELECT c.ConcertDate, CASE WHEN a.ArtistType='PERSON' THEN CONCAT(p.FirstName,' ',p.LastName) WHEN a.ArtistType='BAND' THEN b.BandName END AS ArtistName, c.ConcertStatus, c.TicketsSold "
                                    +
                                    "FROM concert c INNER JOIN venue v ON c.VenueID = v.VenueID INNER JOIN artist a ON c.ArtistID = a.ArtistID "
                                    +
                                    "LEFT JOIN person p ON a.ArtistID = p.ArtistID AND a.ArtistType='PERSON' LEFT JOIN band b ON a.ArtistID = b.ArtistID AND a.ArtistType='BAND' WHERE v.VenueID = ?")) {
                        q.setInt(1, venueId);
                        try (ResultSet crs = q.executeQuery()) {
                            while (crs.next()) {
                                Date d = crs.getDate("ConcertDate");
                                String artist = crs.getString("ArtistName");
                                String status = crs.getString("ConcertStatus");
                                int sold = crs.getInt("TicketsSold");
                                concertsModel
                                        .addRow(new Object[] { d != null ? d.toString() : "", artist, status, sold });
                            }
                        }
                    }
                }

                JPanel info = new JPanel(new BorderLayout());
                JPanel top = new JPanel(new GridLayout(3, 2));
                top.add(new JLabel("Venue Name:"));
                top.add(new JLabel(venueName));
                top.add(new JLabel("Capacity:"));
                top.add(new JLabel(String.valueOf(capacity)));
                top.add(new JLabel("Venue Score:"));
                top.add(new JLabel(String.valueOf(score)));
                info.add(top, BorderLayout.NORTH);

                JTable concertsTable = new JTable(concertsModel);
                info.add(new JScrollPane(concertsTable), BorderLayout.CENTER);

                JDialog dialog = new JDialog(this, "Λεπτομέρειες Χώρου: " + venueName, true);
                dialog.getContentPane().add(info);
                dialog.setSize(800, 400);
                dialog.setLocationRelativeTo(this);
                dialog.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα ανάκτησης στοιχείων venue: " + e.getMessage(), "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTableData(String tableName) {
        try {
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            // Εξάγουμε το πραγματικό όνομα του πίνακα/view από την επιλογή
            String actualTableName = extractTableName(tableName);

            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM " + actualTableName)) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++)
                    tableModel.addColumn(metaData.getColumnName(i));
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= columnCount; i++)
                        row.add(rs.getObject(i));
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα φόρτωσης δεδομένων: " + e.getMessage());
        }
    }

    private String extractTableName(String selection) {
        if (selection == null)
            return null;
        if (selection.contains("("))
            return selection.substring(0, selection.indexOf("(")).trim();
        if (selection.startsWith("---"))
            return "artist";
        return selection;
    }

    private boolean isViewSelection(String selection) {
        return selection.contains("_view") || selection.contains("(");
    }

    private String[] getEnumValues(String tableName, String columnName) {
        try {
            ArrayList<String> values = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt
                            .executeQuery("SHOW COLUMNS FROM " + tableName + " WHERE Field = '" + columnName + "'")) {
                if (rs.next()) {
                    String type = rs.getString("Type").toLowerCase();

                    // Ειδική μεταχείριση για boolean/tinyint(1) πεδία
                    if (type.equals("tinyint(1)")) {
                        values.add("1 - Ναι");
                        values.add("0 - Όχι");
                    }
                    // Κανονική μεταχείριση για enum πεδία
                    else if (type.startsWith("enum(")) {
                        type = type.substring(5, type.length() - 1);
                        String[] splits = type.split(",");
                        for (String s : splits) {
                            String cleanValue = s.trim().replace("'", "");
                            // Μετατροπή boolean τιμών σε Ναι/Όχι
                            if (cleanValue.equalsIgnoreCase("true")) {
                                values.add("1 - Ναι");
                            } else if (cleanValue.equalsIgnoreCase("false")) {
                                values.add("0 - Όχι");
                            } else {
                                values.add(cleanValue);
                            }
                        }
                    }
                }
            }
            return values.toArray(new String[0]);
        } catch (SQLException e) {
            return new String[0];
        }
    }

    private boolean isDateColumn(String tableName, String columnName) {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SHOW COLUMNS FROM " + tableName + " WHERE Field = '" + columnName + "'")) {
            if (rs.next()) {
                String type = rs.getString("Type").toLowerCase();
                return type.contains("date") || type.contains("timestamp");
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private boolean isAutoIncrement(String tableName, String columnName) {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SHOW COLUMNS FROM " + tableName + " WHERE Field = '" + columnName + "'")) {
            if (rs.next()) {
                return "auto_increment".equalsIgnoreCase(rs.getString("Extra"));
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private boolean isNullableColumn(String tableName, String columnName) {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SHOW COLUMNS FROM " + tableName + " WHERE Field = '" + columnName + "'")) {
            if (rs.next()) {
                return "YES".equalsIgnoreCase(rs.getString("Null"));
            }
        } catch (SQLException e) {
            return true;
        }
        return true;
    }

    // Εμφάνιση του πίνακα παραγωγών
    private String[] getProducers() {
        ArrayList<String> producers = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT ProducerID, FirstName, LastName FROM producer ORDER BY LastName, FirstName")) {
            while (rs.next()) {
                producers.add(
                        rs.getInt("ProducerID") + ": " + rs.getString("FirstName") + " " + rs.getString("LastName"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading producers: " + e.getMessage());
        }
        return producers.toArray(new String[0]);
    }

    // Εμφάνιση του πίνακα δισκογραφικών εταιριών
    private String[] getRecordCompanies() {
        ArrayList<String> companies = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT CompanyID, CompanyName FROM recordcompany ORDER BY CompanyName")) {
            while (rs.next()) {
                companies.add(rs.getInt("CompanyID") + ": " + rs.getString("CompanyName"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading record companies: " + e.getMessage());
        }
        return companies.toArray(new String[0]);
    }

    private String extractIdFromSelection(String selection) {
        if (selection == null)
            return null;

        // Για τα boolean πεδία (1 - Ναι, 0 - Όχι)
        if (selection.contains(" - ")) {
            return selection.substring(0, selection.indexOf(" - ")).trim();
        }

        // Για τα foreign key πεδία (ID: Description)
        int colonIdx = selection.indexOf(':');
        if (colonIdx > 0) {
            try {
                return selection.substring(0, colonIdx).trim();
            } catch (Exception e) {
                return null;
            }
        }
        return selection;
    }

    private boolean isForeignKeyField(String tableName, String columnName) {
        try (ResultSet rs = conn.getMetaData().getImportedKeys(null, null, tableName)) {
            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("FKCOLUMN_NAME"))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    private String[] getForeignKeyOptions(String tableName, String columnName) {
        ArrayList<String> options = new ArrayList<>();
        try {
            ResultSet fks = conn.getMetaData().getImportedKeys(null, null, tableName);
            while (fks.next()) {
                if (columnName.equalsIgnoreCase(fks.getString("FKCOLUMN_NAME"))) {
                    String pkTable = fks.getString("PKTABLE_NAME");
                    String pkColumn = fks.getString("PKCOLUMN_NAME");

                    // Special handling for common reference tables
                    if (pkTable.equalsIgnoreCase("producer")) {
                        return getProducers();
                    }
                    if (pkTable.equalsIgnoreCase("recordcompany")) {
                        return getRecordCompanies();
                    }

                    // Default handling: show ID and first VARCHAR column
                    try (Statement stmt = conn.createStatement()) {
                        ResultSet cols = stmt.executeQuery("SHOW COLUMNS FROM " + pkTable
                                + " WHERE Type LIKE '%varchar%' OR Type LIKE '%char%'");
                        String displayCol = pkColumn;
                        if (cols.next()) {
                            displayCol = cols.getString("Field");
                        }

                        try (ResultSet rs = stmt.executeQuery("SELECT " + pkColumn + ", " + displayCol
                                + " FROM " + pkTable + " ORDER BY " + displayCol)) {
                            while (rs.next()) {
                                String id = rs.getString(1);
                                String display = rs.getString(2);
                                if (display == null)
                                    display = id;
                                options.add(id + ": " + display);
                            }
                        }
                    }
                    break;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting FK options: " + e.getMessage());
        }
        return options.toArray(new String[0]);
    }

    // επιλογέας ημερομηνίας με βελάκια
    private String showDatePicker(String title, String currentValue) {
        // αν θελω να εχω μια τιμη null βαζω ενα tickbox που μπορω να το επιλεξω και να
        // κανει assign NULL
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"); // μορφή ημερομηνίας
        dateSpinner.setEditor(dateEditor);

        JCheckBox nullCheck = new JCheckBox("Κενό (NULL)");

        // αν ειναι κενο ή εχει επιλεχθει NULL , τοτε θετω NULL στην ημερομηνια
        if (currentValue == null || currentValue.isEmpty()) {
            nullCheck.setSelected(true);
            dateSpinner.setEnabled(false);
        } else {
            try {
                java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(currentValue);
                dateSpinner.setValue(date);
            } catch (Exception ignored) {
            }
        }

        nullCheck.addActionListener(ev -> dateSpinner.setEnabled(!nullCheck.isSelected()));

        JPanel center = new JPanel(new BorderLayout());
        center.add(dateSpinner, BorderLayout.CENTER);
        center.add(nullCheck, BorderLayout.SOUTH);

        panel.add(new JLabel(title + ":"), BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (nullCheck.isSelected()) {

                return "";
            }
            java.util.Date date = (java.util.Date) dateSpinner.getValue();
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
        }

        return null;
    }

    // εισαγωγη δεδομενων σε πινακα
    private void insertRow() {
        String tableName = (String) tableSelector.getSelectedItem();
        if (tableName == null)
            return;
        if (isViewSelection(tableName)) {
            JOptionPane.showMessageDialog(this, "Δεν μπορείτε να προσθέσετε δεδομένα σε view.", "Μη έγκυρη επιλογή",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String actualTableName = extractTableName(tableName);

        ArrayList<String> reqFields = new ArrayList<>();
        ArrayList<String> dateFields = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM " + actualTableName)) {
            while (rs.next()) {
                String field = rs.getString("Field");
                String type = rs.getString("Type");
                String nullable = rs.getString("Null");
                if (nullable.equals("NO")) {
                    reqFields.add(field);
                }
                if (type.toLowerCase().contains("date")) {
                    dateFields.add(field);
                }
            }
        } catch (SQLException e) {

            System.err.println("Warning: Could not get column metadata: " + e.getMessage());
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + actualTableName + " LIMIT 1")) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            ArrayList<String> columnNames = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();

            for (int i = 1; i <= colCount; i++) {
                String col = meta.getColumnName(i);

                // Προσπέραση AUTO_INCREMENT πεδίων
                if (isAutoIncrement(actualTableName, col)) {
                    continue;
                }

                columnNames.add(col);
                String val = null;

                if (isForeignKeyField(actualTableName, col)) {
                    String[] options = getForeignKeyOptions(actualTableName, col);
                    if (options != null && options.length > 0) {
                        String selected = (String) JOptionPane.showInputDialog(this,
                                "Επιλέξτε τιμή για " + col + ":",
                                "Επιλογή " + col,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[0]);

                        // Αν πατήσει Cancel , στοπ την διαδικασία
                        if (selected == null) {
                            return;
                        }
                        val = extractIdFromSelection(selected);
                    }
                }
                // Επιλογή ημερομηνίας
                else if (isDateColumn(actualTableName, col)) {
                    val = showDatePicker("Επιλέξτε ημερομηνία για " + col, null);
                    // Αν πατήσει Cancel , στοπ την διαδικασία
                    if (val == null && !isNullableColumn(actualTableName, col)) {
                        return;
                    }
                }
                // Επιλογή Enum με dropdown menu
                else {
                    String[] enumVals = getEnumValues(actualTableName, col);
                    if (enumVals != null && enumVals.length > 0) {
                        val = (String) JOptionPane.showInputDialog(this,
                                "Επιλέξτε τιμή για " + col + ":",
                                "Επιλογή " + col,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                enumVals,
                                enumVals[0]);
                    }

                    else {
                        // Κανονικό text input
                        val = JOptionPane.showInputDialog(this, "Τιμή για " + col + ":");
                        // Αν πατήσει Cancel , στοπ την διαδικασία
                        if (val == null && !isNullableColumn(actualTableName, col)) {
                            return;
                        }
                    }
                }

                // Καθάρισμα κενών τιμών
                if (val != null && val.trim().isEmpty()) {
                    val = null;
                }

                // Έλεγχος για υποχρεωτικά πεδία μετά το καθάρισμα
                if (val == null && !isNullableColumn(actualTableName, col)) {
                    JOptionPane.showMessageDialog(this, "Το πεδίο " + col + " είναι υποχρεωτικό!", "Σφάλμα",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                values.add(val);
            }

            if (columnNames.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Δεν υπάρχουν πεδία για εισαγωγή!", "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String columnsList = String.join(",", columnNames);
            String placeholders = String.join(",", Collections.nCopies(values.size(), "?"));
            String sql = "INSERT INTO " + actualTableName + " (" + columnsList + ") VALUES (" + placeholders + ")";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < values.size(); i++) {
                    String v = values.get(i);
                    String columnName = columnNames.get(i);

                    // Get column metadata to handle types properly
                    String columnType = "";
                    try (Statement s = conn.createStatement();
                            ResultSet r = s.executeQuery(
                                    "SHOW COLUMNS FROM " + actualTableName + " WHERE Field = '" + columnName + "'")) {
                        if (r.next()) {
                            columnType = r.getString("Type").toLowerCase();
                        }
                    }

                    if (v == null || v.trim().isEmpty()) {
                        ps.setNull(i + 1, Types.NULL);
                        continue;
                    }

                    // Διαχείριση boolean τιμών (το TINYINT(1) χρησιμοποιείται για boolean στο
                    // MySQL)
                    if (columnType.equals("tinyint(1)")) {

                        // Για 1 -> true , για 0 -> false
                        if (v.contains(" - ")) {
                            v = v.substring(0, v.indexOf(" - ")).trim();
                        }
                        ps.setInt(i + 1, v.equals("1") || v.equalsIgnoreCase("true") ? 1 : 0);
                    } else if (columnType.contains("int")) {
                        // Extract ID from "ID: Description" format if needed
                        if (v.contains(":")) {
                            v = v.substring(0, v.indexOf(":")).trim();
                        }
                        ps.setInt(i + 1, Integer.parseInt(v));
                    }

                    else if (columnType.contains("date")) {
                        ps.setDate(i + 1, java.sql.Date.valueOf(v));
                    }

                    else if (columnType.contains("timestamp")) {
                        ps.setTimestamp(i + 1, java.sql.Timestamp.valueOf(v));
                    }
                    // Διαχείριση δεκαδικών/αριθμητικών τύπων
                    else if (columnType.contains("decimal") || columnType.contains("numeric")) {
                        ps.setBigDecimal(i + 1, new java.math.BigDecimal(v));
                    }
                    // Διαχείριση τύπων float/double
                    else if (columnType.contains("float") || columnType.contains("double")) {
                        ps.setDouble(i + 1, Double.parseDouble(v));
                    }
                    // Default τιμή για όλους τους άλλους τύπους
                    else {
                        ps.setString(i + 1, v);
                    }
                }
                ps.executeUpdate();
                logDBAAction("INSERT", actualTableName, "Inserted values");
                loadTableData(tableName);
                JOptionPane.showMessageDialog(this, "Εισαγωγή επιτυχής!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα εισαγωγής: " + e.getMessage());
        }
    }

    // Ενημέρωση εγγραφής
    private void updateRow() {
        String tableName = (String) tableSelector.getSelectedItem();
        if (tableName == null)
            return;

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Παρακαλώ επιλέξτε μια γραμμή για ενημέρωση.", "Προσοχή",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            // Παιρνουμε πρωτευον κλειδι
            DatabaseMetaData dbMeta = conn.getMetaData();
            ResultSet pkeys = dbMeta.getPrimaryKeys(null, null, tableName);
            if (!pkeys.next()) {
                JOptionPane.showMessageDialog(this, "Ο πίνακας δεν έχει primary key!", "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String pkColumn = pkeys.getString("COLUMN_NAME");
            String pkValue = table.getValueAt(selectedRow, findColumnIndex(pkColumn)).toString();

            ArrayList<String> columnNames = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();

            for (int i = 1; i <= colCount; i++) {
                String col = meta.getColumnName(i);
                if (col.equals(pkColumn))
                    continue;

                Object currentValue = table.getValueAt(selectedRow, i - 1);
                columnNames.add(col);
                String val = null;

                // Διαχείριση foreign κλειδιών
                if (isForeignKeyField(tableName, col)) {
                    String[] options = getForeignKeyOptions(tableName, col);
                    if (options != null && options.length > 0) {
                        String selected = (String) JOptionPane.showInputDialog(this,
                                "Ενημέρωση " + col + ":",
                                "Επεξεργασία " + col,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                findCurrentOption(options, currentValue != null ? currentValue.toString() : null));

                        if (selected != null) {
                            val = extractIdFromSelection(selected);
                        }
                    }
                }
                // Διαχείριση ημερομηνιών
                else if (isDateColumn(tableName, col)) {
                    val = showDatePicker("Ενημέρωση ημερομηνίας για " + col,
                            currentValue != null ? currentValue.toString() : null);
                }
                // Διαχείριση enums με dropdown
                else {
                    String[] enumVals = getEnumValues(tableName, col);
                    if (enumVals != null && enumVals.length > 0) {
                        val = (String) JOptionPane.showInputDialog(this,
                                "Ενημέρωση " + col + ":",
                                "Επεξεργασία " + col,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                enumVals,
                                currentValue);
                    }

                    else {
                        val = JOptionPane.showInputDialog(this,
                                "Ενημέρωση " + col + ":",
                                currentValue != null ? currentValue.toString() : "");
                    }
                }

                if (val == null && !isNullableColumn(tableName, col)) {
                    JOptionPane.showMessageDialog(this, "Το πεδίο " + col + " είναι υποχρεωτικό!", "Σφάλμα",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                values.add(val);
            }

            if (columnNames.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Δεν υπάρχουν πεδία για ενημέρωση!", "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
            for (int i = 0; i < columnNames.size(); i++) {
                if (i > 0)
                    sql.append(", ");
                sql.append(columnNames.get(i)).append(" = ?");
            }
            sql.append(" WHERE ").append(pkColumn).append(" = ?");

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < values.size(); i++) {
                    String v = values.get(i);
                    String columnName = columnNames.get(i);

                    String columnType = "";
                    try (Statement s = conn.createStatement();
                            ResultSet r = s.executeQuery(
                                    "SHOW COLUMNS FROM " + tableName + " WHERE Field = '" + columnName + "'")) {
                        if (r.next()) {
                            columnType = r.getString("Type").toLowerCase();
                        }
                    }

                    if (v == null || v.trim().isEmpty()) {
                        ps.setNull(i + 1, Types.NULL);
                        continue;
                    }

                    // διαχειριση boolean
                    if (columnType.equals("tinyint(1)")) {
                        // 1 -> true, 0 -> false
                        if (v.contains(" - ")) {
                            v = v.substring(0, v.indexOf(" - ")).trim();
                        }
                        ps.setInt(i + 1, v.equals("1") || v.equalsIgnoreCase("true") ? 1 : 0);
                    }
                    // διαχειριση integer
                    else if (columnType.contains("int")) {

                        if (v.contains(":")) {
                            v = v.substring(0, v.indexOf(":")).trim();
                        }
                        ps.setInt(i + 1, Integer.parseInt(v));
                    }
                    // διαχείριση ημερομηνίας
                    else if (columnType.contains("date")) {
                        ps.setDate(i + 1, java.sql.Date.valueOf(v));
                    }
                    // διαχείριση timestamp
                    else if (columnType.contains("timestamp")) {
                        ps.setTimestamp(i + 1, java.sql.Timestamp.valueOf(v));
                    }
                    // διαχείριση decimal/numeric
                    else if (columnType.contains("decimal") || columnType.contains("numeric")) {
                        ps.setBigDecimal(i + 1, new java.math.BigDecimal(v));
                    }
                    // διαχείριση floating point
                    else if (columnType.contains("float") || columnType.contains("double")) {
                        ps.setDouble(i + 1, Double.parseDouble(v));
                    }
                    // Default για άλλους τύπους
                    else {
                        ps.setString(i + 1, v);
                    }
                }

                // διαχείριση του πρωτεύοντος κλειδιού
                String pkColumnType = "";
                try (Statement s = conn.createStatement();
                        ResultSet r = s
                                .executeQuery("SHOW COLUMNS FROM " + tableName + " WHERE Field = '" + pkColumn + "'")) {
                    if (r.next()) {
                        pkColumnType = r.getString("Type").toLowerCase();
                    }
                }

                if (pkColumnType.contains("int")) {
                    ps.setInt(values.size() + 1, Integer.parseInt(pkValue));
                } else {
                    ps.setString(values.size() + 1, pkValue);
                }

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    logDBAAction("UPDATE", tableName, "Updated record with " + pkColumn + "=" + pkValue);
                    loadTableData(tableName);
                    JOptionPane.showMessageDialog(this, "Η ενημέρωση ολοκληρώθηκε επιτυχώς!");
                } else {
                    JOptionPane.showMessageDialog(this, "Δεν έγινε ενημέρωση. Η εγγραφή μπορεί να μην υπάρχει πλέον.",
                            "Προσοχή", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα ενημέρωσης: " + e.getMessage());
        }
    }

    private int findColumnIndex(String columnName) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private String findCurrentOption(String[] options, String currentValue) {
        if (currentValue == null)
            return options[0];

        // Για boolean πεδία (1/0)
        if (currentValue.equals("1") || currentValue.equals("0")) {
            for (String option : options) {
                if (option.startsWith(currentValue + " - ")) {
                    return option;
                }
            }
        }

        // Για foreign keys (id: description)
        for (String option : options) {
            if (option.startsWith(currentValue + ":") || option.equals(currentValue)) {
                return option;
            }
        }
        return options[0];
    }

    // διαγραφη εγγραφης απο πινακα
    private void deleteRow() {
        String tableName = (String) tableSelector.getSelectedItem();
        if (tableName == null)
            return;

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Παρακαλώ επιλέξτε μια γραμμή για διαγραφή.", "Προσοχή",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // παιρνουμε πρωτευον κλειδι πινακα
            DatabaseMetaData dbMeta = conn.getMetaData();
            ResultSet pkeys = dbMeta.getPrimaryKeys(null, null, tableName);
            if (!pkeys.next()) {
                JOptionPane.showMessageDialog(this, "Ο πίνακας δεν έχει primary key!", "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            String pkColumn = pkeys.getString("COLUMN_NAME");

            // παιρνουμε πρωτευον κλειδι απο την γραμμη που μας ενδιαφερει
            int pkColIndex = findColumnIndex(pkColumn);
            if (pkColIndex == -1) {
                JOptionPane.showMessageDialog(this, "Δεν βρέθηκε η στήλη " + pkColumn, "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Object pkValue = table.getValueAt(selectedRow, pkColIndex);
            if (pkValue == null) {
                JOptionPane.showMessageDialog(this, "Η επιλεγμένη γραμμή δεν έχει τιμή primary key!", "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // επιβεβαίωση διαγραφής
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Είστε σίγουροι ότι θέλετε να διαγράψετε την επιλεγμένη εγγραφή;\n" +
                            pkColumn + " = " + pkValue,
                    "Επιβεβαίωση Διαγραφής",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION)
                return;

            // Διαγραφή από τον πίνακα με χρήση sql
            String sql = "DELETE FROM " + tableName + " WHERE " + pkColumn + " = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, pkValue.toString());

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    logDBAAction("DELETE", tableName, "Deleted record with " + pkColumn + "=" + pkValue);
                    loadTableData(tableName);
                    JOptionPane.showMessageDialog(this, "Η διαγραφή ολοκληρώθηκε επιτυχώς!");
                } else {
                    JOptionPane.showMessageDialog(this, "Δεν έγινε διαγραφή. Η εγγραφή μπορεί να μην υπάρχει πλέον.",
                            "Προσοχή", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("foreign key")
                    || e.getMessage().toLowerCase().contains("reference")) {
                JOptionPane.showMessageDialog(this,
                        "Δεν μπορεί να γίνει διαγραφή γιατί η εγγραφή χρησιμοποιείται από άλλους πίνακες!",
                        "Σφάλμα Foreign Key",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Σφάλμα διαγραφής: " + e.getMessage(), "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DatabaseUI().setVisible(true));
    }
}
