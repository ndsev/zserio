package constraints.union_constraints;

union UnionConstraints
{
    uint8   value8  : value8 != 0;
    uint16  value16 : value16 > 255;
};
