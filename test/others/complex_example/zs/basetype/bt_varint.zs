package basetype.bt_varint;

const varint16  VINT16_MIN = -16383;
const varint16  VINT16_MAX = 16383;
const varuint16 VUINT16_MIN = 0;
const varuint16 VUINT16_MAX = 32767;

const varint32 VINT32_MIN = -268435455;
const varint32 VINT32_MAX = 268435455;
const varuint32 VUINT32_MIN = 0;
const varuint32 VUINT32_MAX = 536870911;

const varint64 VUINT64_MIN = 0;

// subtypes
subtype varint16    VARINT16_T;
subtype varuint16   VARUINT16_T;

subtype varint32    VARINT32_T;
subtype varuint32   VARUINT32_T;

subtype varint64    VARINT64_T;
subtype varuint64   VARUINT64_T;

subtype varint      VARINT_T;
subtype varuint     VARUINT_T;

// enumerations
enum varint16 EnumV16
{
    VALUE1 = 0,
    VALUE2 = -200,
    VALUE3 = 200
};

enum varuint16 EnumVU16
{
    VALUE1 = 0,
    VALUE2 = 100,
    VALUE3 = 200
};

enum varint32 EnumV32
{
    VALUE1 = 0,
    VALUE2 = -100,
    VALUE3 = 200
};

enum varuint32 EnumVU32
{
    VALUE1 = 0,
    VALUE2 = 100,
    VALUE3 = 200
};

struct VarIntStructure
{
    uint32      lastFieldOffset;

    EnumV16     varEnum16;
    EnumVU16    varEnumU16;

    EnumV32     varEnum32;
    EnumVU32    varEnumU32;

    varint16    varInt16;
    varuint16   varUInt16;

    varint32    varInt32;
    varuint32   varUInt32;

    varint64    varInt64;
    varuint64   varUInt64;

    VARINT16_T  varInt16_T;
    VARUINT16_T varUInt16_T;

    VARINT32_T  varInt32_T;
    VARUINT32_T varUInt32_T;

    VARINT64_T  varInt64_T;
    VARUINT64_T varUInt64_T;

    int16       count;

    varint16    arrVarInt16[ count ];
    varuint16   arrVarUInt16[ count ];

    varint32    arrVarInt32[ count ];
    varuint32   arrVarUInt32[ count ];

    varint64    arrVarInt64[ count ];
    varuint64   arrVarUInt64[ count ];

    VARINT16_T  arrVarInt16_T[ count ];
    VARUINT16_T arrVarUInt16_T[ count ];

    VARINT32_T  arrVarInt32_T[ count ];
    VARUINT32_T arrVarUInt32_T[ count ];

    VARINT64_T  arrVarInt64_T[ count ];
    VARUINT64_T arrVarUInt64_T[ count ];

    VARINT_T    arrVarInt_T[ count ];
    VARUINT_T   arrVarUInt_T[ count ];

lastFieldOffset:
    uint32      lastField;
};

choice VarIntChoice(varint16 type) on type
{
    case 0:
        varint16    varInt16;
    case 1:
        varuint16   varUInt16;
    case 2:
        varint32    varInt32;
    case 3:
        varuint32   varUInt32;
    case 4:
        varint64    varInt64;
    case 5:
        varuint64   varUInt64;
    case 6:
        varint      varInt;
    case 7:
        varuint     varUInt;
    case 8:
        VARINT16_T  varInt16_T;
    case 9:
        VARUINT16_T varUInt16_T;
    case 10:
        VARINT32_T  varInt32_T;
    case 11:
        VARUINT32_T varUInt32_T;
    case 12:
        VARINT64_T  varInt64_T;
    case 13:
        VARUINT64_T varUInt64_T;
    case 14:
        VARINT_T    varInt_T;
    case 15:
        VARUINT_T   varUInt_T;

    case 16:
        varint16    arrVarInt16[ 10 ];
    case 17:
        varuint16   arrVarUInt16[ 10 ];
    case 18:
        varint32    arrVarInt32[ 10 ];
    case 19:
        varuint32   arrVarUInt32[ 10 ];
    case 20:
        varint64    arrVarInt64[ 10 ];
    case 21:
        varuint64   arrVarUInt64[ 10 ];
    case 22:
        varint      arrVarInt[ 10 ];
    case 23:
        varuint     arrVarUInt[ 10 ];
    case 24:
        VARINT16_T  arrVarInt16_T[ 10 ];
    case 25:
        VARUINT16_T arrVarUInt16_T[ 10 ];
    case 26:
        VARINT32_T  arrVarInt32_T[ 10 ];
    case 27:
        VARUINT32_T arrVarUInt32_T[ 10 ];
    case 28:
        VARINT64_T  arrVarInt64_T[ 10 ];
    case 29:
        VARUINT64_T arrVarUInt64_T[ 10 ];
    case 30:
        VARINT_T    arrVarInt_T[ 10 ];
    case 31:
        VARUINT_T   arrVarUInt_T[ 10 ];

    case 32:
        EnumV16     varEnum16;
    case 33:
        EnumVU16    varEnumU16;
    case 34:
        EnumV32     varEnum32;
    case 35:
        EnumVU32    varEnumU32;
};

