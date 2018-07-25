## Parametrized Types

The definition of a compound type may be augmented with a parameter list, similar to a parameter list in a Java
method declaration. Each item of the parameter list has a type and a name. Within the body of the compound type
definition, parameter names may be used as expressions of the corresponding type.

To use a parameterized type as a field type in another compound type, the parameterized type must be
instantiated with an argument list matching the types of the parameter list.

**Example**
```
struct Header
{
    uint32 version;
    uint16 numItems;
};

struct Message
{
    Header       header;
    Item(header) items[header.numItems];
};

struct Item(Header header)
{
    uint16 param;
    uint32 ExtraParam if header.version >= 10;
};

```

When the element type of an array is parameterized, a special notation can be used to pass different arguments
to each element of the array:

**Example**
```
struct Database
{
    uint16                  numBlocks;
    BlockHeader             headers[numBlocks];
    Block(headers[@index])  blocks[numBlocks];
};

struct BlockHeader
{
    uint16 numItems;
    uint32 offset;
};

struct Block(BlockHeader header)
{
    header.offset:
    int64 items[header.numItems];
};
```

The `@index` denotes the current index of the `blocks` array. The use of this expression in the argument list
for the `Block` reference indicates that the `i`-th element of the `blocks` array is of type `Block`
instantiated with the `i`-th header `headers[i]`.

[\[top\]](ZserioLanguageOverview.md#language-guide)
