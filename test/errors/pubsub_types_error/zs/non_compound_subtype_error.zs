package non_compound_subtype_error;

subtype int32 Data;

pubsub User
{
    subscribe("provider/data") Data data;
};
