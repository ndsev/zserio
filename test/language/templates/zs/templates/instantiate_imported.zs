package templates.instantiate_imported;

import templates.instantiate_imported.pkg.*;

struct Test<T>
{
    T value;
};

struct InstantiateImported
{
    Test<uint32> test32; // will be instantiated as U32 within the pkg package
    Test<string> testStr; // will be instantiated here using default name
};
