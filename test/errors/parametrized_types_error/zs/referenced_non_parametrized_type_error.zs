package referenced_non_parametrized_type_error;

struct Item
{
    uint16      param;
    uint32      extraParam;
};

struct ItemHolder
{
    uint32          version;
    Item(version)   item;
};
