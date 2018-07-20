package wrong_number_of_arguments_error;

struct Item(uint32 version, uint16 value)
{
    uint16      param;
    uint32      extraParam if version >= 10;
    bool        hasZeroValue if value == 0;
};

struct ItemHolder
{
    uint32          version;
    Item(version)   item;
};
