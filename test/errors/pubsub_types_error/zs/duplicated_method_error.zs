package duplicated_method_error;

struct Response32
{
    uint32 value;
};

struct Response64
{
    uint64 value;
};

pubsub Provider
{
    publish topic("powerOfTwo") Response32 powerOfTwo;
    publish topic("powerOfTwo") Response64 powerOfTwo;
};
