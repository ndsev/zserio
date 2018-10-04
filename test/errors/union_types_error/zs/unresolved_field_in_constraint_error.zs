package unresolved_field_in_constraint_error;

union UnresolvedFieldUnion
{
    int32 field1;
    int16 field2 : field2 < 10 && field1 > 10; // field1 is not allowed here! 
};
