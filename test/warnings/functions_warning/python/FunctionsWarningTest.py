import unittest

from testutils import getZserioApi, assertWarningsPresent

class FunctionsWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "functions_warning.zs", expectedWarnings=2, errorOutputDict=cls.warnings)

    def testOptionalReferencesInFunction(self):
        assertWarningsPresent(self,
            "functions_warning.zs",
            [
                "optional_references_in_function.zs:11:16: Function "
                "'suspicionFunction' contains reference to optional field 'additionalValue'."
            ]
        )

        assertWarningsPresent(self,
            "functions_warning.zs",
            [
                "optional_references_in_function.zs:16:16: Function "
                "'autoSuspicionFunction' contains reference to optional field 'autoAdditionalValue'."
            ]
        )
