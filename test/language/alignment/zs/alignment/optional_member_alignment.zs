package alignment.optional_member_alignment;

struct OptionalMemberAlignment
{
    bool hasOptional;

align(32):
    int32 optionalField if hasOptional == true;

    int32 field;
};
