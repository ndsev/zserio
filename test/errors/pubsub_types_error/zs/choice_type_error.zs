package choice_type_error;

// choice is always parameterized!
choice Data(bit:8 size) on size
{
    case 16:
        int16 value16;
    case 32:
        int16 value32;
};

pubsub Provider
{
    publish("provider/data") Data data;
};
