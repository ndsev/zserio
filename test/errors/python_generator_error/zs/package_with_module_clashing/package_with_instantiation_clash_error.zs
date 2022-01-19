package package_with_module_clashing.package_with_instantiation_clash_error;

import package_with_module_clashing.package_with_instantiation_clash_error.clashing_name.*;

union Clashing<T>
{
    Empty empty;
    T field;
};

struct Name
{
    uint32 field;
};

subtype Clashing<Name> ClashingNameSubtype;
