package functions.choice_array;

struct Item
{
    uint8   valueA;
    uint8   valueB;
};

struct OuterArray
{
    uint16      numElements;
    Item        values[numElements];
    bit:1       dummy if numElements == 0;
};

struct Inner
{
    OuterArray                      outerArray;
    uint8                           isExplicit;
    ItemRef(isExplicit, outerArray) ref;
    int32                           extra if ref.getElement().valueA == 20;
};

choice ItemRef(uint8 isExplicit, OuterArray outerArray) on isExplicit
{
    case 1:
        Item      item;

    case 0:
        uint16    pos;

    function Item getElement()
    {
        return (isExplicit == 1) ? item : outerArray.values[getPosition()];
    }

    function uint16 getPosition()
    {
        return pos;
    }
};
