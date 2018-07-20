package basetype.bt_string;

const string STRING_CONST0 = "";
const string STRING_CONST1 = "Hello world";
const string STRING_CONST2 = "0xCAFEBABE";
const string STRING_CONST3 = "01101b";
const string STRING_CONST4 = "-5";

subtype string u8string;

struct StringStructure
{
    uint32      lastFieldOffset;

    string      first;
    u8string    second;

    int16       count;
    string      array1[ count ];
    u8string    array2[ count ];

lastFieldOffset:
    uint32      lastField;
};

choice StringChoice(int8 type) on type
{
    case 0:
        string      first;
    case 1:
        u8string    second;
    case 2:
        string      array1[ 10 ];
    case 3:
        u8string    array2[ 10 ];
};

union StringUnion
{
    string      first;
    u8string    second;
    string      array1[ 10 ];
    u8string    array2[ 10 ];
};
