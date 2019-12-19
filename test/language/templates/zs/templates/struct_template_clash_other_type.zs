package templates.struct_template_clash_other_type;

struct Test_uint32
{
    string value;
};

struct Test<T>
{
    T value;
};

struct InstantiationNameClashOtherType
{
    Test<uint32> value;
};
