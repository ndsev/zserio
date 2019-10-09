package instantiation_name_clash_across_packages_error.pkg2;

import instantiation_name_clash_across_packages_error.test_struct.*;

struct Test
{
    uint32 value;
};

struct InstantiationInPkg2
{
    TestStruct<Test> test;
};
