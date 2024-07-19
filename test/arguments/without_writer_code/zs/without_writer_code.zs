package without_writer_code;

enum int8 ItemType
{
    SIMPLE,
    WITH_EXTRA_PARAM,
};

bitmask bit:3 VersionAvailability
{
    VERSION_NUMBER,
    VERSION_STRING
};

union ExtraParamUnion
{
    uint16  value16;
    uint32  value32;
};

struct ItemWithOptionalField
{
    optional uint16 opt;
};

struct Item(ItemType itemType)
{
    uint16          param;
    ExtraParamUnion extraParam if itemType == ItemType.WITH_EXTRA_PARAM;
};

choice ItemChoice(bool hasItem) on hasItem
{
    case true:
        Item(ItemType.WITH_EXTRA_PARAM)  item;

    case false:
        uint16      param;
};

struct ItemChoiceHolder
{
    bool                hasItem;
    ItemChoice(hasItem) itemChoice;
};

struct Tile
{
    VersionAvailability versionAvailability;
    uint8               version if valueof(versionAvailability & VersionAvailability.VERSION_NUMBER) != 0;
    string              versionString if valueof(versionAvailability & VersionAvailability.VERSION_STRING) != 0;

    uint32              numElementsOffset;

numElementsOffset:
    uint32              numElements;
    uint32              offsets[numElements];
offsets[@index]:
    ItemChoiceHolder    data[numElements];
};

sql_table GeoMapTable
{
    int32           tileId sql "PRIMARY KEY NOT NULL";
    Tile            tile;
};

sql_database WorldDb
{
    GeoMapTable europe;
    GeoMapTable america;
};
