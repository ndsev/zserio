package bitmask_types.bitmask_used_by_bitmask;

bitmask bit:7 Permission
{
    NONE = 0,

    READ   = valueof(SimplePermission.READ),
    WRITE  = valueof(SimplePermission.WRITE),
    CREATE = valueof(SimplePermission.CREATE),
    DELETE,

    READ_ALL   = valueof(ComplexPermission.READ_ALL),
    WRITE_ALL  = valueof(ComplexPermission.WRITE_ALL),
    CREATE_ALL = valueof(ComplexPermission.CREATE_ALL),
    DELETE_ALL
};

// These bitmasks are defined after Permission intentionally to check (pre)evaluation of expressions.
bitmask bit:3 SimplePermission
{
    READ   = 0x02,
    WRITE  = 0x04,
    // The value expression is defined so complicated intentionally to check (pre)evaluation of expression tree.
    CREATE = 0x02 | 0x04
};

bitmask bit:5 ComplexPermission
{
    READ_ALL    = 0x11,
    WRITE_ALL   = 0x12,
    CREATE_ALL  = 0x14
};
