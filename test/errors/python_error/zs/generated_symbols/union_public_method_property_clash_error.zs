package generated_symbols.union_public_method_property_clash_error;

union TestUnion
{
    string value1;
    uint32 bitsizeof; // clashes with generated API
};
