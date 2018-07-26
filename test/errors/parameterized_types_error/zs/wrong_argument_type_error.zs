package wrong_argument_type_error;

struct Item(uint32 version)
{
    uint16      param;
    uint32      extraParam if version >= 10;
};

struct ItemHolder
{
    float16         version;
    Item(version)   item;
};
