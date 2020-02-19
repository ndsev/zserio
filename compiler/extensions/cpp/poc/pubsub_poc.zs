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
    publish("pubsub/uint64", UInt64Value) uint64ValuePub;
    subscribe("pubsub/uint64", UInt64Value) uint64ValueSub;

    publish("pubsub/int32", Int32Value) int32ValuePub;
    subscribe("pubsub/int32", Int32Value) int32ValueSub;

    publish("pubsub/bool", BoolValue) boolValuePub;
    subscribe("pubsub/bool", BoolValue) boolValueSub;
};
