package unresolved_field_in_constraint_error;

choice UnresolvedFieldChoice(int16 selector) on selector
{
    case 16:
        uint16 uint16Value;

    case 8:
        uint8 uint8Value : uint8Value > 10 && uint16Value < 10; // uint16Value should not be available here!

    default:
        bool boolValue;
};
