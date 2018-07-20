package alignment.auto_optional_member_alignment;

struct AutoOptionalMemberAlignment
{
align(32):
    optional int32 autoOptionalField;

    int32 field;
};
