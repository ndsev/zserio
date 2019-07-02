package functions.structure_parent_child_value;

struct ChildValue
{
    uint32      val;

    function uint32 getValue()
    {
        return val;
    }
};

struct ParentValue
{
    ChildValue      childValue;

    // This function has the same name as ChildValue has but there is no clash, so it must be ok.
    // Pythom emitter expression formatter had problem with this.
    function uint32 getValue()
    {
        return childValue.getValue();
    }
};
