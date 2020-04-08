package parameterized_struct_error;

// parameterized types cannot be deserialized correctly since parameters are not being written in the stream
struct Data(bit:8 size)
{
    int<size> value;
};

pubsub User
{
    subscribe topic("provider/data") Data data;
};
