package basetype.bt_bitfield;

const bit:1     BIT1_MIN = 0;
const bit:2     BIT2_MIN = 0;
const bit:1     BIT1_MAX = 1;
const bit:2     BIT2_MAX = 2;

const bit:7     BIT7_MIN = 0;
const bit:8     BIT8_MIN = 0;
const bit:9     BIT9_MIN = 0;
const bit:7     BIT7_MAX = 127;
const bit:8     BIT8_MAX = 255;
const bit:9     BIT9_MAX = 511;

const bit:15    BIT15_MIN = 0;
const bit:16    BIT16_MIN = 0;
const bit:17    BIT17_MIN = 0;
const bit:15    BIT15_MAX = 32767;
const bit:16    BIT16_MAX = 65535;
const bit:17    BIT17_MAX = 131070;

const bit:31    BIT31_MIN = 0;
const bit:32    BIT32_MIN = 0;
const bit:33    BIT33_MIN = 0;
const bit:31    BIT31_MAX = 2147483647;
const bit:32    BIT32_MAX = 4294967294;
const bit:33    BIT33_MAX = 8589934588;

const bit:63    BIT63_MIN = 0;
const bit:63    BIT63_MAX = 9223372036854775807;

const bit:10  BIT_DECIMAL2 = 103;
const bit:8   BIT_DECIMAL3 = 255;
const int:15  BIT_DECIMAL4 = -12345;

const bit:32 HEX = 0xCAFEBABE;

// Octal: 044, 0377, -010
const bit:15  OCTAL = 0377;

// Binary: 111b, 110b, 001B, -1010b
const int:15  BINARY = -1010b;

// subtypes
subtype bit:1       BIT1;
subtype bit:2       BIT2;

subtype bit:7       BIT7;
subtype bit:8       BIT8;
subtype bit:9       BIT9;

subtype bit:15      BIT15;
subtype bit:16      BIT16;
subtype bit:17      BIT17;

subtype bit:31      BIT31;
subtype bit:32      BIT32;
subtype bit:33      BIT33;

subtype bit:63      BIT63;

subtype int:1       INT1;
subtype int:2       INT2;

subtype int:7       INT7;
subtype int:8       INT8;
subtype int:9       INT9;

subtype int:15      INT15;
subtype int:16      INT16;
subtype int:17      INT17;

subtype int:31      INT31;
subtype int:32      INT32;
subtype int:33      INT33;

subtype int:63      INT63;
subtype int:64      INT64;

// enumerations
enum bit:1 Enum1
{
    ENUM1_VALUE1 = 1
};

enum bit:7 Enum7
{
    ENUM7_VALUE1 = 127
};

enum bit:8 Enum8
{
    ENUM8_VALUE1 = 255
};

enum bit:32 Enum32
{
    ENUM32_VALUE1 = 0
};

enum bit:63 Enum63
{
    ENUM63_VALUE1 = 0
};

enum int:1 EnumS1
{
    ENUMS1_VALUE1 = 0
};

enum int:7 EnumS7
{
    ENUMS7_VALUE1 = -63
};

enum int:8 EnumS8
{
    ENUMS8_VALUE1 = -127
};

enum int:32 EnumS32
{
    ENUMS32_VALUE1 = 0x7AFEBABE
};

enum int:64 EnumS64
{
    ENUMS64_VALUE1 = -1
};

struct BitfieldStructure
{
    uint32      lastFieldOffset;

    BIT15       _15bits_t;

    INT15       _15sbits_t;

    bit:1       _1bits;
    bit:2       _2bits;

    bit:7       _7bits;
    bit:8       _8bits;
    bit:9       _9bits;

    bit:15      _15bits;
    bit:16      _16bits;
    bit:17      _17bits;

    bit:31      _31bits;
    bit:32      _32bits;
    bit:33      _33bits;

    bit:63      _63bits;

    int:1       _1sbits;
    int:2       _2sbits;

    int:7       _7sbits;
    int:8       _8sbits;
    int:9       _9sbits;

    int:15      _15sbits;
    int:16      _16sbits;
    int:17      _17sbits;

    int:31      _31sbits;
    int:32      _32sbits;
    int:33      _33sbits;

    int:63      _63sbits;
    int:64      _64sbits;

    BIT15       count;

    bit:1       array_1bits[ count ];
    bit:2       array_2bits[ count ];

    bit:7       array_7bits[ count ];
    bit:8       array_8bits[ count ];
    bit:9       array_9bits[ count ];

    bit:15      array_15bits[ count ];
    bit:16      array_16bits[ count ];
    bit:17      array_17bits[ count ];

    bit:31      array_31bits[ count ];
    bit:32      array_32bits[ count ];
    bit:33      array_33bits[ count ];

    bit:63      array_63bits[ count ];

    int:1       array_1sbits[ count ];
    int:2       array_2sbits[ count ];

    int:7       array_7sbits[ count ];
    int:8       array_8sbits[ count ];
    int:9       array_9sbits[ count ];

    int:15      array_15sbits[ count ];
    int:16      array_16sbits[ count ];
    int:17      array_17sbits[ count ];

    int:31      array_31sbits[ count ];
    int:32      array_32sbits[ count ];
    int:33      array_33sbits[ count ];

    int:63      array_63sbits[ count ];
    int:64      array_64sbits[ count ];

lastFieldOffset:
    uint32      lastField;
};

