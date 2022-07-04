CREATE TABLE IF NOT EXISTS library.janitors (
  id uuid,
  name text,
  description text,
  skills list<text>,
  version bigint,
  PRIMARY KEY (id)
);