CREATE CUSTOM INDEX IF NOT EXISTS sasi_janitors_characteristic_idx ON library.janitors(characteristic) USING 'org.apache.cassandra.index.sasi.SASIIndex'
  WITH OPTIONS = {
    'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer',
    'tokenization_enable_stemming': 'true',
    'analyzed': 'true',
    'tokenization_normalize_lowercase': 'true',
    'tokenization_locale': 'en'
  };