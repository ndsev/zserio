package indirect_recursion_error;

struct Item
{
    uint16      param;
    uint32      extraParam;
    ItemHolder  itemHolder;
};

struct ItemHolder
{
    uint32      version;
    Item        item;
};
