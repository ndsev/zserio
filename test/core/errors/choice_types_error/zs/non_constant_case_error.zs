package non_constant_case_error;

struct Container
{
    uint16              tag;
    IntChoice(tag, 6)   content;
};

subtype int8  VariantA;
subtype int16 VariantB;
subtype int32 VariantC;

choice IntChoice(uint16 tag, uint16 value) on tag
{
    case 1:
        VariantA  a;

    case 2:
    case 3:
    case 4:
        VariantB  b;

    case value:         // 'value' cannot be used here even if it has correct type
    case 6:
        // empty
        ;

    default:
        VariantC  c;
};
