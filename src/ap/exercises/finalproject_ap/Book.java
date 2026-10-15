package ap.exercises.finalproject_ap;

import java.io.Serializable;

// Book.java
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bookId;
    private String title;
    private String author;
    private int publishYear;
    private Employee bookRegisterer;

    public Book(Employee bookRegisterer, String title, String author, int publishYear, String bookId) {
        this.bookRegisterer = bookRegisterer;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public String getBookId() {
        return bookId;
    }

    public Employee getBookRegisterer() {
        return bookRegisterer;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    @Override
    public String toString() {
        return " BookId: " + bookId +
                " | Title: " + title +
                " | Author Name: " + author +
                " | Publish Year: " + publishYear;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Book book = (Book) obj;
        return this.bookId.equals(book.bookId)
                && this.title.equalsIgnoreCase(book.title)
                && this.author.equalsIgnoreCase(book.author)
                && this.publishYear == book.publishYear;
    }

    @Override
    public int hashCode() {
        int result = bookId != null ? bookId.hashCode() : 0;
        result = 31 * result + (title != null ? title.toLowerCase().hashCode() : 0);
        result = 31 * result + (author != null ? author.toLowerCase().hashCode() : 0);
        result = 31 * result + publishYear;
        return result;
    }
}