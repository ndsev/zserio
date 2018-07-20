package functions.structure_array;

struct Item
{
    uint8       a;
    uint8       b;
};

struct StructureArray
{
    uint16      numElements;
    Item        values[numElements];
    uint16      pos;

    function Item getElement()
    {
        return values[pos];
    }
};
