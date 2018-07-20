package package_import.top;

import package_import.first.*;
import package_import.second.*;

struct Coordinate(uint8 constraintX, uint8 constraintY)
{
    uint32  x : x == constraintX;
    uint32  y : y == constraintY;
};

struct TopStructure
{
    /*
     * Test that unqualified Coordinate resolves to the local type. The imported types are not parametrized
     * with two parameters. So, if the tool resolves Coordinate to the wrong type, we get a compilation error.
     */
    Coordinate(1, 1) coordinateLocal;

    /*
     * Test that the type package_import.first.Coordinate can still be used when referred to with its fully
     * qualified name.
     */
    package_import.first.Coordinate coordinateImportedFirst;

    /*
     * Test that the type package_import.second.Coordinate can still be used when referred to with its fully
     * qualified name.
     */
    package_import.second.Coordinate(1) coordinateImportedSecond;
};
