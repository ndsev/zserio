package templates.expression_const_template_argument;

const uint8 LENGTH = 10;

struct ConstTemplateArgument<C>
{
    uint8   array[C];
    int32   extraField if C == 10;
};

struct ConstTemplateArgumentHolder
{
    ConstTemplateArgument<LENGTH> constTemplateArgument;
};
