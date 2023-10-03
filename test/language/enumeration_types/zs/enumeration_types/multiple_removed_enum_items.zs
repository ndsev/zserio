package enumeration_types.multiple_removed_enum_items;

enum uint8 Traffic
{
    NONE = 1,
    @removed HEAVY,
    @removed LIGHT,
    @removed MID
};

struct AllocatorType // empty just to get allocator_type typedef in the test
{};
