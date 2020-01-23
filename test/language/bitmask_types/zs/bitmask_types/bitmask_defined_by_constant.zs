package bitmask_types.bitmask_defined_by_constant;

const uint32 READ_PERMISSION = 2;

bitmask uint32 Permission
{
    NONE = 0,
    READ = READ_PERMISSION,
    WRITE
};
