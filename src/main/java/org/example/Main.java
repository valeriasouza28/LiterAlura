package org.example;

import org.example.model.Book;
import org.example.model.BookInfo;
import org.example.model.ReadingListBook;
import org.example.service.GutendexService;
import org.example.service.ReadingList;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static ReadingList readingList;

    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=master";
        String user = "sa";
        String password = "YourStrongPassword!";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the database!");
            createTables(connection);
            readingList = new ReadingList(connection);

//            Map<String, Long> languageCounts = readingList.countBooksByLanguage();
//            languageCounts.forEach((language, count) -> {
//                System.out.println("Idioma: " + language + ", Quantidade de Livros: " + count);
//            });

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Escolha uma opção:");
                System.out.println("1. Buscar livro por título");
                System.out.println("2. Buscar livro por autor");
                System.out.println("3. Buscar livro por linguagem");
                System.out.println("4. Ver minha lista de leitura");
                System.out.println("5. Buscar autores vivos em um ano específico");
                System.out.println("6. Sair");
                int option = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                if (option == 1) {
                    handleBookSearchByTitle(scanner);
                } else if (option == 2) {
                    handleBookSearchByAuthor(scanner);
                } else if (option == 3) {
                    handleBookSearchByLanguage(scanner);
                } else if (option == 4) {
                    displayReadingList();
                } else if (option == 5) {
                    handleBookSearchByAuthorYearRange(scanner);
                }else if (option == 6) {
                    break;
                } else {
                    System.out.println("Opção inválida. Tente novamente.");
                }
            }

            scanner.close();
            System.out.println("Aplicação encerrada.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Connection connection) {
        String createBooksTableSQL = """
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='Books' and xtype='U')
            CREATE TABLE Books (
                id INT PRIMARY KEY IDENTITY(1,1),
                title NVARCHAR(100),
                author NVARCHAR(100),
                published_year INT
            )
        """;

        String createReadingListTableSQL = """
            IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='ReadingList' and xtype='U')
            CREATE TABLE ReadingList (
                id INT PRIMARY KEY IDENTITY(1,1),
                title NVARCHAR(100),
                author NVARCHAR(100),
                year NVARCHAR(10),
                summary NVARCHAR(1000),
                status NVARCHAR(50)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createBooksTableSQL);
            stmt.execute(createReadingListTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void handleBookSearchByTitle(Scanner scanner) {
        System.out.println("Digite o título do livro:");
        String title = scanner.nextLine();
        List<Book> books = GutendexService.searchBooksByTitle(title);
        displaySearchResults(books, scanner);
    }

    private static void handleBookSearchByAuthor(Scanner scanner) {
        System.out.println("Digite o nome do autor:");
        String author = scanner.nextLine();
        List<Book> books = GutendexService.searchBooksByAuthor(author);
        displaySearchResults(books, scanner);
    }
    private static void handleBookSearchByAuthorYearRange(Scanner scanner) {
        System.out.println("Digite o ano inicial:");
        int startYear = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.println("Digite o ano final:");
        int endYear = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        List<Book> books = GutendexService.searchBooksByAuthorYearRange(startYear, endYear);
        displaySearchResults(books, scanner);
    }
//    private static void handleBookSearchByAuthorYearRange(Scanner scanner) {
//        System.out.println("Digite o ano inicial:");
//        int startYear = scanner.nextInt();
//        scanner.nextLine();  // Consume newline
//        System.out.println("Digite o ano final:");
//        int endYear = scanner.nextInt();
//        scanner.nextLine();  // Consume newline
//
//        List<Book> books = GutendexService.searchBooksByAuthorYearRange(startYear, endYear);
//        displaySearchResults(books, scanner);
//    }

    private static void displaySearchResults(List<Book> books, Scanner scanner) {
        IntStream.range(0, books.size()).forEach(i -> {
            Book book = books.get(i);
            System.out.println((i + 1) + ". " + book.title() + " por " + book.author());
        });

        System.out.println("Escolha o número de um livro para adicionar à lista de leitura ou 0 para voltar:");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (choice > 0 && choice <= books.size()) {
            Book selectedBook = books.get(choice - 1);
            System.out.println("Digite o status do livro (ex: lido, lendo, planeja ler):");
            String status = scanner.nextLine();
            readingList.addBook(selectedBook, status);
            System.out.println("Livro adicionado à lista de leitura: " + selectedBook.title() + " por " + selectedBook.author());
        }
    }

    private static void displayReadingList() {
        List<ReadingListBook> readingListBooks = readingList.getBooks();
        for (ReadingListBook book : readingListBooks) {
            System.out.println("Título: " + book.title() + ", Autor: " + book.author() + ", Ano: " + book.year() + ", Status: " + book.status());
        }
    }

    private static void handleBookSearchByLanguage(Scanner scanner) {
        System.out.println("Digite o idioma do livro (ex: en, fr, de):");
        String language = scanner.nextLine();

        // Chama o serviço para obter a lista de BookInfo
        List<BookInfo> bookInfos = GutendexService.searchBooksByLanguage(language);

        // Exibe os resultados
        System.out.println("Resultados da pesquisa por linguagem '" + language + "':");
        for (int i = 0; i < bookInfos.size(); i++) {
            BookInfo bookInfo = bookInfos.get(i);
            System.out.println((i + 1) + ". Título: " + bookInfo.title() + ", Autor: " + bookInfo.author());
        }
    }
}
