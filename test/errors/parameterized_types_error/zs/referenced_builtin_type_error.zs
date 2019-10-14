package referenced_builtin_type_error;

subtype uint32 Item;

struct ItemHolder
{
    uint32          version;
    Item(version)   item;
};
