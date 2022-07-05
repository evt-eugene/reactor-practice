package com.example.demo.standalone;

import com.datastax.oss.driver.api.core.CqlSession;
import com.example.demo.student.books.entity.Book;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;

import java.net.InetSocketAddress;
import java.util.UUID;

public class StandaloneCassandraTemplateApplication {

  private static final Log logger = LogFactory.getLog(StandaloneCassandraTemplateApplication.class);

  public static void main(String[] args) {
    try (var cqlSession = createSession()) {
      var template = new CassandraTemplate(cqlSession);

      var insertedBook = insertNewBook(template);
      countBooks(template);
      findInsertedBook(template, insertedBook);
      truncateBooks(template);
    }
  }

  private static CqlSession createSession() {
    return CqlSession.builder()
        .addContactPoint(InetSocketAddress.createUnresolved("127.0.0.1", 9042))
        .withLocalDatacenter("datacenter1")
        .withKeyspace("library")
        .build();
  }

  private static Book insertNewBook(CassandraTemplate template) {
    var book = new Book(UUID.randomUUID(), "The Mysterious Island", 1875);

    var insertedBook = template.insert(book);

    logger.info("Inserted: id=" + insertedBook.getId() + ", title=" + insertedBook.getTitle());

    return insertedBook;
  }

  private static void countBooks(CassandraTemplate template) {
    var count = template.count(Book.class);

    logger.info("Books count: " + count);
  }

  private static void findInsertedBook(CassandraTemplate template, Book book) {
    var foundBook = template.selectOne(
        Query.query(Criteria.where("id").is(book.getId()))
            .and(Criteria.where("title").is(book.getTitle())),
        Book.class
    );

    logger.info("Found: id=" + foundBook.getId() + ", title=" + foundBook.getTitle());
  }

  private static void truncateBooks(CassandraTemplate template) {
    template.truncate(Book.class);

    countBooks(template);
  }
}
