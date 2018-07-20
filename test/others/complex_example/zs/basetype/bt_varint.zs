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
        VARINT16_T  varInt16_T;
    case 7:
        VARUINT16_T varUInt16_T;
    case 8:
        VARINT32_T  varInt32_T;
    case 9:
        VARUINT32_T varUInt32_T;
    case 10:
        VARINT64_T  varInt64_T;
    case 11:
        VARUINT64_T varUInt64_T;

    case 12:
        varint16    arrVarInt16[ 10 ];
    case 13:
        varuint16   arrVarUInt16[ 10 ];
    case 14:
        varint32    arrVarInt32[ 10 ];
    case 15:
        varuint32   arrVarUInt32[ 10 ];
    case 16:
        varint64    arrVarInt64[ 10 ];
    case 17:
        varuint64   arrVarUInt64[ 10 ];
    case 18:
        VARINT16_T  arrVarInt16_T[ 10 ];
    case 19:
        VARUINT16_T arrVarUInt16_T[ 10 ];
    case 20:
        VARINT32_T  arrVarInt32_T[ 10 ];
    case 21:
        VARUINT32_T arrVarUInt32_T[ 10 ];
    case 22:
        VARINT64_T  arrVarInt64_T[ 10 ];
    case 23:
        VARUINT64_T arrVarUInt64_T[ 10 ];

    case 30:
        EnumV16     varEnum16;
    case 31:
        EnumVU16    varEnumU16;
    case 32:
        EnumV32     varEnum32;
    case 33:
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

    VARINT16_T  varInt16_T;
    VARUINT16_T varUInt16_T;

    VARINT32_T  varInt32_T;
    VARUINT32_T varUInt32_T;

    VARINT64_T  varInt64_T;
    VARUINT64_T varUInt64_T;


    varint16    arrVarInt16[ 10 ];
    varuint16   arrVarUInt16[ 10 ];

    varint32    arrVarInt32[ 10 ];
    varuint32   arrVarUInt32[ 10 ];

    varint64    arrVarInt64[ 10 ];
    varuint64   arrVarUInt64[ 10 ];

    VARINT16_T  arrVarInt16_T[ 10 ];
    VARUINT16_T arrVarUInt16_T[ 10 ];

    VARINT32_T  arrVarInt32_T[ 10 ];
    VARUINT32_T arrVarUInt32_T[ 10 ];

    VARINT64_T  arrVarInt64_T[ 10 ];
    VARUINT64_T arrVarUInt64_T[ 10 ];

    EnumV16     varEnum16;
    EnumVU16    varEnumU16;
    EnumV32     varEnum32;
    EnumVU32    varEnumU32;
};

struct VarIntStructureParameter( varint16 a, varint32 b )
{
    bit< a >    value1;
    bit< b >    value2;

    function varint16 returnParamA()
    {
        return (a + VUINT16_MIN) / 2;
    }

    function varint32 returnParamB()
    {
        return b;
    }
};

struct VarUIntStructureParameter( varuint16 a, varuint32 b )
{
    int< a >    value1;
    int< b >    value2;

    function varuint16 returnParamA()
    {
        return (a + VUINT16_MIN) / 2;
    }

    function varuint32 returnParamB()
    {
        return b;
    }
};

struct VarIntStructureParameterTest
{
    VarIntStructureParameter( 1, 1024 )      varintAutoParams;
    VarUIntStructureParameter( -1, -1024 )   varuintAutoParams;
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
