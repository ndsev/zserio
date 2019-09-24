package templates.recursive_template;

struct Compound<T>
{
    T value;
};

struct RecursiveTemplate
{
    Compound<Compound<uint32>> compound1;
    Compound<Compound<Compound<string>>> compound2;
};
