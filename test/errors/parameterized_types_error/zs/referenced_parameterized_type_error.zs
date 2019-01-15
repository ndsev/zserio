package referenced_parameterized_type_error;

struct Item(uint32 version)
{
    uint16      param;
    uint32      extraParam if version >= 1;
};

struct ItemHolder
{
    uint32      version;
    Item        item;
};
