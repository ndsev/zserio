package expressions.field_type_with_clash;

struct ContainedType
{
    bool            needsExtraValue;
};

struct FieldTypeExpression
{
    ContainedType   ContainedType;  // intended clash with type
    bit:3           extraValue if ContainedType.needsExtraValue;
};
