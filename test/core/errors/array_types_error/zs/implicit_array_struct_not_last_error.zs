package implicit_array_struct_not_last_error;

struct StructWithImplicit
{
    string strField;
    implicit uint8 array[];
};

struct ImplicitArrayStructNotLastError
{
    StructWithImplicit structWithImplicit;
    uint32 anotherField;
};
