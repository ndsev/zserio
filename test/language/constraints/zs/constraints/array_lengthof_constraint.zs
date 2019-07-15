package constraints.array_lengthof_constraint;

struct ArrayLengthofConstraint
{
    uint32 array[] : lengthof(array) > 5 && lengthof(array) < 10;
};
