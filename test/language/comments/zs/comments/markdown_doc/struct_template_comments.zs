package comments.markdown_doc.struct_template_comments;

/*!

**Field**

This defines template which accepts one template parameter.

*/
struct Field<T>
{
    /*! Example of the usage of template parameter. */
    T value;
};

/*!

**Compound**

Description of some compound type.

*/
struct Compound
{
    /*! Description of some field. */
    uint32 value;
};

/*!

**StructTemplatedField**

This structure uses template instantiations.

*/
struct StructTemplatedField
{
    /*! Template instantiation using template argument uint32. */
    Field<uint32>   uint32Field;

    /*! Template instantiation using template argument Compound. */
    Field<Compound> compoundField;

    /*! Template instantiation using template argument string. */
    Field<string>   stringField;
};
