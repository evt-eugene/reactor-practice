package com.example.demo;

import com.datastax.oss.driver.api.core.CqlSession;
import com.example.demo.student.books.entity.Book;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

public class StandaloneCassandraApplication {

  private static final Log LOG = LogFactory.getLog(StandaloneCassandraApplication.class);

  public static void main(String[] args) {
    var cqlSession = CqlSession.builder()
        .addContactPoint(InetSocketAddress.createUnresolved("127.0.0.1", 9042))
        .withLocalDatacenter("datacenter1")
        .withKeyspace("library")
        .build();

    try (cqlSession) {
      var template = new CassandraTemplate(cqlSession);
      var insertedBook = template.insert(new Book(UUID.randomUUID(), "The Mysterious Island", 1875));
      LOG.info("Inserted: id=" + insertedBook.getId() + ", title=" + insertedBook.getTitle());

      LOG.info("Books count: " + template.count(Book.class));

      var foundBook = template.selectOne(
          Query.query(
              Criteria.where("id").is(insertedBook.getId())
          ).and(
              Criteria.where("title").is(insertedBook.getTitle())
          ),
          Book.class
      );

      LOG.info("Found: id=" + foundBook.getId() + ", title=" + foundBook.getTitle());

      template.truncate(Book.class);

      LOG.info("Books count after truncation: " + template.count(Book.class));
    }
  }
}
