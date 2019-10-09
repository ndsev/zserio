package templates.struct_full_name_template_argument;

import templates.struct_full_name_template_argument.storage.*;

struct Storage
{
    string data;
};

struct TemplatedStruct<T>
{
    T storage;
};

struct StructFullNameTemplateArgument
{
    TemplatedStruct<templates.struct_full_name_template_argument.storage.Storage> structExternal;
    TemplatedStruct<Storage> structInternal;
};
