package complex_allocation;

import complex_allocation.allocation_choice.AllocationChoice;
import complex_allocation.allocation_union.AllocationUnion;
import complex_allocation.allocation_struct.AllocationStruct;
import complex_allocation.allocation_struct_optional.AllocationStructOptional;

// This schema should use all possible types which can allocate memory.
struct MainStructure
{
    string                      stringField;
    string                      stringArray[] : lengthof(stringArray) > 0;

    bool                        hasArray;
    AllocationChoice(hasArray)  choiceField;
    AllocationUnion             unionField;

    AllocationStruct            structField;
    AllocationStructOptional    structOptionalField;

    extern                      externalField;
    extern                      externalArray[] : lengthof(externalArray) > 0;

    function string constString()
    {
        return "This is constant string " + "longer than 32 bytes!";
    }

    function AllocationStructOptional constCompound()
    {
        return structOptionalField;
    }
};
