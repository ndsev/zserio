package single_type_import.top;

import single_type_import.imported.SimpleStructure;
import single_type_import.imported.SimpleParamStructure;

struct SimpleParamStructure
{
    uint64 value;
};

struct TopStructure
{
    SimpleStructure                              simpleStructureShortName;
    single_type_import.imported.SimpleStructure  simpleStructureFullName;

    /*
     * Test that unqualified SimpleParamStructure resolves to the local type, not to
     * single_type_import.imported.SimpleParamStructure. The type
     * single_type_import.imported.SimpleParamStructure is parameterized. So, if the tool resolves
     * SimpleParamStructure to the wrong type, we get a compilation error.
     */
    SimpleParamStructure simpleParamStructureLocal;

    /*
     * Test that the type single_type_import.imported.SimpleParamStructure can still be used when referred to
     * with its fully qualified name.
     */
    single_type_import.imported.SimpleParamStructure(4) simpleParamStructureImported;
};
