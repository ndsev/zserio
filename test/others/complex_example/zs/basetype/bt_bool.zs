package basetype.bt_bool;

const bool BOOL_CONST1 = true;
const bool BOOL_CONST2 = false;

subtype bool half;

struct BoolStructure
{
    uint32  lastFieldOffset;

    bool    first if lastFieldOffset != 1;
    half    second;

    int16   count;

    bool    array1[ count ];
    half    array2[ count ];

lastFieldOffset:
    uint32  lastField;
};

choice BoolChoice(int8 type) on type
{
    case 0:
        bool    first;
    case 1:
        half    second;
    case 2:
        bool    array1[ 10 ];
    case 3:
        half    array2[ 10 ];
};

choice BoolChoice2(bool type) on type
{
    case true:
        bool    first;
    case false:
        bool    array[ 10 ];
};

union BoolUnion
{
    bool first;
    half second;
    bool array1[ 10 ];
    half array2[ 10 ];
};

struct BoolStructureParameter( bool a )
{
    int16   value1;
    int16   value2 if a == true;

    function bool returnParamA()
    {
        return a;
    }
};

struct BoolStructureParameterTest
{
    BoolStructureParameter( true )   boolAutoParams1;
    BoolStructureParameter( false )  boolAutoParams2;
};
