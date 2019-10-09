package templates.struct_recursive_template;

struct Compound<T>
{
    T value;
};

struct StructRecursiveTemplate
{
    Compound<Compound<uint32>> compound1;
    Compound<Compound<Compound<string>>> compound2;
};
