package bitmask_types.uint64_bitmask;

bitmask uint64 Permission
{
    // different naming styles are intended to check extensions which rename symbols (Python)
    nonePermission   = 0000b,
    READ_PERMISSION  = 0010b,
    write_permission = 0100b,
    CreatePermission = 1000b
};
