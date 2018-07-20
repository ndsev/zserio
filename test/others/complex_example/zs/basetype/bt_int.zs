package basetype.bt_int;

// Decimal: 100, 4711, 255, -3, +2
const int8   INT_DECIMAL1 = 100;
const int16  INT_DECIMAL2 = 4711;
const uint8  INT_DECIMAL3 = 255;
const int32  INT_DECIMAL4 = -12345;

// Hexadecimal
const uint32 INT_HEX1 = 0xabCdEf;
const int16  INT_HEX2 = -0XEF;
const uint32 INT_HEX3 = 0xFEDCBA;
const int32  INT_HEX4 = 0xABCDEF;

const int16  SIGNED_HEX1 = -0x8000;
const int16  SIGNED_HEX2 = -0x1;
const uint16 UNSIGNED_HEX1 = 0x8000;
const uint16 UNSIGNED_HEX2 = 0xFFFF;

// Octal
const int32  INT_OCTAL1 = 044;
const int16  INT_OCTAL2 = 0377;
const int8   INT_OCTAL3 = -010;

// Binary
const int32  INT_BINARY1 = -111B;
const int32  INT_BINARY2 = 110b;
const int32  INT_BINARY3 = 001B;
const int32  INT_BINARY4 = -1010b;

const int8      ZSERIO_INT8_MIN = -128;
const int8      ZSERIO_INT8_MAX = 127;
const uint8     ZSERIO_UINT8_MIN = 0;
const uint8     ZSERIO_UINT8_MAX = 255;

const int16     ZSERIO_INT16_MIN = -32768;
const int16     ZSERIO_INT16_MAX = 32767;
const uint16    ZSERIO_UINT16_MIN = 0;
const uint16    ZSERIO_UINT16_MAX = 65535;

const int32     ZSERIO_INT32_MIN = -2147483648;
const int32     ZSERIO_INT32_MAX = 2147483647;
const uint32    ZSERIO_UINT32_MIN = 0;
const uint32    ZSERIO_UINT32_MAX = 4294967295;

const int64     ZSERIO_INT64_MIN = -9223372036854775808;
const int64     ZSERIO_INT64_MAX = 9223372036854775807;
const uint64    ZSERIO_UINT64_MIN = 0;
const uint64    ZSERIO_UINT64_MAX = 18446744073709551615;

// subtypes
subtype int8    s8;
subtype uint8   u8;
subtype int16   s16;
subtype uint16  u16;
subtype int32   s32;
subtype uint32  u32;
subtype int64   s64;
subtype uint64  u64;

// enumerations
enum int8 Enum8
{
    ENUM8_VALUE1 = 100,
    ENUM8_VALUE1_T,
    ENUM8_VALUE2,
    ENUM8_VALUE2_T,
    ENUM8_VALUE3,
    ENUM8_VALUE3_T,
    ENUM8_VALUE4,
    ENUM8_VALUE4_T,
    ENUM8_VALUE5,
    ENUM8_VALUE5_T,
    ENUM8_VALUE6,
    ENUM8_VALUE6_T,
    ENUM8_VALUE7,
    ENUM8_VALUE7_T,
    ENUM8_VALUE8,
    ENUM8_VALUE8_T
};

enum int32 Enum32
{
    VALUE1 = 0x7AFEBABE,
    VALUE2 = 0377, // octal
    VALUE3 = -1377,
    VALUE4 = 1010101010b,
    VALUE5 = INT_DECIMAL4
};

enum s32 Enum32_T
{
    VALUE1 = 0x7AFEBABE
};

enum uint8 EnumU8
{
    VALUE1 = 0
};

enum int16 Enum16
{
    VALUE1 = 0
};

enum uint16 EnumU16
{
    VALUE1 = 0
};

enum u32 EnumU32_T
{
    VALUE1 = 0xCAFEBABE
};

enum int64 Enum64
{
    ENUM64_VALUE1 = 500000000000
};

struct IntStructure
{
    uint32      lastFieldOffset;

    int8        fieldInt8;
    s8          fieldInt8_T;

    uint8       fieldUInt8;
    u8          fieldUInt8_T;

    int16       fieldInt16;
    s16         fieldInt16_T;

    uint16      fieldUInt16;
    u16         fieldUInt16_T;

    int32       fieldInt32;
    s32         fieldInt32_T;

    uint32      fieldUInt32;
    u32         fieldUInt32_T;

    int64       fieldInt64;
    s64         fieldInt64_T;

    uint64      fieldUInt64;
    u64         fieldUInt64_T;

    int16       count;

    Enum8       arrEnum8[ count ];

