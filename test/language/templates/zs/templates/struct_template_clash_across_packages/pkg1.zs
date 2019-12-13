package templates.struct_template_clash_across_packages.pkg1;

import templates.struct_template_clash_across_packages.test_struct.*;

struct Test
{
    uint32 value;
};

struct InstantiationInPkg1
{
    TestStruct<Test> test;
};
