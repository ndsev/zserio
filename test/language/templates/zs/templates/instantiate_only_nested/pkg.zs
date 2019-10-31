package templates.instantiate_only_nested.pkg;

struct Nested<T>
{
    T value;
};

struct Test<T>
{
    Nested<T> value;
};
