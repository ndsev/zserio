import unittest

from testutils import compileErroneousZserio, assertErrorsPresent

class PackageWithModuleClashingErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}

        compileErroneousZserio(__file__, "package_with_module_clashing/package_with_bitmask_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "package_with_module_clashing/package_with_constant_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "package_with_module_clashing/package_with_enum_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__,
                               "package_with_module_clashing/package_with_instantiate_type_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__,
                               "package_with_module_clashing/package_with_instantiation_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "package_with_module_clashing/package_with_pubsub_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "package_with_module_clashing/package_with_service_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "package_with_module_clashing/package_with_structure_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "package_with_module_clashing/package_with_subtype_clash_error.zs",
                               cls.errors)

    def testPackageWithBitmaskClash(self):
        assertErrorsPresent(self,
            "package_with_module_clashing/package_with_bitmask_clash_error.zs",
            [
                ":5:15: Module " +
                "'package_with_module_clashing.package_with_bitmask_clash_error.clashing_name' " +
                "generated for package symbol 'ClashingName' clashes with equally named generated package!",
                "[ERROR] Python Generator: Package with module name clashing detected!"
            ]
        )

    def testPackageWithConstantClash(self):
        assertErrorsPresent(self,
            "package_with_module_clashing/package_with_constant_clash_error.zs",
            [
                ":5:14: Module " +
                "'package_with_module_clashing.package_with_constant_clash_error.clashing_name' " +
                "generated for package symbol 'CLASHING_NAME' clashes with equally named generated package!",
                "[ERROR] Python Generator: Package with module name clashing detected!"
            ]
        )

    def testPackageWithEnumClash(self):
        assertErrorsPresent(self,
            "package_with_module_clashing/package_with_enum_clash_error.zs",
            [
                ":5:12: Module " +
                "'package_with_module_clashing.package_with_enum_clash_error.clashing_name' " +
                "generated for package symbol 'ClashingName' clashes with equally named generated package!",
                "[ERROR] Python Generator: Package with module name clashing detected!"
            ]
        )

    def testPackageWithInstantiateTypeClash(self):
        assertErrorsPresent(self,
            "package_with_module_clashing/package_with_instantiate_type_clash_error.zs",
            [
                ":18:13: In instantiation of 'Some' required from here",
                ":5:8: Module " +
                "'package_with_module_clashing.package_with_instantiate_type_clash_error.clashing_name' " +
                "generated for package symbol 'ClashingName' clashes with equally named generated package!",
                "[ERROR] Python Generator: Package with module name clashing detected!"
            ]
        )

    def testPackageWithInstantiationClash(self):
        assertErrorsPresent(self,
            "package_with_module_clashing/package_with_instantiation_clash_error.zs",
            [
                ":16:9: In instantiation of 'Clashing' required from here",
                ":5:7: Module " +
                "'package_with_module_clashing.package_with_instantiation_clash_error.clashing_name' " +
                "generated for package symbol 'Clashing_Name' clashes with equally named generated package!",
                "[ERROR] Python Generator: Package with module name clashing detected!"
            ]
        )

    def testPackageWithPubsubClash(self):
        assertErrorsPresent(self,
            "package_with_module_clashing/package_with_pubsub_clash_error.zs",
            [
                ":5:8: Module " +
                "'package_with_module_clashing.package_with_pubsub_clash_error.clashing_name' " +
                "generated for package symbol 'ClashingName' clashes with equally named generated package!",
                "[ERROR] Python Generator: Package with module name clashing detected!"
            ]
        )

    def testPackageWithServiceClash(self):
        assertErrorsPresent(self,
            "package_with_module_clashing/package_with_service_clash_error.zs",
            [
                ":5:9: Module " +
                "'package_with_module_clashing.package_with_service_clash_error.clashing_name' " +
                "generated for package symbol 'ClashingName' clashes with equally named generated package!",
                "[ERROR] Python Generator: Package with module name clashing detected!"
            ]
        )

    def testPackageWithStructureClash(self):
        assertErrorsPresent(self,
            "package_with_module_clashing/package_with_structure_clash_error.zs",
            [
                ":5:8: Module " +
                "'package_with_module_clashing.package_with_structure_clash_error.clashing_name' " +
                "generated for package symbol 'ClashingName' clashes with equally named generated package!",
                "[ERROR] Python Generator: Package with module name clashing detected!"
            ]
        )

    def testPackageWithSubtypeClash(self):
        assertErrorsPresent(self,
            "package_with_module_clashing/package_with_subtype_clash_error.zs",
            [
                ":11:18: Module " +
                "'package_with_module_clashing.package_with_subtype_clash_error.clashing_name' " +
                "generated for package symbol 'ClashingName' clashes with equally named generated package!",
                "[ERROR] Python Generator: Package with module name clashing detected!"
            ]
        )
