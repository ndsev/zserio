package wrong_field_usage_error;

struct WrongFieldReference
{
    uint8           extraData if data > 0;
    uint8           data;
};
