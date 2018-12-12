package unreachable_default_multicase_error;

choice UnreachableDefaultMulticaseChoice(bool selector) on selector
{
    case true:
    case false:
        bool boolValue;

    default:
        ;
};
