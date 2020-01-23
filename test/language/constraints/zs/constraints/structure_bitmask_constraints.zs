package constraints.structure_bitmask_constraints;

bitmask bit:3 Availability
{
    COORD_X,
    COORD_Y,
    COORD_Z
};

struct StructureBitmaskConstraints
{
    Availability availability;
    uint8 coordX : coordX == ((availability & Availability.COORD_X) == Availability.COORD_X ? coordX : 0);
    uint8 coordY : coordY == ((availability & Availability.COORD_Y) == Availability.COORD_Y ? coordY : 0);
    uint8 coordZ : coordZ == ((availability & Availability.COORD_Z) == Availability.COORD_Z ? coordZ : 0);
};
