package optional_members_warning.optional_references_in_constraint;

struct Container
{
    bool hasValue1;
    uint8 value1 if hasValue1 : value1 > 0; // no warning
    uint8 value2 if hasValue1 : value1 > 0; // no warning

    uint8 value3 : value1 > 1; // warning
    optional uint8 value4 : value1 > 1; // warning

    bool hasAnotherContainer;
    Container anotherContainer if hasAnotherContainer;
    uint8 zero = 0 if hasAnotherContainer;
    uint8 anotherValue if hasAnotherContainer : anotherContainer.anotherValue > zero; // warning!
};
