package field_case_error;

struct Container
{
    uint16          tag;
    IntChoice(tag)  content;
};

subtype int8  VariantA;
subtype int16 VariantB;
subtype int32 VariantC;

choice IntChoice(uint16 tag) on tag
{
    case 1:
        VariantA  a;

    case 2:
    case 3:
    case 4:
        VariantB  b;

    case b:         // 'b' cannot be used here
    case 6:
        // empty
        ;

    default:
        VariantC  c;
};
