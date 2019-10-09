package templates.expression_enum_template_argument;

enum uint8 Color
{
    BLACK,
    WHITE
};

struct EnumTemplateArgument<E>
{
    bool    boolField;
    int32   expressionField if valueof(E.BLACK) == 0;
};

struct EnumTemplateArgumentHolder
{
    EnumTemplateArgument<Color> enumTemplateArgument;
};
