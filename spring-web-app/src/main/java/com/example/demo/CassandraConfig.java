package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
@EnableReactiveCassandraRepositories(basePackages = "com.example.demo.student.persistence")
public class CassandraConfig extends AbstractReactiveCassandraConfiguration {

  @Value("${cassandra.contact-points}")
  private String contactPoints;

  @Value("${cassandra.port}")
  private int port;

  @Value("${cassandra.local-datacenter}")
  private String localDatacenter;

  @Value("${cassandra.keyspace-name}")
  private String keySpace;

  @Autowired
  private ResourceLoader resourceLoader;

  @Override
  protected String getContactPoints() {
    return contactPoints;
  }

  @Override
  protected int getPort() {
    return port;
  }

  @Override
  protected String getLocalDataCenter() {
    return localDatacenter;
  }

  @Override
  protected String getKeyspaceName() {
    return keySpace;
  }

  @Override
  protected List<String> getStartupScripts() {
    return List.of(
        loadResourceAsString("classpath:cql/create_keyspace.cql", UTF_8),
        loadResourceAsString("classpath:cql/create_books_table.cql", UTF_8),
        loadResourceAsString("classpath:cql/create_books_title_index.cql", UTF_8)
    );
  }

  private String loadResourceAsString(String location, Charset charset) {
    var resource = resourceLoader.getResource(location);

    try (var is = resource.getInputStream()) {
      return StreamUtils.copyToString(is, charset);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}