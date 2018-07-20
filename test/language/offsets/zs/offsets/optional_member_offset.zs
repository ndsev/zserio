package offsets.optional_member_offset;

struct OptionalMemberOffset
{
    bool    hasOptional;
    uint32  optionalFieldOffset;

optionalFieldOffset:
    int32   optionalField if hasOptional == true;

    int32   field;
};
