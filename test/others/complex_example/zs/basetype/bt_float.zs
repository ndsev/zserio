package basetype.bt_float;

const float16 FLOAT_CONST1 = 0;
const float16 FLOAT_CONST5 = -12;

subtype float16 half;

struct FloatStructure
{
    uint32  lastFieldOffset;

    float16 first;
    half    second;

    int16   count;

    float16 array1[ count ];
    half    array2[ count ];

lastFieldOffset:
    uint32      lastField;
};

choice FloatChoice(int8 type) on type
{
    case 0:
        float16 first;
    case 1:
        half    second;
    case 2:
        float16 array1[ 10 ];
    case 3:
        half    array2[ 10 ];
};

union FloatUnion
{
    float16 first;
    half    second;
    float16 array1[ 10 ];
    half    array2[ 10 ];
};

struct FloatStructureParameter( float16 a )
{
    int16   value1;
    int16   value2 if a > 5.0;

    function float16 returnParamA()
    {
        return a;
    }
};

struct FloatStructureParameterTest
{
    FloatStructureParameter( 5.0 ) floatParams;
};
