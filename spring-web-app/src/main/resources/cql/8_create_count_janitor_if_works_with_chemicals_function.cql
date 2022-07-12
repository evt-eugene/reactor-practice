CREATE FUNCTION IF NOT EXISTS library.count_janitor_if_works_with_chemicals (skills list<text>)
RETURNS NULL ON NULL INPUT
RETURNS int
LANGUAGE java AS $$
  return skills.contains("KNOWLEDGE_OF_CLEANING_CHEMICALS_AND_SUPPLIES") ? 1 : 0;
$$;