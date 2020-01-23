package cyclic_definition_using_constant_error;

const uint32 READ_PERM = 10b;

bitmask uint32 Permissions
{
    READ = READ_PERM, // OK, READ_PERM defined
    WRITE = WRITE_PERM
};

const uint32 WRITE_PERM = valueof(Permissions.WRITE); // cycle!
