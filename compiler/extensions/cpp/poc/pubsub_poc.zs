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

pubsub SimplePubSub
{
    publish("pubsub/uint64", UInt64Value) uint64Value;

    publish("pubsub/int32", Int32Value) int32ValueOut;
    subscribe("pubsub/int32", Int32Value) int32ValueIn;

    subscribe("pubsub/bool", BoolValue)   boolValue;
};

/*
pubsub SimplePubSub
{
    publish("pubsub/uint64", UInt64Value) uint64Value;

    subscribe("pubsub/int32", Int32Value) int32Value;
    subscribe("pubsub/bool", BoolValue)   boolValue;
};

pubsub SimplePubSub
{
    publish   uint64Value("pubsub/uint64", UInt64Value);

    subscribe int32Value("pubsub/int32", Int32Value);
    subscribe boolValue("pubsub/bool", BoolValue);
};

pubsub SimplePubSub
{
    publish UInt64Value  uint64Value("pubsub/uint64");

    subscribe Int32Value int32Value("pubsub/int32");
    subscribe BoolValue  boolValue("pubsub/bool");
};
*/
