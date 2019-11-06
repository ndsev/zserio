package templates.instantiate_not_imported.pkg;

struct Test<T>
{
    T value;
};

instantiate Test<uint32> U32; // not imported in the main zs, the Test will be instantiated twice!
