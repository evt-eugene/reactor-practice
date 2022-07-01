CREATE TABLE IF NOT EXISTS library.librarians (
   id   uuid,
   name frozen<fullname>,
   age  tinyint,
   version bigint,
   PRIMARY KEY ((id), name)
)
WITH CLUSTERING ORDER BY (name ASC);