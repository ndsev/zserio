package pubsub_poc;

struct UInt64Value
{
    uint64 value;
};

struct Int32Value
{
    int32 value;
};

struct BoolValue
{
    bool value;
};

pubsub Client
{
    publish("pubsub/request") Int32Value request;
    subscribe("pubsub/powerOfTwo") UInt64Value powerOfTwo;
    subscribe("pubsub/boolean/#") BoolValue booleanResponse;

    // parameterized topic will look like following:
    // publish("pubsub/{id}/request/", int32 id) Int32Value request;
};

pubsub PowerOfTwoProvider
{
    subscribe("pubsub/request") Int32Value request;
    publish("pubsub/powerOfTwo") UInt64Value powerOfTwo;
};

pubsub PositivenessProvider
{
    subscribe("pubsub/request") Int32Value request;
    publish("pubsub/boolean/positiveness") BoolValue positiveness;
};

pubsub GreaterThanTenProvider
{
    subscribe("pubsub/request") Int32Value request;
    publish("pubsub/boolean/greaterThanTen") BoolValue greaterThanTen;
};
