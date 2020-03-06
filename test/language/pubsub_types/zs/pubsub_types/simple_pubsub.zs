package pubsub_types.simple_pubsub;

struct Int32Value
{
    int32 value;
};

struct UInt64Value
{
    uint64 value;
};

// separated provider and client
pubsub SimplePubsubProvider
{
    publish("simple_pubsub/power_of_two") UInt64Value powerOfTwo;
    subscribe("simple_pubsub/request") Int32Value request;
};

pubsub SimplePubsubClient
{
    publish("simple_pubsub/request") Int32Value request;
    subscribe("simple_pubsub/power_of_two") UInt64Value powerOfTwo;
};

// or just a single Pub/Sub
pubsub SimplePubsub
{
    pubsub("simple_pubsub/request") Int32Value request;
    pubsub("simple_pubsub/power_of_two") UInt64Value powerOfTwo;
};
