package optional_members_warning.optional_references_in_offset;

struct OffsetHolder
{
    uint32 offset;
};

struct OptionalOffsetHolder
{
    optional uint32 offset;
};

struct Container
{
    bool hasOffset;
    uint32 offset if hasOffset;
offset:
    uint8 value1 if hasOffset; // no warning

    optional uint32 optionalOffset;
optionalOffset:
    uint8 value2; // warning

    optional OffsetHolder offsetHolder;
offsetHolder.offset:
    uint8 value3; // warning

    OptionalOffsetHolder optionalOffsetHolder;
optionalOffsetHolder.offset:
    uint8 value4; // warning
};
