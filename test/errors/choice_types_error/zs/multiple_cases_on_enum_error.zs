package multiple_cases_on_enum_error;

enum uint8 Enum
{
    ITEM1,
    ITEM2,
    ITEM3
};

choice MultipleCasesChoice(Enum selector) on selector
{
    case ITEM1:
        uint16 uint16Value;

    case ITEM2:
        uint8 uint8Value;

    case ITEM1:
        uint32 uint32Value;

    default:
        bool boolValue;
};
