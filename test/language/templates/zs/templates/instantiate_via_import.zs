package templates.instantiate_via_import;

import templates.instantiate_via_import.pkg.*;

struct InstantiateViaImport
{
    Test<uint32> test32; // will be instantiated as U32 within the pkg package
    Test<string> testStr; // will be instantiated in pkg package using default name
};
