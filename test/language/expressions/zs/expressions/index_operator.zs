package expressions.index_operator;

choice Element(bool isEven) on isEven
{
    case true:
        uint8   field8;
    case false:
        int16   field16;
};

struct ElementList
{
    uint16                          length;
    Element(@index % 2 + 1 == 2)    elements[length];
};
