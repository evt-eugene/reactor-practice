package com.example.demo.standalone;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.example.demo.student.books.entity.Book;
import com.example.demo.student.janitors.entity.Janitor;
import com.example.demo.student.janitors.entity.Responsibility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.data.cassandra.core.query.Update;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.UUID;

public class StandaloneCassandraTemplateApplication {

  private static final Log logger = LogFactory.getLog(StandaloneCassandraTemplateApplication.class);

  public static void main(String[] args) {
    try (var cqlSession = createSession()) {
      var template = new CassandraTemplate(cqlSession);

      var insertedBook = insertNewBook(template);
      countBooks(template);
      findInsertedBook(template, insertedBook);
      updateBooksByCriteria(template, insertedBook);
      truncateBooks(template);

      performBatch(template);
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

  private static void updateBooksByCriteria(CassandraTemplate template, Book book) {
    boolean applied = template.update(
        Query.query(Criteria.where("id").is(book.getId())),
        Update.empty().set("publishing_year", 1950),
        Book.class
    );

    if (applied) {
      logger.info("Update applied");
    } else {
      logger.info("Update did not apply");
    }
  }

  private static void truncateBooks(CassandraTemplate template) {
    template.truncate(Book.class);

    countBooks(template);
  }

  private static void performBatch(CassandraTemplate template) {
    var writeResult = template.batchOps(BatchType.LOGGED)
        .insert(new Book(UUID.randomUUID(), "The Mysterious Island", 1875))
        .insert(new Janitor(UUID.randomUUID(), "Mr. Fantastic", new Responsibility("General cleaning", Collections.emptyList())))
        .execute();

    if (writeResult.wasApplied()) {
      logger.info("Batch applied");
    } else {
      logger.info("Batch did not apply");
    }
  }
}
