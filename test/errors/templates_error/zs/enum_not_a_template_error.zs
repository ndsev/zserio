package enum_not_a_template_error;

enum uint32 Enumeration
{
    ONE,
    TWO
};

struct EnumNotATemplate
{
    Enumeration<uint32> value;
};
