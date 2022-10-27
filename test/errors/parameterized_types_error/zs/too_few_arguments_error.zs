package too_few_arguments_error;

struct Item(uint32 version, uint16 param)
{
    uint16      value;
    uint32      extraValue if version >= 10;
    bool        hasZeroValue if param == 0;
};

struct ItemHolder
{
    uint32          version;
    Item(version)   item;
};
