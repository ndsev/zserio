package sql_databases.db_with_relocation;

struct Tile
{
    uint8   version;
    uint8   data;
};

sql_table CountryMapTable
{
    int32   tileId sql "PRIMARY KEY NOT NULL";
    Tile    tile;
};

sql_database EuropeDb
{
    CountryMapTable germany;
};

sql_database AmericaDb
{
    CountryMapTable usa;
    CountryMapTable canada;
    CountryMapTable slovakia;
    CountryMapTable czechia;
};
