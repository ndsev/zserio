package string_selector_error;

choice StringSelectorChoice(string selector) on selector
{
    case "Text16":
        uint16  uint16Value;

    case "Test8":
        uint8   uint8Value;

    default:
        bool    boolValue;
};
