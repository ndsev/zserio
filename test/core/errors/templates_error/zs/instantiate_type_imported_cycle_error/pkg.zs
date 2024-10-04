package instantiate_type_imported_cycle_error.pkg;

struct Template<T>
{
    T field;
};

struct OtherTemplate<T>
{
    T field;
};
