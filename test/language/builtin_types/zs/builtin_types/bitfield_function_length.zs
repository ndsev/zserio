package builtin_types.bitfield_function_length;

struct Container
{
    uint64          id;
    uint64          array[7];
    bit<length3()>  bitField3; // should be mapped to 8-bits unsigned integer
    bit<length4()>  bitField4; // should be mapped to 16-bits unsigned integer
    bit<length5()>  bitField5; // should be mapped to 32-bits unsigned integer

    function bit:3 length3()
    {
        return lengthof(array);
    }

    function bit:4 length4()
    {
        return id & 0x0F;
    }

    function bit:5 length5()
    {
        return id % 32;
    }
};
