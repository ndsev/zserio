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
    publish("powerOfTwo") Response32 powerOfTwo;
    publish("powerOfTwo") Response64 powerOfTwo;
};
