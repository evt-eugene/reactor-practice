package com.example.demo.standalone;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.OptionsMap;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.internal.core.loadbalancing.DcInferringLoadBalancingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class StandaloneCassandraDriverApplication {

  private static final Logger logger = LoggerFactory.getLogger(StandaloneCassandraDriverApplication.class);

  public static void main(String[] args) {
    try (var cqlSession = createSession()) {
      var mysteriousIslandID = insertNewBookBySimpleStatement(cqlSession);
      var twentyLeaguesID = insertNewBookByPreparedStatement(cqlSession);

      printSelectionByIdWithPreparedStatement(cqlSession, mysteriousIslandID);
      printSelectionByIdWithQueryBuilder(cqlSession, twentyLeaguesID);

      printCountWithQueryBuilder(cqlSession);
      truncateBooks(cqlSession);
    }
  }

  private static CqlSession createSession() {
    var map = OptionsMap.driverDefaults();
    map.put(TypedDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(5));
    map.put(TypedDriverOption.CONTACT_POINTS, List.of("127.0.0.1:9042"));
    map.put(TypedDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, "datacenter1");
    map.put(TypedDriverOption.SESSION_KEYSPACE, "library");
    map.put(TypedDriverOption.LOAD_BALANCING_POLICY_CLASS, DcInferringLoadBalancingPolicy.class.getCanonicalName());

    var loader = DriverConfigLoader.fromMap(map);

    return CqlSession.builder()
        .withConfigLoader(loader)
        .build();
  }

  private static UUID insertNewBookBySimpleStatement(CqlSession cqlSession) {
    var mysteriousIslandID = UUID.randomUUID();

    var ss = SimpleStatement.newInstance("INSERT INTO library.books (id, title, publishing_year) VALUES (?, ?, ?)",
        mysteriousIslandID, "The Mysterious Island", 1875);

    var rs = cqlSession.execute(ss);

    logger.info("SIMPLE STATEMENT WAS APPLIED: " + rs.wasApplied());

    return mysteriousIslandID;
  }

  private static UUID insertNewBookByPreparedStatement(CqlSession cqlSession) {
    var twentyLeaguesID = UUID.randomUUID();

    var ps = cqlSession.prepare("INSERT INTO library.books (id, title, publishing_year) VALUES (?, ?, ?)");
    var bs = ps.bind()
        .setUuid(0, twentyLeaguesID)
        .setString(1, "Twenty Thousand Leagues Under the Seas")
        .setInt(2, 1870)
        .setConsistencyLevel(ConsistencyLevel.ONE);

    var rs = cqlSession.execute(bs);

    logger.info("PREPARED STATEMENT WAS APPLIED: " + rs.wasApplied());

    return twentyLeaguesID;
  }

  private static void printSelectionByIdWithPreparedStatement(CqlSession cqlSession, UUID id) {
    var ps = cqlSession.prepare("SELECT id, title, publishing_year FROM library.books WHERE id=?");
    var bs = ps.bind().setUuid(0, id);

    var rs = cqlSession.execute(bs);
    if (rs.wasApplied()) {
      for (Row row : rs) {
        logger.info("ID: " + row.getUuid("id"));
        logger.info("TITLE: " + row.getString("title"));
        logger.info("PUBLISHING YEAR: " + row.getInt("publishing_year"));
      }
    } else {
      logger.info("SELECT BY ID WAS NOT APPLIED!!!");
    }
  }

  private static void printSelectionByIdWithQueryBuilder(CqlSession cqlSession, UUID id) {
    var ss = QueryBuilder.selectFrom("books")
        .all()
        .whereColumn("id").isEqualTo(QueryBuilder.literal(id))
        .build();

    var rs = cqlSession.execute(ss);
    if (rs.wasApplied()) {
      for (Row row : rs) {
        logger.info("ID: " + row.getUuid("id"));
        logger.info("TITLE: " + row.getString("title"));
      }
    } else {
      logger.info("SELECT BY QUERY WAS NOT APPLIED!!!");
    }
  }

  private static void printCountWithQueryBuilder(CqlSession cqlSession) {
    var ss = QueryBuilder.selectFrom("books")
        .countAll()
        .build();

    var rs = cqlSession.execute(ss);
    if (rs.wasApplied()) {
      logger.info("Books count: " + rs.one().getLong(0));
    } else {
      logger.info("COUNT FOR ALL BOOKS WAS NOT APPLIED!!! ");
    }
  }

  private static void truncateBooks(CqlSession cqlSession) {
    var ss = QueryBuilder.truncate("books").build();

    cqlSession.execute(ss);

    printCountWithQueryBuilder(cqlSession);
  }
}
