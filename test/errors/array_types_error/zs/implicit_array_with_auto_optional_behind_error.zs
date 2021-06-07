package implicit_array_with_auto_optional_behind_error;

struct StructWithImplicit
{
    implicit uint32 array[];
};

struct ImplicitArrayWithAutoOptionalBehindError
{
    StructWithImplicit structWithImplicit;
    optional string autoOptional; // auto optional needs to store a bool!
};
