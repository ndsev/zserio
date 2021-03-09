package package_with_module_clashing.package_with_instantiate_type_clash_error;

import package_with_module_clashing.package_with_instantiate_type_clash_error.clashing_name.*;

choice Some<T>(bool selector) on selector
{
    case true:
        T field;
    default:
        Empty empty;
};

struct Name
{
    uint32 field;
};

instantiate Some<Name> ClashingName;
