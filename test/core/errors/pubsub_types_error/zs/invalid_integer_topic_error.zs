package invalid_integer_topic_error;

struct Response32
{
    uint32 value;
};

pubsub Provider
{
    publish topic(10 + 10) Response32 powerOfTwo;
};
