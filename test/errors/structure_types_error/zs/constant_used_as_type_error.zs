package constant_used_as_type_error;

const int32 ConstantUsedAsType = 10;

struct Item
{
    int32               param1;
    ConstantUsedAsType  param2;
};
