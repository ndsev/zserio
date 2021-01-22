package errors.union_invalid_property_name_reserved_error;

union TestUnion
{
    string str;
    uint32 __hash__; // starts with '_'
};
