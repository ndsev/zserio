package functions.union_array;

struct Item
{
    uint8       a;
    uint8       b;
};

struct OuterArray
{
    uint16          numElements;
    Item            values[numElements];
    bit:1           dummy if numElements == 0;
};

struct Inner
{
    OuterArray          outerArray;
    ItemRef(outerArray) ref;
};

union ItemRef(OuterArray array)
{
    Item        item;
    uint16      pos;

    function Item getExplicitItem()
    {
        return item;
    }

    function Item getElement()
    {
        return array.values[getPosition()];
    }

    function uint16 getPosition()
    {
        return pos;
    }
};
