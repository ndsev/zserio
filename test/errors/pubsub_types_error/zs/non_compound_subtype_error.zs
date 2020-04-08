package non_compound_subtype_error;

subtype int32 Data;

pubsub User
{
    subscribe topic("provider/data") Data data;
};
