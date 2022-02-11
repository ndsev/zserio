package optional_members_warning.optional_references_in_optional_clause;

struct Container
{
    bool hasValue1;
    uint8 value1 if hasValue1 == true;
    uint8 value2 if (hasValue1 == true) && (value1 > 0); // no warning
    uint8 value;
    uint8 value3 if value == 0 && hasValue1 == true && value1 > 0; // no warning
    uint8 value4 if hasValue1 == true && value == 0 && value1 > 0; // no warning
    uint8 value5 if ((hasValue1 == true && value == 0)) && value1 > 0; // no warning
    uint8 value6 if (hasValue1 == true) && (value1 > 0) && value2 > 0; // no warning

    uint8 value7 if value1 > 0 && (hasValue1 == true); // warning
    uint8 value8 if value1 > 0 && hasValue1 == true; // warning
    uint8 value9 if value1 > 0; // warning
};
