package subtypes.bitmask_subtype;

subtype Permission PermissionSubtype;

bitmask uint16 Permission
{
    NONE = 0,
    READ,
    WRITE
};

const PermissionSubtype CONST_READ = PermissionSubtype.READ;
