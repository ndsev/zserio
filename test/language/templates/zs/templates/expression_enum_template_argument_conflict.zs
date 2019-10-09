package templates.expression_enum_template_argument_conflict;

enum uint8 Letters
{
    A,
    B,
    C,
    D,
    E
};

enum uint8 E
{
    BLACK,
    WHITE
};

struct EnumTemplateArgumentConflict<E>
{
    bool    boolField;
    int32   expressionField if valueof(E.E) == 4 &&
                    valueof(templates.expression_enum_template_argument_conflict.E.BLACK) == 0;
};

struct EnumTemplateArgumentConflictHolder
{
    EnumTemplateArgumentConflict<Letters> enumTemplateArgumentConflict;
};
