// This is an example of default package without any package definition.

import default_package_import.top.*;

struct Child
{
    uint32 value;
};

/**
 * Test symbol reference resolving in default package.
 *
 * @see "Top package" default_package_import.top
 *
 * @see "Top structure" default_package_import.top.TopStructure
 *
 * @see TopStructure
 */
struct DefaultPackageStructure(uint8 numBits)
{
    bit<numBits>    value;
    TopStructure    topStructure;
    Child           childStructure;
};
