package referenced_enum_type_error;

enum uint32 Item
{
    ONE,
    TWO
};

struct ItemHolder
{
    uint32          version;
    Item(version)   item;
};
