package instantiation_via_subtype_error;

struct TestStructure<T>
{
    T field;

    function uint32 getFieldValue()
    {
        return field.value;
    }
};

subtype TestStructure<uint32> InstantiationViaSubtypeError;
