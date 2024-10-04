package cyclic_definition_using_bitmask_value_error;

bitmask uint32 Permissions
{
    NONE  = 00b,
    READ  = 01b,
    WRITE = valueof(OtherPermissions.WRITE)
};

bitmask uint32 OtherPermissions
{
    OTHER_WRITE = valueof(Permissions.WRITE) // cycle!
};
