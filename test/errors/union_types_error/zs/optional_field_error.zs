package optional_field_error;

union OptionalFieldNotAllowed(int32 param)
{
    int32 field1;
    int16 field2 if param < 10;
};
