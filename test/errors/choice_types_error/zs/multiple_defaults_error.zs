package multiple_defaults_error;

choice MultipleDefaultsChoice(int16 selector) on selector
{
    case 16:
        uint16 uint16Value;

    case 8:
        uint8 uint8Value;

    default:
        uint32 uint32Value;

    default:
        bool boolValue;
};
