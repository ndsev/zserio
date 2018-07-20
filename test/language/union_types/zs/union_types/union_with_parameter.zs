package union_types.union_with_parameter;

union TestUnion(bool case1Allowed)
{
    int32 case1Field : case1Allowed;
    int16 case2Field;
    int8  case3Field;
};
