//package org.example.service;
//
//import org.example.model.Book;
//
//public interface BookDatabase extends JpaRepository<Book, Long> {
//
//    @Query("SELECT COUNT(b) FROM Book b WHERE b.language = :language")
//    long countBooksByLanguage(@Param("language") String language);
//
//    // Outras consultas podem ser adicionadas aqui conforme necessário
//
//}
//
