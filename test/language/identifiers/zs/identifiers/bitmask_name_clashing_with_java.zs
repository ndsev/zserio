package identifiers.bitmask_name_clashing_with_java;

// bitmask has toString method returning java.lang.String
bitmask uint8 String
{
    READ,
    WRITE
};

struct BitmaskNameClashingWithJava
{
    String stringField;
};
