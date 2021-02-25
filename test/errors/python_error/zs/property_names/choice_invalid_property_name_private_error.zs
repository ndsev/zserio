package property_names.choice_invalid_property_name_private_error;

choice TestChoice(uint32 param) on param
{
    case 0:
        uint32 _choice; // starts with '_' (and yet clashes with a private member)
    default:
        ;
};
