CREATE AGGREGATE IF NOT EXISTS library.total_chemical_janitors (list<text>)
SFUNC state_count_janitor_if_works_with_chemicals
STYPE int
INITCOND 0;