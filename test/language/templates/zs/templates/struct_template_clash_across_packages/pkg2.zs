package templates.struct_template_clash_across_packages.pkg2;

import templates.struct_template_clash_across_packages.test_struct.*;

struct Test
{
    string value;
};

struct InstantiationInPkg2
{
    TestStruct<Test> test;
};
