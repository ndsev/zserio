package constraint_expression_expects_constant_error;

struct TestStruct<T>
{
    uint8 value : value < T; // T must be constnat
};

struct ConstraintExpressionExpectsConstant
{
    TestStruct<uint32> test;
};
