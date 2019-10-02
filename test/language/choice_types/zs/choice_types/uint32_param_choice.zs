package choice_types.uint32_param_choice;

subtype int8  VariantA;
subtype int16 VariantB;
subtype int32 VariantC;

// Type uint32 has been chosen intentionally because of Java (switch on long is not supported).
choice UInt32ParamChoice(uint32 selector) on selector
{
    case 1:
        VariantA  a;

    case 2: 
    case 3:
    case 4:
        VariantB  b;

    case 5:
    case 6:
        // empty
        ;

    default:
        VariantC  c;
};
