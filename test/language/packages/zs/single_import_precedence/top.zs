package single_import_precedence.top;

import single_import_precedence.pkg.*;

import single_import_precedence.single.Structure;
import single_import_precedence.single.CONST_A;

// helper to check that Structure is really from the single import
struct Parameterized(Structure param)
{
    uint32 arr[param.value]; // only single.Structure has the value field
};

struct SingleImportPrecedence
{
    // both unqualified and fully qualified versions are ok
    Structure singleStructure1;
    single_import_precedence.single.Structure singleStructure2;
    Parameterized(singleStructure1) parameterized;

    // must be fully qualified
    single_import_precedence.pkg.Structure pkgStructure;

    // both unqualified and fully qualified versions are ok
    uint32 value1 = CONST_A;
    uint32 value2 = single_import_precedence.single.CONST_A;

    // must be fully qualified
    uint32 value3 = single_import_precedence.pkg.CONST_A;
};
