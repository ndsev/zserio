package choice_types.constant_in_choice_case;

const uint8 UINT8_CONST = 13;

choice ConstantInChoiceCase(uint8 selector) on selector
{
    case 0:
        uint32 zeroCase;
    case UINT8_CONST:
        uint8 constCase;
};