choice BitfieldChoice(bit:7 type) on type
{
    case 0:
        bit:1       _1bits;
    case 1:
        bit:2       _2bits;

    case 2:
        bit:7       _7bits;
    case 3:
        bit:8       _8bits;
    case 4:
        bit:9       _9bits;

    case 5:
        bit:15      _15bits;
    case 6:
        bit:16      _16bits;
    case 7:
        bit:17      _17bits;

    case 8:
        bit:31      _31bits;
    case 9:
        bit:32      _32bits;
    case 10:
        bit:33      _33bits;

    case 11:
        bit:63      _63bits;

    case 12:
        int:1       _1sbits;
    case 13:
        int:2       _2sbits;

    case 14:
        int:7       _7sbits;
    case 15:
        int:8       _8sbits;
    case 16:
        int:9       _9sbits;

    case 17:
        int:15      _15sbits;
    case 18:
        int:16      _16sbits;
    case 19:
        int:17      _17sbits;

    case 20:
        int:31      _31sbits;
    case 21:
        int:32      _32sbits;
    case 22:
        int:33      _33sbits;

    case 23:
        int:63      _63sbits;
    case 24:
        int:64      _64sbits;


    case 100:
        bit:1       array_1bits[ 10 ];
    case 101:
        bit:2       array_2bits[ 10 ];

    case 102:
        bit:7       array_7bits[ 10 ];
    case 103:
        bit:8       array_8bits[ 10 ];
    case 104:
        bit:9       array_9bits[ 10 ];

    case 105:
        bit:15      array_15bits[ 10 ];
    case 106:
        bit:16      array_16bits[ 10 ];
    case 107:
        bit:17      array_17bits[ 10 ];

    case 108:
        bit:31      array_31bits[ 10 ];
    case 109:
        bit:32      array_32bits[ 10 ];
    case 110:
        bit:33      array_33bits[ 10 ];

    case 111:
        bit:63      array_63bits[ 10 ];

    case 112:
        int:1       array_1sbits[ 10 ];
    case 113:
        int:2       array_2sbits[ 10 ];

    case 114:
        int:7       array_7sbits[ 10 ];
    case 115:
        int:8       array_8sbits[ 10 ];
    case 116:
        int:9       array_9sbits[ 10 ];

    case 117:
        int:15      array_15sbits[ 10 ];
    case 118:
        int:16      array_16sbits[ 10 ];
    case 119:
        int:17      array_17sbits[ 10 ];

    case 120:
        int:31      array_31sbits[ 10 ];
    case 121:
        int:32      array_32sbits[ 10 ];
    case 122:
        int:33      array_33sbits[ 10 ];

    case 123:
        int:63      array_63sbits[ 10 ];
    case 124:
        int:64      array_64sbits[ 10 ];
};

union BitfieldUnion
{
    bit:1       _1bits;
    bit:2       _2bits;

    bit:7       _7bits;
    bit:8       _8bits;
    bit:9       _9bits;

    bit:15      _15bits;
    bit:16      _16bits;
    bit:17      _17bits;

    bit:31      _31bits;
    bit:32      _32bits;
    bit:33      _33bits;

    bit:63      _63bits;

    int:1       _1sbits;
    int:2       _2sbits;

    int:7       _7sbits;
    int:8       _8sbits;
    int:9       _9sbits;

    int:15      _15sbits;
    int:16      _16sbits;
    int:17      _17sbits;

    int:31      _31sbits;
    int:32      _32sbits;
    int:33      _33sbits;

    int:63      _63sbits;
    int:64      _64sbits;


    bit:1       array_1bits[ 10 ];
    bit:2       array_2bits[ 10 ];

    bit:7       array_7bits[ 10 ];
    bit:8       array_8bits[ 10 ];
    bit:9       array_9bits[ 10 ];

    bit:15      array_15bits[ 10 ];
    bit:16      array_16bits[ 10 ];
    bit:17      array_17bits[ 10 ];

    bit:31      array_31bits[ 10 ];
    bit:32      array_32bits[ 10 ];
    bit:33      array_33bits[ 10 ];

    bit:63      array_63bits[ 10 ];

    int:1       array_1sbits[ 10 ];
    int:2       array_2sbits[ 10 ];

    int:7       array_7sbits[ 10 ];
    int:8       array_8sbits[ 10 ];
    int:9       array_9sbits[ 10 ];

    int:15      array_15sbits[ 10 ];
    int:16      array_16sbits[ 10 ];
    int:17      array_17sbits[ 10 ];

    int:31      array_31sbits[ 10 ];
    int:32      array_32sbits[ 10 ];
    int:33      array_33sbits[ 10 ];

    int:63      array_63sbits[ 10 ];
    int:64      array_64sbits[ 10 ];
};

struct BitStructureParameter( bit:1 a, bit:15 b, bit:29 c )
{
    bit< a >    value1;
    bit< b >    value2;
    bit< c >    value3;

    function bit:1 returnParamA()
    {
        return a;
    }

    function bit:15 returnParamB()
    {
        return b;
    }

    function bit:29 returnParamC()
    {
        return c;
    }
};

struct SBitStructureParameter( int:2 a, int:15 b, int:29 c )
{
    int< a >    value1;
    int< b >    value2;
    int< c >    value3;

    function int:1 returnParamA()
    {
        return a;
    }

    function int:15 returnParamB()
    {
        return b;
    }

    function int:29 returnParamC()
    {
        return c;
    }
};

struct BitStructureParameterTest
{
    BitStructureParameter( 1, 1024, 500000 )        bitAutoParams;
    SBitStructureParameter( -1, -1024, -500000 )    sbitAutoParams;
};
