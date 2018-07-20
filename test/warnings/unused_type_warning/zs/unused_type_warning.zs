package unused_type_warning;

// Unused enumeration.
enum bit:2 UnusedEnumeration
{
    ID_ONE = 1,
    ID_TWO = 2
};

// Used enumeration.
enum bit:3 Color
{
    RED   = 0,
    BLACK = 1
};

// Unused subtype.
subtype int32 UnusedSubtype;

// Used subtypes.
subtype int16 Red;
subtype int8  Black;
subtype int:4 Blue;

const Blue DARK_BLUE = 2;

// Unused choice.
choice UnusedChoice(Color selector) on selector
{
    case Color.RED:
        Red red;

    case Color.BLACK:
        Black black;
};

// Used choice.
choice BoolChoice(bool selector) on selector
{
    case false:
        bit:1   value1;

    case true:
        bit:2   value2;
};

// Unused union.
union UnusedUnion
{
    Red     red;
    Black   black;
};

// Used union.
union BoolUnion
{
    bit:8   oneByte;
    bit:4   oneNibble;
};

// Unused structure.
struct UnusedStructure(Color color)
{
    bool    hasRed      if color == Color.RED;
    bool    hasBlack    if color == Color.BLACK;
};

// Used structure.
struct BoolStructure(bool isByte)
{
    BoolChoice(isByte)  boolChoice;
    BoolUnion           boolUnion;
};

// Unused table.
sql_table UnusedTable
{
    int32                   id  sql "PRIMARY KEY";
    bool                    isByte;
};

// Used table.
sql_table BoolStructureTable
{
    int32                   id  sql "PRIMARY KEY";
    bool                    isByte;
    BoolStructure(isByte)   boolStructure;
};

sql_database BoolStructureDatabase
{
    BoolStructureTable      boolStructureTable;
};
