package ap.exercises.finalproject_ap;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Persists the whole library state into a local SQLite database (library.db).
 * Every save replaces the tables with the current in-memory state inside one
 * transaction; every load rebuilds the managers so that all entities (books,
 * students, employees) are shared by reference exactly like at runtime.
 */
public class Persistence implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String DB_FILE = "library.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

    public void save(LibrarySystem librarySystem) {
        try (Connection conn = openConnection()) {
            ensureSchema(conn);
            conn.setAutoCommit(false);
            try {
                clearTables(conn);
                saveEmployees(conn, librarySystem.getEmployeeManager().getEmployees());
                saveStudents(conn, librarySystem.getStudentManager());
                saveBooks(conn, librarySystem.getBookManager().getBooks());
                saveLoans(conn, librarySystem.getLoanBookManager().getLoans());
                saveRequests(conn, librarySystem.getLoanRequestManager().getRequests());
                conn.commit();
                System.out.println("Saving completed.");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Saving failed: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite driver not found on the classpath. Add lib/sqlite-jdbc-3.53.4.0.jar to the run configuration.");
        } catch (SQLException e) {
            System.out.println("Saving failed: " + e.getMessage());
        }
    }

    public LibrarySystem load() {
        if (!new File(DB_FILE).exists()) {
            return new LibrarySystem();
        }
        try (Connection conn = openConnection()) {
            ensureSchema(conn);

            Map<String, Employee> employees = loadEmployees(conn);
            List<Student> blockedStudents = new ArrayList<>();
            Map<String, Student> students = loadStudents(conn, blockedStudents);
            Map<String, Book> books = loadBooks(conn, employees);
            List<LoanBook> loans = loadLoans(conn, students, books, employees);
            List<LoanRequest> requests = loadRequests(conn, students, books);

            StudentManager studentManager = new StudentManager();
            studentManager.restore(new ArrayList<>(students.values()), blockedStudents);
            EmployeeManager employeeManager = new EmployeeManager();
            employeeManager.restore(new ArrayList<>(employees.values()));
            LoanBookManager loanBookManager = new LoanBookManager();
            loanBookManager.restore(loans);
            BookManager bookManager = new BookManager(loanBookManager);
            bookManager.restore(new ArrayList<>(books.values()));
            LoanRequestManager loanRequestManager = new LoanRequestManager();
            loanRequestManager.restore(requests);

            System.out.println("Loading completed.");
            return new LibrarySystem(studentManager, employeeManager, bookManager, loanBookManager, loanRequestManager);
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite driver not found on the classpath. Add lib/sqlite-jdbc-3.53.4.0.jar to the run configuration.");
            return new LibrarySystem();
        } catch (Exception e) {
            System.out.println("Saved data could not be loaded (corrupted database or incompatible version). Starting with a fresh system.");
            return new LibrarySystem();
        }
    }

    private Connection openConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    private void ensureSchema(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS employees (" +
                    "employee_id TEXT PRIMARY KEY, name TEXT NOT NULL, " +
                    "username TEXT NOT NULL, password_hash TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "student_id TEXT PRIMARY KEY, name TEXT NOT NULL, username TEXT NOT NULL, " +
                    "password_hash TEXT NOT NULL, blocked INTEGER NOT NULL DEFAULT 0)");
            st.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "book_id TEXT PRIMARY KEY, title TEXT NOT NULL, author TEXT NOT NULL, " +
                    "publish_year INTEGER NOT NULL, registerer_id TEXT NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS loans (" +
                    "loan_id INTEGER PRIMARY KEY AUTOINCREMENT, student_id TEXT NOT NULL, " +
                    "book_id TEXT NOT NULL, giver_id TEXT NOT NULL, getter_id TEXT, " +
                    "start_date TEXT NOT NULL, end_date TEXT NOT NULL, return_date TEXT)");
            st.execute("CREATE TABLE IF NOT EXISTS loan_requests (" +
                    "request_id INTEGER PRIMARY KEY AUTOINCREMENT, student_id TEXT NOT NULL, " +
                    "book_id TEXT NOT NULL, start_date TEXT NOT NULL, end_date TEXT NOT NULL)");
        }
    }

    private void clearTables(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM loans");
            st.executeUpdate("DELETE FROM loan_requests");
            st.executeUpdate("DELETE FROM books");
            st.executeUpdate("DELETE FROM students");
            st.executeUpdate("DELETE FROM employees");
        }
    }

    private void saveEmployees(Connection conn, List<Employee> employees) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO employees (employee_id, name, username, password_hash) VALUES (?, ?, ?, ?)")) {
            for (Employee e : employees) {
                ps.setString(1, e.getUserId());
                ps.setString(2, e.getName());
                ps.setString(3, e.getUsername());
                ps.setString(4, e.getPasswordHash());
                ps.executeUpdate();
            }
        }
    }

    private void saveStudents(Connection conn, StudentManager studentManager) throws SQLException {
        List<Student> blackList = studentManager.getBlackList();
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO students (student_id, name, username, password_hash, blocked) VALUES (?, ?, ?, ?, ?)")) {
            for (Student s : studentManager.getStudents()) {
                ps.setString(1, s.getUserId());
                ps.setString(2, s.getName());
                ps.setString(3, s.getUsername());
                ps.setString(4, s.getPasswordHash());
                ps.setInt(5, blackList.contains(s) ? 1 : 0);
                ps.executeUpdate();
            }
        }
    }

    private void saveBooks(Connection conn, List<Book> books) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO books (book_id, title, author, publish_year, registerer_id) VALUES (?, ?, ?, ?, ?)")) {
            for (Book b : books) {
                ps.setString(1, b.getBookId());
                ps.setString(2, b.getTitle());
                ps.setString(3, b.getAuthor());
                ps.setInt(4, b.getPublishYear());
                ps.setString(5, b.getBookRegisterer() != null ? b.getBookRegisterer().getUserId() : null);
                ps.executeUpdate();
            }
        }
    }

    private void saveLoans(Connection conn, List<LoanBook> loans) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO loans (student_id, book_id, giver_id, getter_id, start_date, end_date, return_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            for (LoanBook l : loans) {
                ps.setString(1, l.getStudent().getUserId());
                ps.setString(2, l.getBook().getBookId());
                ps.setString(3, l.getGiverEmployee().getUserId());
                ps.setString(4, l.getGetterEmployee() != null ? l.getGetterEmployee().getUserId() : null);
                ps.setString(5, l.getStartDate().toString());
                ps.setString(6, l.getEndDate().toString());
                ps.setString(7, l.getReturnDate() != null ? l.getReturnDate().toString() : null);
                ps.executeUpdate();
            }
        }
    }

    private void saveRequests(Connection conn, List<LoanRequest> requests) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO loan_requests (student_id, book_id, start_date, end_date) VALUES (?, ?, ?, ?)")) {
            for (LoanRequest r : requests) {
                ps.setString(1, r.getStudent().getUserId());
                ps.setString(2, r.getBook().getBookId());
                ps.setString(3, r.getStartDate().toString());
                ps.setString(4, r.getEndDate().toString());
                ps.executeUpdate();
            }
        }
    }

    private Map<String, Employee> loadEmployees(Connection conn) throws SQLException {
        Map<String, Employee> employees = new LinkedHashMap<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT employee_id, name, username, password_hash FROM employees")) {
            while (rs.next()) {
                Employee e = new Employee(rs.getString("name"), rs.getString("employee_id"),
                        rs.getString("username"), rs.getString("password_hash"), true);
                employees.put(e.getUserId(), e);
            }
        }
        return employees;
    }

    private Map<String, Student> loadStudents(Connection conn, List<Student> blockedStudents) throws SQLException {
        Map<String, Student> students = new LinkedHashMap<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT student_id, name, username, password_hash, blocked FROM students")) {
            while (rs.next()) {
                Student s = new Student(rs.getString("name"), rs.getString("student_id"),
                        rs.getString("username"), rs.getString("password_hash"), true);
                students.put(s.getUserId(), s);
                if (rs.getInt("blocked") == 1) {
                    blockedStudents.add(s);
                }
            }
        }
        return students;
    }

    private Map<String, Book> loadBooks(Connection conn, Map<String, Employee> employees) throws SQLException {
        Map<String, Book> books = new LinkedHashMap<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT book_id, title, author, publish_year, registerer_id FROM books")) {
            while (rs.next()) {
                Book b = new Book(employees.get(rs.getString("registerer_id")), rs.getString("title"),
                        rs.getString("author"), rs.getInt("publish_year"), rs.getString("book_id"));
                books.put(b.getBookId(), b);
            }
        }
        return books;
    }

    private List<LoanBook> loadLoans(Connection conn, Map<String, Student> students, Map<String, Book> books,
                                     Map<String, Employee> employees) throws SQLException {
        List<LoanBook> loans = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT student_id, book_id, giver_id, getter_id, " +
                     "start_date, end_date, return_date FROM loans ORDER BY loan_id")) {
            while (rs.next()) {
                String getterId = rs.getString("getter_id");
                String returnDateText = rs.getString("return_date");
                loans.add(new LoanBook(
                        students.get(rs.getString("student_id")),
                        books.get(rs.getString("book_id")),
                        employees.get(rs.getString("giver_id")),
                        getterId != null ? employees.get(getterId) : null,
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date")),
                        returnDateText != null ? LocalDate.parse(returnDateText) : null));
            }
        }
        return loans;
    }

    private List<LoanRequest> loadRequests(Connection conn, Map<String, Student> students,
                                           Map<String, Book> books) throws SQLException {
        List<LoanRequest> requests = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT student_id, book_id, start_date, end_date " +
                     "FROM loan_requests ORDER BY request_id")) {
            while (rs.next()) {
                requests.add(new LoanRequest(
                        students.get(rs.getString("student_id")),
                        books.get(rs.getString("book_id")),
                        LocalDate.parse(rs.getString("start_date")),
                        LocalDate.parse(rs.getString("end_date"))));
            }
        }
        return requests;
    }
}
