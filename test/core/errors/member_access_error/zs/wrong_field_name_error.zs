package wrong_field_name_error;

struct WrongFieldReference
{
    uint8           data;
    uint8           extraData if wrongFieldName.useExtraData;
};
