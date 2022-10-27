package too_many_arguments_error;

struct Item<T>(uint32 version)
{
    T value;
    uint32 extraValue if version >= 10;
};

instantiate Item<uint16> ItemU16;

subtype ItemU16 Subtype;

struct ItemHolder
{
    uint32 id;
    uint32 version;
    Subtype(id, version) item;
};
