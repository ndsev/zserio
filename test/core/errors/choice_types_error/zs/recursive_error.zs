package recursive_error;

choice RecursiveChoice(int16 selector) on selector
{
    case 16:
        uint16 uint16Value;

    case 8:
        uint8 uint8Value;

    default:
        RecursiveChoice(0) recursiveValue;
};
