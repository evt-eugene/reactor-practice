package com.example.demo;

import com.example.demo.student.persistance.converters.CassandraIntToYearConverter;
import com.example.demo.student.persistance.converters.CassandraYearToIntConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.config.AbstractReactiveCassandraConfiguration;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.cql.session.init.KeyspacePopulator;
import org.springframework.data.cassandra.core.cql.session.init.ResourceKeyspacePopulator;

import java.util.List;

@Configuration
public class CassandraConfig extends AbstractReactiveCassandraConfiguration {

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

  @Bean
  public CassandraCustomConversions customConversions() {
    return new CassandraCustomConversions(
        List.of(
            CassandraIntToYearConverter.INSTANCE,
            CassandraYearToIntConverter.INSTANCE
        )
    );
  }

}