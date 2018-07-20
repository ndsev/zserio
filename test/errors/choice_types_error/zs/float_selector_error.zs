package float_selector_error;

choice FloatSelectorChoice(float16 selector) on selector
{
    case 1.1:
        uint16  uint16Value;

    case 1.2:
        uint8   uint8Value;

    default:
        bool    boolValue;
};
