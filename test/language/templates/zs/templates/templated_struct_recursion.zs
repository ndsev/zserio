package templates.templated_struct_recursion;

struct RecursiveTemplate<T>
{
    uint32 data[];
    RecursiveTemplate<T> recursiveTemplate if lengthof(data) > 0;
};

struct TemplatedStructRecursion
{
    RecursiveTemplate<uint32> recursiveTemplate;
};
