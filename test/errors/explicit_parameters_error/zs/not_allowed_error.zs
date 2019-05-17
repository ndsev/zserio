package not_allowed_error;

struct Header
{
    uint32 count;
};

struct Blob(Header header)
{
    uint32 array[header.count];
};

struct BlobHolder
{
    Blob(explicit header) blob;
};
