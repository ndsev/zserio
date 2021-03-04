package scope_symbols.pubsub_message_names_clash_error;

struct Int32Value
{
    int32 value;
};

struct UInt64Value
{
    uint64 value;
};

pubsub TestPubsub
{
    topic("test_pubsub/power_of_two_request") Int32Value power_of_two;
    topic("test_pubsub/power_of_two_response") UInt64Value powerOfTwo;
};
