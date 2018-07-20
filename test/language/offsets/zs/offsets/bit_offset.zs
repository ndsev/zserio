package offsets.bit_offset;

struct BitOffset
{
    uint8   field1Offset;
    uint16  field2Offset;
    uint32  field3Offset;
    bit:8   field4Offset;
    bit:15  field5Offset;
    bit:18  field6Offset;
    bit:23  field7Offset;
    bit:8   field8Offset;
    // exactly 128 bits

field1Offset:
    bit:1   field1;

field2Offset:
    bit:2   field2;

field3Offset:
    bit:3   field3;

field4Offset:
    bit:4   field4;

field5Offset:
    bit:5   field5;

field6Offset:
    bit:6   field6;

field7Offset:
    bit:7   field7;

field8Offset:
    bit:8   field8;
};
