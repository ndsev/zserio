# zserio Language Quick Reference

**Signed Integers and Bitfields**
```
int8
int16
int32
int64
varint16
varint32
varint64
int:n
int<expr>
```
**Unsigned Integers and Bitfields**
```
uint8
uint16
uint32
uint64
varuint16
varuint32
varuint64
bit:n
bit<expr>
```
**Floating Point**
```
float
```
**Boolean**
```
bool
```
**Strings**
```
string
```
**Constants**
```
const bit:1 FALSE = 0;
```
**Enumerations**
```
enum bit:3 Color
{
    NONE = 000b,
    RED = 1,
    BLUE,
    BLACK = 111b
};
```
**Structures**
```
struct MyStructure
{
    bit:4 a;
    uint8 b;
    bit:4 c;
};
```
**Choice**
```
choice VarCoordXY(uint8 width) on width
{
    case 8:  CoordXY8 coord8;
    case 16: CoordXY16 coord16;
    case 24: CoordXY24 coord24;
    case 32: CoordXY32 coord32;
};
```
**Contraints**
```
struct GraphicControlExtension
{
    uint8 byteCount : byteCount == 4;
    uint8 terminator : terminator == 0;
};
```
**Optional Member**
```
struct ItemCount
{
    uint8 count8;
    uint16 count16 if count8 == 0xFF;
};

struct Container
{
              int16 item1;
    optional  int32 item2;
};
```
**Functions**
```
struct ItemCount
{
    uint8 count8;
    uint16 count16 if count8 == 0xFF;

    function uint16 getValue()
    {
        return (count8 == 0xFF) ? count16 :
        count8;
    }
};
```
**Arrays**
```
struct ClassicArrayExample
{
    uint8 header[256];
    int16 numItems;
    Element list[numItems];
};

struct AutoArrayExample
{
    uint8 header[256];
    Element list[];

    function uint32 getNumItems()
    {
        return lengthof(list);
    }
}
```
**Alignment**
```
struct AlignmentExample
{
    bit:11 a;
align(32):
    uint32 b;
};
```
**Offset**
```
struct Tile
{
    TileHeader header;
    uint32 stringOffset;
    uint16 numFeatures;

stringOffset:
    StringTable stringTable;
};
```
**Indexed Offsets**
```
struct IndexedInt32Array
{
    uint32 offsets[10];
    bit:1 spacer;
offsets[@index]:
    int32 data[10];
};
```
**Parametrized Type**
```
struct Header
{
    uint32 version;
    uint16 numItems;
};
struct Message
{
    Header h;
    Item(h) items[h.numItems];
};
struct Item(Header header)
{
    uint16 p;
    uint32 q if header.version >= 10;
};
```
**Subtype**
```
subtype uint16 BlockIndex;

struct Block
{
    BlockIndex index;
    BlockData data;
};
```
-------
## SQLite extension 

**SQLite Table**
```
sql_table GeoMap
{
    int32 tileId sql "PRIMARY KEY";
    Tile tile;
};

sql_table Pages using fts5
{
    string title;
    string body;
};
```
**SQLite Database**
```
sql_database TheWorld
{
    GeoMap europe;
    GeoMap america;
    GeoMap africa;
    GeoMap asia;
    GeoMap australia;
};
```
