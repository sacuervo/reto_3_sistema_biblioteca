package me.santiagocuervo;

public interface BookRepository {

    void saveBook(Book book);

    Book findById(String id);

}
