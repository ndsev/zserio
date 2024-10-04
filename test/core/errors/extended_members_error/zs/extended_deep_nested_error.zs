package extended_deep_nested_error;

struct Extended
{
    uint32 field;
    extend string extendedField;
};

struct TopLevel
{
    uint8 param;
    Choice(param) choiceArray[];
};

union Union
{
    string fieldString;
    Extended fieldExtended[];
};

choice Choice(uint8 param) on param
{
    case 0:
        Union unionField[];
    default:
        ;
};
