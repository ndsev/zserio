package errors.union_public_method_property_clash_error;

union TestUnion
{
    string value1;
    uint32 bitSizeOf; // clashes with generated API
};
