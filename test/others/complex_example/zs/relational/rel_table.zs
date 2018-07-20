package relational.rel_table;

subtype bit:4   TileId;
subtype int32   PoiId;
subtype int8    CategoryId;
subtype int64   MortonCode;
subtype int32   TileNumber;
subtype uint16  IconSetId;
subtype uint32  ServiceLocationId;

sql_table PoiServiceLocationTable
{
    ServiceLocationId   serviceLocationId;

    PoiId               poiId;
    CategoryId          catId;
    MortonCode          mortonCode  sql "UNIQUE";
    IconSetId           iconSetId   sql "NULL";

    sql "primary key (serviceLocationId)";
};

sql_table PoiVirtualTileTable
{
    TileId             tileId;
    ServiceLocationId  minId;
    ServiceLocationId  maxId;

    PoiData(tileId, 5)  data;

    sql "primary key (tileId)";
};

sql_table PoiAddress using fts3
{
    sql_virtual PoiId id;

    string      name;
    string      address;

    sql_virtual int8    isValid;
};

sql_table BoundingBoxTable using rtree
{
    int32 id;
    int32 minX;
    int32 maxX;
    int32 minY;
    int32 maxY;
};

// virtual table with no columns
sql_table VirtualTable_NoCols using spellfix1
{
};

// virtual table with only virtual columns
sql_table VirtualTable_VCols using spellfix1
{
    sql_virtual string word;
    sql_virtual int32 foo;
};

// virtual table with virtual column and a sql constraint
sql_table VirtualTable_VColContr using fts3
{
    sql_virtual string content;
    sql "edit_cost_table='poiFtsFuzzyCostTable'";
};

// virtual table with virtual and non-virtual columns and a sql constraint
sql_table VirtualTable_MixCols using fts3
{
    sql_virtual string content;
    string foo;
    sql "edit_cost_table='poiFtsFuzzyCostTable'";
};

struct PoiData(TileId level, uint8 width)
{
    int<width> dx;
    int<width> dy;

align(16):
    int8 importance if level < 13;
};

sql_table PoiDataTable
{
    PoiId   poiId;

    PoiData(explicit level, explicit width) data;
};
