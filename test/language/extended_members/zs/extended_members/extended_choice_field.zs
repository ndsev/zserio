package extended_members.extended_choice_field;

struct Original
{
    uint32 numElements;
};

// tests extended parameterized type which can even become fully empty!
choice Choice(uint32 numElements) on numElements
{
    case 0:
        ;
    case 1:
        uint32 value;
    default:
        uint32 values[numElements];
};

struct Extended
{
    uint32 numElements;
    extend Choice(numElements) extendedValue;
};
