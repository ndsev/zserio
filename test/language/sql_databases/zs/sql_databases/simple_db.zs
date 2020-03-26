package sql_databases.simple_db;

struct Tile
{
    uint8   version;
    uint32  numElements;
    uint8   data[numElements];
};

sql_table GeoMapTable
{
    int32   tileId sql "PRIMARY KEY NOT NULL";
    Tile    tile;
};

sql_database WorldDb
{
    GeoMapTable europe;
    GeoMapTable america;
};
