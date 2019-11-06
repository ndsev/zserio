package templates.instantiate_only_nested;

import templates.instantiate_only_nested.pkg.*;

struct InstantiateOnlyNested
{
    Test<uint32> test32; // Test will be generated in pkg package while Nested will be generated here as N32
};

instantiate Nested<uint32> N32;
