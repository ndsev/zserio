package different_scopes_error;

// This is necessary to call function getValue within scope where val3 exists before it is called within
// CustomVarInt scope.
struct CustomVarIntCaller
{
    CustomVarInt    customVarInt;
    bool            isVal3 if customVarInt.getValue() == customVarInt.val3;
};

struct CustomVarInt
{
    uint8       val1;
    uint16      val2 if val1 == 255;
    bool        isVal2 if getValue() == val2; // this must report error because val3 is not available here
    uint32      val3 if val1 == 254;

    function uint32 getValue()
    {
        return (val1 == 255) ? val2 : (val1 == 254) ? val3 : val1;
    }
};
