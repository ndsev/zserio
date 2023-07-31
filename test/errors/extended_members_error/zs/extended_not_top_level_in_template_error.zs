package extended_not_top_level_in_template_error;

struct Extended<T>
{
    uint32 field;
    extend T extendedField;
};

struct TopLevel
{
    Extended<uint32> extended;
};
