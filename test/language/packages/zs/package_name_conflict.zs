package package_name_conflict;

import package_name_conflict.package_name_conflict.*;

struct Blob
{
    uint32 value;
};

struct PackageNameConflictLocal
{
    Blob blob;
};

struct PackageNameConflictImported
{
    package_name_conflict.package_name_conflict.Blob blob;
};
