package implicit_array_with_packed_array_behind_error;

struct StructWithImplicit
{
    implicit uint32 array[];
};

struct MaybeEmpty(bool empty)
{
    uint32 field1 if !empty;
    string field2 if !empty;
    uint32 array[] if !empty;
};

struct MayNotBeEmpty
{
    uint32 field1;
};

struct ImplicitArrayWithAutoOptionalBehindError
{
    bool empty;
    uint32 len;
    StructWithImplicit structWithImplicit;
    MaybeEmpty(true) array[1+len]; // ok, MaybeEmpty may be empty while the array has non-zero length
    packed MaybeEmpty(true) packedArray1[len]; // packed array, but the array can have zero length
    packed MaybeEmpty(true) packedArray2[1+len]; // packed array, may be empty while has non-zero length
    packed MayNotBeEmpty packedArray3[1+len]; // may not be empty, error
};
