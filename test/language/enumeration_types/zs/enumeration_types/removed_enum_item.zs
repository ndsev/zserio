package enumeration_types.removed_enum_item;

enum uint8 Traffic
{
    NONE = 1,
    @removed HEAVY,
    LIGHT,
    MID
};

struct AllocatorType // empty just to get allocator_type typedef in the test
{};
