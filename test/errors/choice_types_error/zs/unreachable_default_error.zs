package unreachable_default_error;

choice UnreachableDefaultChoice(bool selector) on selector
{
    case true:
        uint16 uintValue;

    case false:
        bool boolValue;

    default:
        ;
};
