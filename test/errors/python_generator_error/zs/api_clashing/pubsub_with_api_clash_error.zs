package api_clashing.pubsub_with_api_clash_error;

const string PUBSUB_TOPIC_NAME = "api";

struct Int32Value
{
    int32 value;
};

struct UInt64Value
{
    uint64 value;
};

pubsub Api
{
    topic(PUBSUB_TOPIC_NAME + "/request") Int32Value request;
    topic(PUBSUB_TOPIC_NAME + "/power_of_two") UInt64Value powerOfTwo;
};

