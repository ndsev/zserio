package extended_not_top_level_error;

struct Extended
{
    uint32 field;
    extend uint32 extendedField;
};

struct TopLevel
{
    Extended extended;
};
