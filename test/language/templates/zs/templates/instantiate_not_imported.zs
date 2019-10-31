package templates.instantiate_not_imported;

import templates.instantiate_not_imported.pkg.Test;

struct InstantiateNotImported
{
    Test<uint32> test32; // instantiate in pkg package using default name
    Test<string> testStr;
};
