package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
public class CassandraConfig extends AbstractReactiveCassandraConfiguration {

  @Autowired
  private ResourceLoader resourceLoader;

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

    try (var reader = new InputStreamReader(resource.getInputStream(), charset)) {
      return FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  protected String getKeyspaceName() {
    return "library";
  }

}