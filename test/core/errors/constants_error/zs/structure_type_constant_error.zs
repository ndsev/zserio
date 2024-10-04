package structure_type_constant_error;

struct Structure
{
    int32 field;
};

const Structure StructConst = 2; // invalid compound type for constant
