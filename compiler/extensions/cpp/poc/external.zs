struct SimpleStructure
{
    bit:3     numberA;
    // this will automatically imply align(8) because of performance reasons
    bitstream externalStructure;
    bit:7     numberC;
};

struct ExternalStructure
{
    uint8     numberB;
};
