package enumeration_types.deprecated_enum_item;

enum uint8 Traffic
{
    NONE = 1,
    @deprecated HEAVY,
    LIGHT,
    MID
};

struct AllocatorType // empty just to get allocator_type typedef in the test
{};