    int8        arrInt8[ count ];
    s8          arrInt8_T[ count ];

    uint8       arrUInt8[ count ];
    u8          arrUInt8_T[ count ];

    int16       arrInt16[ count ];
    s16         arrInt16_T[ count ];

    uint16      arrUInt16[ count ];
    u16         arrUInt16_T[ count ];

    int32       arrInt32[ count ];
    s32         arrInt32_T[ count ];

    uint32      arrUInt32[ count ];
    u32         arrUInt32_T[ count ];

    int64       arrInt64[ count ];
    s64         arrInt64_T[ count ];

    uint64      arrUInt64[ count ];
    u64         arrUInt64_T[ count ];

lastFieldOffset:
    uint32      lastField;
};

choice IntChoice2(int8 i) on i
{
    case INT_DECIMAL1:
        int8 a;
    case 2:
        int16 b;
};

choice IntChoice(Enum8 type) on type
{
    case ENUM8_VALUE1:
        int8        value1;
    case ENUM8_VALUE1_T:
        s8          value1_t;

    case ENUM8_VALUE2:
        uint8       value2;
    case ENUM8_VALUE2_T:
        u8          value2_t;

    case ENUM8_VALUE3:
        int16       value3;
    case ENUM8_VALUE3_T:
        s16         value3_t;

    case ENUM8_VALUE4:
        uint16      value4;
    case ENUM8_VALUE4_T:
        u16         value4_t;

    case ENUM8_VALUE5:
        int32       value5;
    case ENUM8_VALUE5_T:
        s32         value5_t;

    case ENUM8_VALUE6:
        uint32      value6;
    case ENUM8_VALUE6_T:
        u32         value6_t;

    case ENUM8_VALUE7:
        int64       value7;
    case ENUM8_VALUE7_T:
        s64         value7_t;

    case ENUM8_VALUE8:
        uint64      value8;
    case ENUM8_VALUE8_T:
        u64         value8_t;
};

union IntUnion
{
    int8        value1;
    s8          value1_t;

    uint8       value2;
    u8          value2_t;

    int16       value3;
    s16         value3_t;

    uint16      value4;
    u16         value4_t;

    int32       value5;
    s32         value5_t;

    uint32      value6;
    u32         value6_t;

    int64       value7;
    s64         value7_t;

    uint64      value8;
    u64         value8_t;
};

struct ImplicitInt8Array
{
    implicit int8   bytes[];
};

struct IntStructureParameter( int8 a, int16 b, int32 c )
{
    bit< a >    value1;
    bit< b >    value2;
    bit< c >    value3;

    function int8 returnParamA()
    {
        return a;
    }

    function int16 returnParamB()
    {
        return b;
    }

    function int32 returnParamC()
    {
        return c;
    }
};

struct UIntStructureParameter( uint8 a, uint16 b )
{
    int< a >    value1;
    int< b >    value2;

    function uint8 returnParamA()
    {
        return a;
    }

    function uint16 returnParamB()
    {
        return b;
    }
};

struct IntStructureParameterTest
{
    IntStructureParameter( 1, 1024, 500000 )     intAutoParams;
    UIntStructureParameter( -1, -1024 )          uintAutoParams;
};

subtype bit:7 BitValue;

const BitValue BIT1 = 0;
const BitValue BIT2 = 1;
const BitValue BIT3 = 2;
const BitValue BIT4 = 3;
const BitValue BIT5 = 4;
const BitValue BIT6 = 5;
const BitValue BIT7 = 6;

struct BitValueTest
{
    BitValue  bits;

    function bool testBit1()
    {
        return (bits & 1) == 1;
    }

    function bool testBit2()
    {
        return (bits & (1 << BIT2)) != 0;
    }

    function bool testBit3()
    {
        return (bits & (1 << BIT3)) != 0;
    }

    function bool testBit4()
    {
        return (bits & (1 << BIT4)) != 0;
    }

    function bool testBit5()
    {
        return (bits & (1 << BIT5)) != 0;
    }

    function bool testBit6()
    {
        return (bits & (1 << BIT6)) != 0;
    }

    function bool testBit7()
    {
        return (bits & (1 << BIT7)) != 0;
    }
};

struct BitOperatorTest
{
    uint16  bits;

    function uint16 testAnd()
    {
        return bits & 0xABCD;
    }

    function uint16 testOr()
    {
        return bits | 0xABCD;
    }

    function uint16 testXor()
    {
        return bits ^ 0xABCD;
    }

    function uint16 testNot()
    {
        return ~bits;
    }

    function uint16 testAll()
    {
        return ~((0xABCD ^ (bits | 1010101010101010B)) & 0xABCD);
    }
};
