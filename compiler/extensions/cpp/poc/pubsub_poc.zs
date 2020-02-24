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
    pubsub("pubsub/int32", Int32Value) request;
    subscribe("pubsub/powerOfTwo", UInt64Value) powerOfTwo;
    subscribe("pubsub/boolean/#", BoolValue) booleanResponse;
};

pubsub PowerOfTwoProvider
{
    subscribe("pubsub/request", Int32Value) request;
    publish("pubsub/powerOfTwo", UInt64Value) powerOfTwo;
};

pubsub PositivenessProvider
{
    subscribe("pubsub/request", Int32Value) request;
    publish("pubsub/boolean/positiveness", BoolValue) positiveness;
};

pubsub GreaterThanTenProvider
{
    subscribe("pubsub/request", Int32Value) request;
    publish("pubsub/boolean/greaterThanTen", BoolValue) greaterThanTen;
};
