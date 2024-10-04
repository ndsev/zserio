package extended_nested_in_choice_error;

struct Extended
{
    uint32 field;
    extend string extendedField;
};

choice Choice(bool param) on param
{
    case true:
        uint32 field;
    default:
        Extended extended;
};
