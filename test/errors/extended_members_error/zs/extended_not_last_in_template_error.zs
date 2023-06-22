package extended_not_last_in_template_error;

struct Extended<T>
{
    uint32 field1;
    extend T field2;
    float32 field3 if field1 != 0;
};

instantiate Extended<string> ExtendedString;