union VarIntUnion
{
    varint16    varInt16;
    varuint16   varUInt16;

    varint32    varInt32;
    varuint32   varUInt32;

    varint64    varInt64;
    varuint64   varUInt64;

    varint      varInt;
    varuint     varUInt;

    VARINT16_T  varInt16_T;
    VARUINT16_T varUInt16_T;

    VARINT32_T  varInt32_T;
    VARUINT32_T varUInt32_T;

    VARINT64_T  varInt64_T;
    VARUINT64_T varUInt64_T;

    VARINT_T    varInt_T;
    VARUINT_T   varUInt_T;

    varint16    arrVarInt16[ 10 ];
    varuint16   arrVarUInt16[ 10 ];

    varint32    arrVarInt32[ 10 ];
    varuint32   arrVarUInt32[ 10 ];

    varint64    arrVarInt64[ 10 ];
    varuint64   arrVarUInt64[ 10 ];

    varint      arrVarInt[ 10 ];
    varuint     arrVarUInt[ 10 ];

    VARINT16_T  arrVarInt16_T[ 10 ];
    VARUINT16_T arrVarUInt16_T[ 10 ];

    VARINT32_T  arrVarInt32_T[ 10 ];
    VARUINT32_T arrVarUInt32_T[ 10 ];

    VARINT64_T  arrVarInt64_T[ 10 ];
    VARUINT64_T arrVarUInt64_T[ 10 ];

    VARINT_T    arrVarInt_T[ 10 ];
    VARUINT_T   arrVarUInt_T[ 10 ];

    EnumV16     varEnum16;
    EnumVU16    varEnumU16;
    EnumV32     varEnum32;
    EnumVU32    varEnumU32;
};

struct VarIntStructureParameter( varint16 a, varint32 b, varint c )
{
    int< a >    value1;
    int< b >    value2 : value2 < c;

    function varint16 returnParamA()
    {
        return (a + VUINT16_MIN) / 2;
    }

    function varint32 returnParamB()
    {
        return b;
    }

    function varint returnParamC()
    {
        return c & 0x7fffffff;
    }
};

struct VarUIntStructureParameter( varuint16 a, varuint32 b, varuint c )
{
    bit< a >    value1;
    bit< b >    value2 : value2 < c;

    function varuint16 returnParamA()
    {
        return (a + VUINT16_MIN) / 2;
    }

    function varuint32 returnParamB()
    {
        return b;
    }

    function varuint returnParamC()
    {
        return c & 0xffffffff;
    }
};

struct VarIntStructureParameterTest
{
    VarIntStructureParameter( 2, 10, -12345678 ) varintAutoParams;
    VarUIntStructureParameter( 8, 12, 12345678 ) varuintAutoParams;
};

subtype varuint16 VarBitValue;

const VarBitValue VARBIT1  = 0;
const VarBitValue VARBIT2  = 1;
const VarBitValue VARBIT3  = 2;
const VarBitValue VARBIT13 = 12;
const VarBitValue VARBIT14 = 13;
const VarBitValue VARBIT15 = 14;

struct VarBitValueTest
{
    VarBitValue  bits;

    function bool testBit1()
    {
        return (bits & 1) == 1;
    }

    function bool testBit2()
    {
        return (bits & (1 << VARBIT2)) != 0;
    }

    function bool testBit3()
    {
        return (bits & (1 << VARBIT3)) != 0;
    }

    function bool testBit13()
    {
        return (bits & (1 << VARBIT13)) != 0;
    }

    function bool testBit14()
    {
        return (bits & (1 << VARBIT14)) != 0;
    }

    function bool testBit15()
    {
        return (bits & (1 << VARBIT15)) != 0;
    }
};

struct VarBitOperatorTest
{
    varuint16  bits;

    function varuint16 testAnd()
    {
        return bits & 0xABCD;
    }

    function varuint16 testOr()
    {
        return bits | 0xABCD;
    }

    function varuint16 testXor()
    {
        return bits ^ 0xABCD;
    }

    function varuint16 testNot()
    {
        return ~bits;
    }

    function varuint16 testAll()
    {
        return ~((0xABCD ^ (bits | 1010101010101010B)) & 0xABCD);
    }
};
