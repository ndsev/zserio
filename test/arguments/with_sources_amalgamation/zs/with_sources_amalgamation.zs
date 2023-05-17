package with_sources_amalgamation;

import _imported_tile_.Tile;
import ___.Empty;

// two enums to check that no "inner" classes clash (e.g. ZserioElementBitSize)
enum int<3> Color
{
    RED,
    GREEN,
    BLUE
};

enum bit<3> Direction
{
    FORWARD,
    BACKWARD,
    BOTH
};

// two bitmasks to check that no "inner" classes clash (e.g. ZserioElementBitSize)
bitmask bit<5> Permission
{
    READ = 0x1,
    WRITE = 0x2,
    READ_WRITE = 0x1 | 0x2
};

bitmask bit<3> Group
{
    VISITOR,
    USER,
    ADMIN,
};

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
