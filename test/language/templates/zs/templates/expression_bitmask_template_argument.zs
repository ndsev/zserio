package templates.expression_bitmask_template_argument;

bitmask uint8 Permission
{
    NONE = 0,
    READ,
    WRITE
};

struct BitmaskTemplateArgument<B>
{
    bool    boolField;
    int32   expressionField if valueof(B.NONE) == 0;
};

struct BitmaskTemplateArgumentHolder
{
    BitmaskTemplateArgument<Permission> bitmaskTemplateArgument;
};
