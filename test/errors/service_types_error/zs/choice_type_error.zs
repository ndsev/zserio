package choice_type_error;

struct Response
{
    uint64 value;
};

// choice is always parameterized!
choice Request(bit:8 size) on size
{
    case 16:
        int16 value16;
    case 32:
        int16 value32;
};

service Service
{
    rpc Response powerOfTwo(Request);
};
