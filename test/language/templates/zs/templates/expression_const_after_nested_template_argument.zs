package templates.expression_const_after_nested_template_argument;

const uint32 SIZE = 3;

struct Element<T>
{
    T value;
};

struct Compound<T, S>
{
    T array[S];
};

struct ConstAfterNested
{
    Compound<Element<uint32>, SIZE> compound; // checks that SIZE is allowed here
};
