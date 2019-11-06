package templates.instantiate_vs_default;

import templates.instantiate_vs_default.pkg.*; // contains Test<T>

struct InstantiateVsDefault
{
    Test<uint32> test32; // will be instantiated in pkg package using default name
    Test<string> testStr; // will be instantiated here as TStr
};

instantiate Test<string> TStr;
