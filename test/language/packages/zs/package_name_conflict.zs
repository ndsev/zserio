package package_name_conflict;

import package_name_conflict.package_name_conflict.*;

struct Blob
{
    uint32 value;
};

struct PackageNameConflict
{
    Blob blob;
};

struct PackageNameConflictInner
{
    package_name_conflict.package_name_conflict.Blob blob;
};
