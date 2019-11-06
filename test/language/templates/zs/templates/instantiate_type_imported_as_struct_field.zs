package templates.instantiate_type_imported_as_struct_field;

import templates.instantiate_type_imported_as_struct_field.pkg.Test32;

struct Test<T>
{
    T value;
};

struct InstantiateTypeImportedAsStructField
{
    Test32 test;
};
