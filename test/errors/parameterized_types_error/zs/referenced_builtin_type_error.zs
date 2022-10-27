package referenced_builtin_type_error;

subtype uint32 Item;

subtype Item Subtype;

struct ItemHolder
{
    uint32          version;
    Subtype(version)   item;
};
