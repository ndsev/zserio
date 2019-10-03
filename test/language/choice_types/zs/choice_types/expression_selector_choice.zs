package choice_types.expression_selector_choice;

choice ExpressionSelectorChoice(uint16 tag) on tag + 1
{
    case 1:
        int8    field8;

    case 2:
        int16   field16;

    default:
        int32   field32;
};
