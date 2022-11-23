package functions.structure_bytes;

struct Child
{
    bytes field;
};

struct TestStructure
{
    bytes field;
    Child child;

    function bytes getField()
    {
        return field;
    }

    function bytes getChildField()
    {
        return child.field;
    }
};
