package union_types.union_compatibility_check;

struct CoordXY
{
    uint32 coordX;
    uint32 coordY;
};

union UnionVersion1
{
    CoordXY coordXY;
    string text;
};

struct CoordXYZ
{
    uint32 coordX;
    uint32 coordY;
    float64 coordZ;
};

union UnionVersion2
{
    CoordXY coordXY;
    string text;
    CoordXYZ coordXYZ;
};

// In this test we need to be able to:
// 1. write version 1 and read it using version 1
// 2. write version 1 and read it using version 2!
// 3. write version 2 without using added fields (coordXYZ) and read it using version 1!
// 4. write version 2 using any features and read it using version 2 :-)
struct UnionCompatibilityCheck<UNION>
{
    UNION array[];
    packed UNION packedArray[];
};

instantiate UnionCompatibilityCheck<UnionVersion1> UnionCompatibilityCheckVersion1;
instantiate UnionCompatibilityCheck<UnionVersion2> UnionCompatibilityCheckVersion2;
