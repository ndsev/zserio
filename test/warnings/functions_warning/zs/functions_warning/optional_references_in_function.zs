package functions_warning.optional_references_in_function;

struct SingleOptionalFieldFunction
{
    uint32 length;
    uint32 additionalValue if length != 0;
    optional uint32 autoAdditionalValue;

    function uint32 suspicionFunction()
    {
        return additionalValue / 2 + 1; // warning
    }

    function uint32 autoSuspicionFunction()
    {
        return autoAdditionalValue / 2 + 1; // warning
    }
};
