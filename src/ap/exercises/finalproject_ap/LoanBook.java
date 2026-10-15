package ap.exercises.finalproject_ap;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LoanBook implements Serializable, Loanable {

    private static final long serialVersionUID = 1L;

    private Student student;
    private Book book;
    private Employee giverEmployee;
    private Employee getterEmployee;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate returnDate;


    public LoanBook(LoanRequest loanRequest, Employee giverEmployee) {
        this.student = loanRequest.getStudent();
        this.book = loanRequest.getBook();
        this.giverEmployee = giverEmployee;
        this.startDate = loanRequest.getStartDate();
        this.endDate = loanRequest.getEndDate();
    }

    // Restores a loan saved in the database with its full state.
    LoanBook(Student student, Book book, Employee giverEmployee, Employee getterEmployee,
             LocalDate startDate, LocalDate endDate, LocalDate returnDate) {
        this.student = student;
        this.book = book;
        this.giverEmployee = giverEmployee;
        this.getterEmployee = getterEmployee;
        this.startDate = startDate;
        this.endDate = endDate;
        this.returnDate = returnDate;
    }

    public void setGetterEmployee(Employee getterEmployee) {
        this.getterEmployee = getterEmployee;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Employee getGiverEmployee() {
        return giverEmployee;
    }

    public Employee getGetterEmployee() {
        return getterEmployee;
    }

    public Student getStudent() {
        return student;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    @Override
    public boolean isReturned() {
        return returnDate != null;
    }

    @Override
    public boolean isDelayedInReturning() {
        if (endDate == null) {
            return false;
        }
        LocalDate effectiveReturnDate = (returnDate != null) ? returnDate : LocalDate.now();
        return effectiveReturnDate.isAfter(endDate);
    }

    @Override
    public int loanDuration() {
        if (startDate == null || returnDate == null) {
            return 0;
        }
        long duration = ChronoUnit.DAYS.between(startDate, returnDate);
        return (int) Math.max(duration, 0);
    }

    @Override
    public int delayDuration() {
        if (endDate == null) {
            return 0;
        }
        LocalDate effectiveReturnDate = (returnDate != null) ? returnDate : LocalDate.now();
        long delay = ChronoUnit.DAYS.between(endDate, effectiveReturnDate);
        return (int) Math.max(delay, 0);
    }
}
