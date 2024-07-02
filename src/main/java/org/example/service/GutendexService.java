package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.model.Book;
import org.example.model.BookInfo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GutendexService {
    private static final String SEARCH_API_URL = "https://gutendex.com/books/?search=";
    private static final String SEARCH_LANGUAGE_API_URL = "https://gutendex.com/books/?languages=";
    private static final String SEARCH_AUTHOR_YEAR_API_URL = "https://gutendex.com/books/?author_year_start=%d&author_year_end=%d";



//    public static List<Book> searchBooksByTitle(String title) {
//        return searchBooks(title);
//    }
//
//    public static List<Book> searchBooksByAuthor(String author) {
//        return searchBooks(author);
//    }
//
//    public static List<Book> searchBooksByLanguage(String language) {
//        return searchBooks(language);
//    }
//
//    public static List<Book> searchBooksByAuthorYearRange(int startYear, int endYear) {
//        return searchBooks(String.format(SEARCH_AUTHOR_YEAR_API_URL, startYear, endYear));
//    }

    public static List<Book> searchBooksByTitle(String title) {
        return searchBooks(SEARCH_API_URL + URLEncoder.encode(title, StandardCharsets.UTF_8));
    }

    public static List<Book> searchBooksByAuthor(String author) {
        return searchBooks(SEARCH_API_URL + URLEncoder.encode(author, StandardCharsets.UTF_8));
    }
    public static List<Book> searchBooksByAuthorYearRange(int startYear, int endYear) {
        return searchBooks(String.format(SEARCH_AUTHOR_YEAR_API_URL, startYear, endYear));
    }


    public static List<BookInfo> searchBooksByLanguage(String language) {
        String encodedLanguage = URLEncoder.encode(language, StandardCharsets.UTF_8);
        String apiUrl = SEARCH_LANGUAGE_API_URL + encodedLanguage;

        try {
            String searchResponse = HttpClientUtil.sendRequest(apiUrl);
            JsonNode searchRoot = JsonUtil.parse(searchResponse);
            JsonNode booksArray = searchRoot.get("results");

            return StreamSupport.stream(booksArray.spliterator(), false)
                    .map(GutendexService::mapDocToBookInfo)
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.err.println("Erro ao buscar livros por linguagem: " + e.getMessage());
            return List.of(); // Retorna uma lista vazia em caso de erro
        }
    }



    private static List<Book> searchBooks(String url) {
        try {
            String searchResponse = HttpClientUtil.sendRequest(url);

            JsonNode searchRoot = JsonUtil.parse(searchResponse);
            JsonNode booksArray = searchRoot.get("results");

            return StreamSupport.stream(booksArray.spliterator(), false)
                    .map(GutendexService::mapDocToBook)
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.err.println("Erro ao buscar livros: " + e.getMessage());
            return List.of();
        }
    }
        private static BookInfo mapDocToBookInfo(JsonNode doc) {
            String title = doc.has("title") ? doc.get("title").asText() : "Título não encontrado";
            String author = doc.has("authors") && doc.get("authors").isArray() && doc.get("authors").size() > 0
                    ? doc.get("authors").get(0).get("name").asText() : "Autor não encontrado";

            return new BookInfo(title, author);
        }


        private static Book mapDocToBook(JsonNode doc) {
            String title = doc.has("title") ? doc.get("title").asText() : "Título não encontrado";
            String author = doc.has("authors") && doc.get("authors").isArray() && doc.get("authors").size() > 0
                    ? doc.get("authors").get(0).get("name").asText() : "Autor não encontrado";
            String year = doc.has("download_count") ? doc.get("download_count").asText() : "Ano não encontrado";
            String summary = "Resumo não disponível";

            String language = doc.has("languages") && doc.get("languages").isArray() && doc.get("languages").size() > 0
                    ? doc.get("languages").get(0).asText() : "Idioma não disponível";


            return new Book(title, author, year, summary, language);
        }

//    private static List<Book> searchBooks(String query) {
//        try {
//            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
//            String searchUrl = SEARCH_LANGUAGE_API_URL + encodedQuery;
//            String searchResponse = HttpClientUtil.sendRequest(searchUrl);
//
//            JsonNode searchRoot = JsonUtil.parse(searchResponse);
//            JsonNode booksArray = searchRoot.get("results");
//
//            return StreamSupport.stream(booksArray.spliterator(), false)
//                    .map(GutendexService::mapDocToBook)
//                    .collect(Collectors.toList());
//        } catch (IOException | InterruptedException | URISyntaxException e) {
//            System.err.println("Erro ao buscar livros: " + e.getMessage());
//            return List.of();
//        }
//    }

//    private static List<BookInfo> searchBooksLanguage(String query) {
//        try {
//            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
//            String searchUrl = SEARCH_LANGUAGE_API_URL + encodedQuery;
//            String searchResponse = HttpClientUtil.sendRequest(searchUrl);
//
//            JsonNode searchRoot = JsonUtil.parse(searchResponse);
//            JsonNode booksArray = searchRoot.get("results");
//
//            return StreamSupport.stream(booksArray.spliterator(), false)
//                    .map(GutendexService::mapDocToBook)
//                    .collect(Collectors.toList());
//        } catch (IOException | InterruptedException | URISyntaxException e) {
//            System.err.println("Erro ao buscar livros: " + e.getMessage());
//            return List.of();
//        }
//    }

//    private static List<Book> searchBooksAuthorYearRange(String url) {
//        try {
//            String searchResponse = HttpClientUtil.sendRequest(url);
//
//            JsonNode searchRoot = JsonUtil.parse(searchResponse);
//            JsonNode booksArray = searchRoot.get("results");
//
//            return StreamSupport.stream(booksArray.spliterator(), false)
//                    .map(GutendexService::mapDocToBook)
//                    .collect(Collectors.toList());
//        } catch (IOException | InterruptedException | URISyntaxException e) {
//            System.err.println("Erro ao buscar livros: " + e.getMessage());
//            return List.of();
//        }
//    }


}
