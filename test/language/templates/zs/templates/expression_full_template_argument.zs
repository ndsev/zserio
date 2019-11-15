package templates.expression_full_template_argument;

import templates.expression_full_template_argument.color.*;

enum uint8 Color
{
    BLACK,
    WHITE
};

struct FullTemplateArgument<E>
{
    bool    boolField;
    int32   expressionField if valueof(E.BLACK) == 0;
};

struct FullTemplateArgumentHolder
{
    FullTemplateArgument<Color> templateArgumentInternal;
    FullTemplateArgument<templates.expression_full_template_argument.color.Color> templateArgumentExternal;
};
