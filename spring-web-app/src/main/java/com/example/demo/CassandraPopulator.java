package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration;
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;

@Configuration
public class CassandraPopulator extends AbstractReactiveCassandraConfiguration {

  @Override
  protected KeyspacePopulator keyspacePopulator() {
    var filePopulator = new ResourceKeyspacePopulator();
    filePopulator.setSeparator(";");
    filePopulator.setScripts(new ClassPathResource("cql/db-schema.cql"));

    return filePopulator;
  }

  @Override
  protected String getKeyspaceName() {
    return "library";
  }
}