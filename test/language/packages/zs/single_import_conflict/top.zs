package single_import_conflict.top;

import single_import_conflict.imported.SimpleStructure;
import single_import_conflict.imported.SimpleParamStructure;
import single_import_conflict.imported.CONST_A;
import single_import_conflict.imported.CONST_B;

const uint32 CONST_A = 13;

struct SimpleParamStructure
{
    uint64 value;
};

struct TopStructure
{
    // both unqualified and fully qualified versions are ok
    SimpleStructure                                 simpleStructureShortName;
    single_import_conflict.imported.SimpleStructure simpleStructureFullName;

    // both must be fully qualified
    single_import_conflict.top.SimpleParamStructure simpleParamStructureLocal;
    single_import_conflict.imported.SimpleParamStructure(4) simpleParamStructureImported;

    // both unqualified and fully qualified versions are ok
    uint32 value1 = CONST_B;
    uint32 value2 = single_import_conflict.imported.CONST_B;

    // both must be fully qualified
    uint32 value3 = single_import_conflict.top.CONST_A;
    uint32 value4 = single_import_conflict.imported.CONST_A;
};
