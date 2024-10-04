package implicit_array_with_auto_array_behind_error;

struct StructWithImplicit
{
    implicit uint32 array[];
};

struct ImplicitArrayWithAutoArrayBehindError
{
    StructWithImplicit structWithImplicit;
    uint32 array[]; // auto array needs to store size!
};
