package compound.ct_structure;

struct MyStructure
{
    uint32      lastFieldOffset;
    int16       count;
    int8        array[ count ];

    uint8       b   : sum( array ) > 10;
    bit:4       b2  if sum( array ) <= 10;

    bit:4       c   : lengthof( array ) > 5;
    bit:4       c2  if lengthof( array ) <= 5;

lastFieldOffset:
    uint32      lastField;

    function uint16 multiply()
    {
        return count * 10;
    }
};

struct Element
{
    int8 _8bits;
};

// make sure that empty types (no members) can compile fine
struct EmptyStructure
{
};

struct EmptyStructureWithFunction
{
    function uint8 value()
    {
        return 3;
    }
};

struct ImplicitElementArray
{
    implicit Element    elements[];
};

subtype uint8 MagicCode;

const MagicCode UNDEFINED_MAGIC_CODE = 0;

struct UseOfConst(MagicCode defaultMagicCode)
{
    MagicCode magicCode if defaultMagicCode == UNDEFINED_MAGIC_CODE;
};

struct TestConstParameter
{
    UseOfConst(UNDEFINED_MAGIC_CODE) single;
    UseOfConst(UNDEFINED_MAGIC_CODE) list[2];
};


struct TestForBug1732604
{
    ByteAlignedBitField(5) data;
};

struct ByteAlignedBitField(uint16 numBits)
{
      bit<8*((numBits+7)/8)>    mask;
};

struct CondExpr
{
    uint8        tag;
    CondBlock(tag % 2 == 0 ? 47 : 11)  block;
};

struct CondBlock(uint8 tag)
{
    int32        value : value == tag;
};

struct RecursiveStructure
{
    uint8    count;
    RecursiveData(count) theElement    if count > 0;
};

struct RecursiveData(uint8 byteCount)
{
    uint8    dataBytes[byteCount]    : byteCount > 0;
    uint8    blockTerminator;

    RecursiveData(blockTerminator)    nextData    if blockTerminator > 0;
};
