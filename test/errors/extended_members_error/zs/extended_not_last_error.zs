package extended_not_last_error;

struct Extended
{
    uint32 field1;
    extend string field2;
    float32 field3 if field1 != 0;
};
