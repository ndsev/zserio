package templates.expression_full_enum_template_argument;

import templates.expression_full_enum_template_argument.color.Color;

enum uint8 Color
{
    BLACK,
    WHITE
};

struct FullEnumTemplateArgument<E>
{
    bool    boolField;
    int32   expressionField if valueof(E.BLACK) == 0;
};

struct FullEnumTemplateArgumentHolder
{
    FullEnumTemplateArgument<Color> enumTemplateArgumentInternal;
    FullEnumTemplateArgument<templates.expression_full_enum_template_argument.color.Color> enumTemplateArgumentExternal;
};
