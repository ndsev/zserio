package expressions.numbits_operator;

struct NumBitsFunctions
{
    uint8   value8;
    uint16  value16;
    uint32  value32;
    uint64  value64;

    function uint8 getNumBits8()
    {
        return numbits(value8);
    }

    function uint8 getNumBits16()
    {
        return numbits(value16);
    }

    function uint8 getNumBits32()
    {
        return numbits(value32);
    }

    function uint8 getNumBits64()
    {
        return numbits(value64);
    }
};
