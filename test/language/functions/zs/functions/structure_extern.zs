package functions.structure_extern;

struct Child
{
    extern field;
};

struct TestStructure
{
    extern field;
    Child child;

    function extern getField()
    {
        return field;
    }

    function extern getChildField()
    {
        return child.field;
    }
};
