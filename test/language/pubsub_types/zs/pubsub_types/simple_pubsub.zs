package pubsub_types.simple_pubsub;

const string PUBSUB_TOPIC_NAME = "simple_pubsub";

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
    publish topic(PUBSUB_TOPIC_NAME + "/power_of_two") UInt64Value powerOfTwo;
    subscribe topic(PUBSUB_TOPIC_NAME + "/request") Int32Value request;
};

pubsub SimplePubsubClient
{
    // intended to check string concatenation
    publish topic("simple_pubsub" + "/request") Int32Value request;
    // intended to check string concatenation
    subscribe topic("simple_pubsub/power_of_two") UInt64Value powerOfTwo;
};

// or just a single Pub/Sub
pubsub SimplePubsub
{
    topic(PUBSUB_TOPIC_NAME + "/request") Int32Value request;
    topic(PUBSUB_TOPIC_NAME + "/power_of_two") UInt64Value powerOfTwo;
};
