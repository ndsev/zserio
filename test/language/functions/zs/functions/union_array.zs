package functions.union_array;

struct Item
{
    uint8   valueA;
    uint8   valueB;
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
    uint16      position;

    // Intentionally named as getItem to check clashing with item getter name.
    function Item getItem()
    {
        return item;
    }

    function Item getElement()
    {
        return array.values[getPosition()];
    }

    // Intentionally named as getPosition to check clashing with position getter name.
    function uint16 getPosition()
    {
        return position;
    }
};
