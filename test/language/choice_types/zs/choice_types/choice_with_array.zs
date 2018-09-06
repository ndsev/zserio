package choice_types.choice_with_array;

struct Data8
{
    int8 data;
};

choice TestChoice(int8 selector) on selector
{
    case 8:
        Data8 array8[]; // ObjectArray needs @SuppressWarning("unchecked")
    case 16:
        int16 array16[];
};
