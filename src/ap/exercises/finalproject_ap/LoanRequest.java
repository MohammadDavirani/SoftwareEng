package ap.exercises.finalproject_ap;

import java.io.Serializable;
import java.time.LocalDate;

public class LoanRequest implements Serializable {
    private Student student;
    private Book book;
    private LocalDate startDate;
    private LocalDate endDate;

    public LoanRequest(Student student, Book book, LocalDate startDate, LocalDate endDate) {
        this.student = student;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
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



    @Override
    public String toString() {
        return " Student -> " + student
                + " | Book -> " + book
                + " | StartDate: " + startDate
                + " | EndDate: " + endDate;
    }

}
