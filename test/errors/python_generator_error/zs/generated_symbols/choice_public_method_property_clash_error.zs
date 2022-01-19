package generated_symbols.choice_public_method_property_clash_error;

choice TestChoice(uint32 param) on param
{
    case 1:
        bool write; // clashes with generated API
    default:
        ;
};
