package choice_types.default_empty_choice;

subtype int8  VariantA;
subtype int16 VariantB;

choice DefaultEmptyChoice(bit:3 tag) on tag
{
    case 1:
        VariantA  a;

    case 2:
        VariantB  b;

    default:
        //  empty
        ;
};
