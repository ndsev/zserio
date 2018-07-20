package expressions.field_type;

struct ContainedType
{
    bool            needsExtraValue;
};

struct FieldTypeExpression
{
    ContainedType   containedType;
    bit:3           extraValue if containedType.needsExtraValue;
};
