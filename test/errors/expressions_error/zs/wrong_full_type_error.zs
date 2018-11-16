package wrong_full_type_error;

struct WrongFullTypeError
{
    int32           data;
    SomeStructure   someStructure;
    bool            isTypeOk if wrong_full_type_error.someStructure.type == 3; // 'someStructure' is not type
};

struct SomeStructure
{
    uint8           type : type == 3;
    int32           data;
};
