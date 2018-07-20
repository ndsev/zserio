package structure_type_constant_error;

struct Structure
{
    int32 field;
};

const Structure structConst = 2; // invalid compound type for constant
