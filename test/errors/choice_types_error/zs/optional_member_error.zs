package optional_member_error;

choice BoolCaseChoice(bool selector) on selector
{
    case true:
        uint16  uintValue   if selector == false;

    case 0:
        bool    boolValue;
};
