package multiple_cases_with_expression_error;

choice MultipleCasesChoice(int16 selector) on selector
{
    case 16:
        uint16 uint16Value;

    case 8:
        uint8 uint8Value;

    case 8 + 4 + 4:
        uint32 uint32Value;

    default:
        bool boolValue;
};
