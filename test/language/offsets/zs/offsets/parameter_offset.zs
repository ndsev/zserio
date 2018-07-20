package offsets.parameter_offset;

struct OffsetHolder
{
    uint32              roomOffset;
};

struct School
{
    uint16              schoolId;
    OffsetHolder        offsetHolder;
    Room(offsetHolder)  room;
};

struct Room(OffsetHolder offsetHolder)
{
offsetHolder.roomOffset:
    uint16              roomId;
};
