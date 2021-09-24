package comments_warning.unresolved_see_tag_in_templated_struct;

/** @see unknown */
struct TemplatedStruct<T>
{
    bool hasField;
    T    field if hasField;
};

instantiate TemplatedStruct<string> TemplatedStructString;

// resolve step for the see tag unknown is called twice and it's what we need to test
instantiate TemplatedStruct<uint32> TemplatedStructUInt32; // only one instantiation is not enough!
