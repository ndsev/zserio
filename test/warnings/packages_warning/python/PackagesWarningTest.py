import unittest

from testutils import getZserioApi, assertWarningsPresent

class PackagesWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "packages_warning.zs", expectedWarnings=4, errorOutputDict=cls.warnings)

    def testDuplicatedPackageImport(self):
        assertWarningsPresent(self,
            "packages_warning.zs",
            [
                "duplicated_package_import_warning.zs:6:8: "
                "Duplicated import of package 'packages_warning.simple_database'."
            ]
        )

    def testDuplicatedSingleTypeImport(self):
        assertWarningsPresent(self,
            "packages_warning.zs",
            [
                "duplicated_single_type_import_warning.zs:6:8: "
                "Duplicated import of 'packages_warning.simple_database.SimpleTable'."
            ]
        )

    def testPackageImportOverwrite(self):
        assertWarningsPresent(self,
            "packages_warning.zs",
            [
                "package_import_overwrite_warning.zs:6:8: "
                "Import of package 'packages_warning.simple_database' overwrites single import of "
                "'packages_warning.simple_database.SimpleTable'."
            ]
        )

    def testSingleTypeAlreadyImported(self):
        assertWarningsPresent(self,
            "packages_warning.zs",
            [
                "single_type_already_imported_warning.zs:6:8: "
                "Single import of 'packages_warning.simple_database.SimpleTable' "
                "already covered by package import."
            ]
        )
