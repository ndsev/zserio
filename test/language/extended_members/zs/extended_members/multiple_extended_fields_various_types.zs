package extended_members.multiple_extended_fields_various_types;

struct Original
{
    int:7 value;
};

struct Extended1
{
    int:7 value;
    extend optional uint32 extendedValue1;
    extend extern extendedValue2;
    extend bytes extendedValue3;
};

union Union(varsize arrayLen)
{
    uint32 valueU32;
    extern valueExtern;
    string valueStringArray[arrayLen];
};

struct Extended2
{
    int:7 value;
    extend optional uint32 extendedValue1;
    extend extern extendedValue2;
    extend bytes extendedValue3;
    extend optional bytes extendedValue4[];
    extend varsize extendedValue5;
    extend string extendedValue6[extendedValue5];
    extend Union(extendedValue5) extendedValue7;
    extend optional Union(extendedValue5) extendedValue8[];
    extend bit<extendedValue5> extendedValue9 if extendedValue5 != 0;
};
