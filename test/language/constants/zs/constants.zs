package constants;

// unsigned integer types
const uint8         UINT8_MIN_CONSTANT = 0x00;
const uint8         UINT8_MAX_CONSTANT = 0xFF;
const uint16        UINT16_MIN_CONSTANT = 0x0000;
const uint16        UINT16_MAX_CONSTANT = 0xFFFF;
const uint32        UINT32_MIN_CONSTANT = 0x00000000;
const uint32        UINT32_MAX_CONSTANT = 0xFFFFFFFF;
const uint64        UINT64_MIN_CONSTANT = 0x0000000000000000;
const uint64        UINT64_MAX_CONSTANT = 0xFFFFFFFFFFFFFFFF;

// signed integer types
const int8          INT8_MIN_CONSTANT = -128;
const int8          INT8_MAX_CONSTANT = 127;
const int16         INT16_MIN_CONSTANT = -32768;
const int16         INT16_MAX_CONSTANT = 32767;
const int32         INT32_MIN_CONSTANT = -2147483648;
const int32         INT32_MAX_CONSTANT = 2147483647;
const int64         INT64_MIN_CONSTANT = -9223372036854775808;
const int64         INT64_MAX_CONSTANT = 9223372036854775807;

// unsigned bitfield types
const bit:8         BITFIELD8_MIN_CONSTANT = 0x00;
const bit:8         BITFIELD8_MAX_CONSTANT = 0xFF;
const bit<8>        VARIABLE_BITFIELD_CONSTANT = 0xAB;

// signed bitfield types
const int:8         INTFIELD8_MIN_CONSTANT = -128;
const int:8         INTFIELD8_MAX_CONSTANT = 127;
const int<8>        VARIABLE_INTFIELD_CONSTANT = 0x12;

// float type
const float16       FLOAT16_CONSTANT = 3.13;

// variable unsigned integer types
const varuint16     VARUINT16_MIN_CONSTANT = 0x0000;
const varuint16     VARUINT16_MAX_CONSTANT = 0x7FFF;
const varuint32     VARUINT32_MIN_CONSTANT = 0x00000000;
const varuint32     VARUINT32_MAX_CONSTANT = 0x1FFFFFFF;
const varuint64     VARUINT64_MIN_CONSTANT = 0x0000000000000000;
const varuint64     VARUINT64_MAX_CONSTANT = 0x01FFFFFFFFFFFFFF;

// variable signed integer types
const varint16      VARINT16_MIN_CONSTANT = -16383;
const varint16      VARINT16_MAX_CONSTANT = 16383;
const varint32      VARINT32_MIN_CONSTANT = -268435455;
const varint32      VARINT32_MAX_CONSTANT = 268435455;
const varint64      VARINT64_MIN_CONSTANT = -72057594037927935;
const varint64      VARINT64_MAX_CONSTANT = 72057594037927935;

// boolean type
const bool          BOOL_TRUE_CONSTANT = true;
const bool          BOOL_FALSE_CONSTANT = false;

// string types
const string        STRING_CONSTANT = "Test \"Quated\" String";
const string        UNICODE_ESC_STRING_CONSTANT = "Test string with unicode escape \u0019";
const string        HEX_ESC_STRING_CONSTANT = "Test string with hexadecimal escape \x19";
const string        OCTAL_ESC_STRING_CONSTANT = "Test string with octal escape \031";

// enumeration type
enum bit:4 Colors
{
    WHITE,
    BLACK,
    RED,
    BLUE
};

// constant defined by enum value
const Colors        DEFAULT_PEN_COLOR = Colors.BLACK;

// constant defined by another constant
const uint32        UINT32_FULL_MASK = UINT32_MAX_CONSTANT;
