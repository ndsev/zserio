package symbol_with_type_name_clash_error;

struct TestStruct<T>
{
    T Field;
};

struct Field
{
    uint32 value;
};

struct SymbolWithTypeNameClashError
{
    TestStruct<Field> test;
};
