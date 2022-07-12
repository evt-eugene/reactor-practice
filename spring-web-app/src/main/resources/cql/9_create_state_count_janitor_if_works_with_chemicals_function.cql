CREATE FUNCTION IF NOT EXISTS library.state_count_janitor_if_works_with_chemicals (total int, skills list<text>)
RETURNS NULL ON NULL INPUT
RETURNS int
LANGUAGE java AS $$
  return skills.contains("KNOWLEDGE_OF_CLEANING_CHEMICALS_AND_SUPPLIES") ? total + 1 : total;
$$;