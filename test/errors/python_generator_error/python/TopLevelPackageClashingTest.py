import unittest

from testutils import compileErroneousZserio, assertErrorsPresent

class ApiClashingErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}
        compileErroneousZserio(__file__, "top_level_package_clashing/top_level_package_typing_clash_error.zs",
                               cls.errors, extraArgs=["-setTopLevelPackage", "typing"])

    def testTopLevelPackageTypingClash(self):
        assertErrorsPresent(self,
            "top_level_package_clashing/top_level_package_typing_clash_error.zs",
            [
                ":1:9: Top level package 'typing' clashes with Python 'typing' module " +
                "which is used by generated code.",
                "[ERROR] Python Generator: Top level package clash detected!"
            ]
        )
