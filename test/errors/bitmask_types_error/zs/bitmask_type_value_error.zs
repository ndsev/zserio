package bitmask_type_value_error;

bitmask uint8 Permissions
{
    NONE       = 000b,
    READ       = 001b,
    WRITE      = OtherPermissions.WRITE
};

bitmask uint8 OtherPermissions
{
    NONE,
    READ,
    WRITE
};
