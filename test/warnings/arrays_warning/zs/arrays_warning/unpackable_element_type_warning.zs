package arrays_warning.unpackable_element_type_warning;

bitmask uint8 TestBitmask
{
    BLACK,
    WHITE
};

enum uint16 TestEnum
{
    ONE,
    TWO
};

struct UnpackableElementTypeWarning
{
    packed bool array1[]; // unpackable
    packed string array2[]; // unpackable
    packed float64 array3[]; // unpackable
    packed uint32 array4[];
    packed extern array5[]; // unpackable
    packed TestEnum array6[];
    packed TestBitmask array7[];
    packed bit:5 array8[];
    uint8 bitLength;
    packed int<bitLength> array9[];
};
