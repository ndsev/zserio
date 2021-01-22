package errors.union_invalid_property_name_private_error;

union TestUnion
{
    uint32 _choice; // starts with '_' (and yet clashes with a private member)
    string str;
};
