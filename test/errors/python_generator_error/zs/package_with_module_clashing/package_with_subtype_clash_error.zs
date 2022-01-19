package package_with_module_clashing.package_with_subtype_clash_error;

import package_with_module_clashing.package_with_subtype_clash_error.clashing_name.*;

enum uint8 SomeEnum
{
    ONE,
    TWO,
};

subtype SomeEnum ClashingName;
