import unittest

from testutils import getZserioApi, assertWarningsPresent


class ChoiceTypesWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        getZserioApi(__file__, "choice_types_warning.zs", expectedWarnings=1, errorOutputDict=cls.warnings)

    def testOptionalReferencesInSelector(self):
        assertWarningsPresent(
            self,
            "choice_types_warning.zs",
            [
                "optional_references_in_selector.zs:8:41: Choice 'TestChoice' selector "
                "contains reference to optional field 'numBits'."
            ],
        )
