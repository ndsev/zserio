package bool_case_error;

choice BoolCaseChoice(bool selector) on selector
{
    case true:
        uint16 uintValue;

    case 0:
        bool boolValue;
};
