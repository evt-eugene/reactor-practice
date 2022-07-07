CREATE TABLE IF NOT EXISTS library.janitors (
  id uuid,
  name text,
  description text,
  skills list<text>,
  characteristic text,
  created_by text,
  created_date timestamp,
  last_modified_by text,
  last_modified_date timestamp,
  version bigint,
  PRIMARY KEY (id)
);