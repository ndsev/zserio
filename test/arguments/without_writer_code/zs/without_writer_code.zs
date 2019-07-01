package without_writer_code;

enum int8 ItemType
{
    SIMPLE,
    WITH_EXTRA_PARAM,
};

union ExtraParamUnion
{
    uint16  value16;
    uint32  value32;
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
    uint8               version;
    uint32              numElementsOffset;

numElementsOffset:
    uint32              numElements;
    uint32              offsets[numElements];
offsets[@index]:
    ItemChoiceHolder    data[numElements];
};

sql_table GeoMapTable
{
    int32           tileId sql "PRIMARY KEY";
    Tile            tile;
};

sql_database WorldDb
{
    GeoMapTable europe;
    GeoMapTable america;
};
