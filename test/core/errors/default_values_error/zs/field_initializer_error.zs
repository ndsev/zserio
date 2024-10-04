package field_initializer_error;

struct FieldInitializer
{
    int8    field;
    int8    fieldInitializer = field;
};
