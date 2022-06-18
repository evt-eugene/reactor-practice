CREATE KEYSPACE IF NOT EXISTS library
   WITH REPLICATION = {
      'class' : 'SimpleStrategy', 'replication_factor' : 1
   }
   AND DURABLE_WRITES = true;

CREATE TABLE IF NOT EXISTS library.books (
   id uuid PRIMARY KEY,
   title text,
   publishing_year int
);

CREATE INDEX IF NOT EXISTS books_title_idx ON library.books (title);