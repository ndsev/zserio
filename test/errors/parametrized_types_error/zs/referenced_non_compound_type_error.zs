package referenced_non_compound_type_error;

subtype uint32 Item;

struct ItemHolder
{
    uint32          version;
    Item(version)   item;
};
