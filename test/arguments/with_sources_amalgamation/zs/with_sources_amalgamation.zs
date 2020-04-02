package with_sources_amalgamation;

import _imported_tile_.Tile;
import ___.Empty;

sql_table GeoMapTable
{
    int32   tileId sql "PRIMARY KEY NOT NULL";
    Tile    tile;
    Empty   empty;
};

sql_database WorldDb
{
    GeoMapTable europe;
    GeoMapTable america;
};
