package choice_type_constant_error;

choice Choice(int32 selector) on selector
{
    case 0:
        int32 zeroCase;
    default:
        int16 defaultCase;
};

const Choice choiceConst = 0;
