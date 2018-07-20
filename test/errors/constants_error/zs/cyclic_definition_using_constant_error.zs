package cyclic_definition_using_constant_error;

const int32 ConstantA = ConstantB; // OK, constant defined later
const int32 ConstantB = 42;
const int32 ConstantC = ConstantB; // OK, constant defined before

const int32 ConstantD = ConstantE;
const int32 ConstantE = ConstantD; // cycle!
