package comments.classic_doc.pubsub_comments;

/** Structure used for request topic interface. */
struct Int32Value
{
    /** Value which is used as an input for power_of_two topic interface. */
    int32 value;
};

/** Structure used for power_of_two topic interface. */
struct UInt64Value
{
    /** Value which contains the power of two of the value from request. */
    uint64 value;
};

/** Definition of the simple pubsub inteface. */
pubsub SimplePubsub
{
    /** Topic to pass the request. */
    topic("simple_pubsub/request") Int32Value request;

    /** Topic to publish power of two of the value from request. */
    topic("simple_pubsub/power_of_two") UInt64Value powerOfTwo;
};
