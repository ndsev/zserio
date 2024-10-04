package recursive_error;

struct CustomVarInt
{
    uint8       val1;
    uint16      val2 if val1 == 255;
    uint32      val3 if val1 == 254;

    function uint32 getValue()
    {
        return (val1 == 255) ? val2 : (val1 == 254) ? val3 : getValue();
    }
};
