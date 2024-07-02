package org.example.service;

import org.example.model.Book;
import org.example.model.ReadingListBook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadingList {
    private final List<ReadingListBook> books = new ArrayList<>();
    private final Connection connection;

    public ReadingList(Connection connection) {
        this.connection = connection;
        loadReadingListFromDatabase();
    }

    public void addBook(Book book, String status) {
        ReadingListBook readingListBook = new ReadingListBook(book.title(), book.author(), book.year(), book.summary(), status);
        books.add(readingListBook);
        saveBookToDatabase(readingListBook);
    }

    public List<ReadingListBook> getBooks() {
        return books;
    }

//    public Map<String, Long> countBooksByLanguage() {
//        Map<String, Long> languageCounts = new HashMap<>();
//        for (ReadingListBook readingListBook : books) {
//            String language = readingListBook.language();
//            languageCounts.put(language, languageCounts.getOrDefault(language, 0L) + 1);
//        }
//        return languageCounts;
//    }

    private void loadReadingListFromDatabase() {
        String selectSQL = "SELECT * FROM ReadingList";
        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String year = resultSet.getString("year");
                String summary = resultSet.getString("summary");
                String status = resultSet.getString("status");
                books.add(new ReadingListBook(title, author, year, summary, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveBookToDatabase(ReadingListBook book) {
        String insertSQL = "INSERT INTO ReadingList (title, author, year, summary, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, book.title());
            preparedStatement.setString(2, book.author());
            preparedStatement.setString(3, book.year());
            preparedStatement.setString(4, book.summary());
            preparedStatement.setString(5, book.status());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
