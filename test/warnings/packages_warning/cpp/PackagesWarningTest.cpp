#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "test_utils/ZserioErrorOutput.h"

class PackagesWarningTest : public ::testing::Test
{
protected:
    PackagesWarningTest()
    :   zserioWarnings("warnings/packages_warning")
    {}

    const test_utils::ZserioErrorOutput zserioWarnings;
};

TEST_F(PackagesWarningTest, optionalReferencesInSelector)
{
    const std::string warning = "duplicated_package_import_warning.zs:6:8: "
            "Duplicated import of package 'packages_warning.simple_database'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(PackagesWarningTest, duplicatedSingleTypeImport)
{
    const std::string warning = "duplicated_single_type_import_warning.zs:6:8: "
            "Duplicated import of 'packages_warning.simple_database.SimpleTable'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(PackagesWarningTest, packageImportOverwrite)
{
    const std::string warning = "package_import_overwrite_warning.zs:6:8: "
            "Import of package 'packages_warning.simple_database' overwrites single import of "
            "'packages_warning.simple_database.SimpleTable'.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}

TEST_F(PackagesWarningTest, singleTypeAlreadyImported)
{
    const std::string warning = "single_type_already_imported_warning.zs:6:8: "
            "Single import of 'packages_warning.simple_database.SimpleTable' "
            "already covered by package import.";
    ASSERT_TRUE(zserioWarnings.isPresent(warning));
}
