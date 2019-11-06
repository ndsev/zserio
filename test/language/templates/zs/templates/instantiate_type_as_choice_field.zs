package templates.instantiate_type_as_choice_field;

struct Test<T>
{
    T value;
};

choice InstantiateTypeAsChoiceField(bool param) on param
{
    case true:
        Test32 test;
    case false:
        uint32 value;
};

// define at the end to check correct instantiate type resolution
instantiate Test<uint32> Test32;
