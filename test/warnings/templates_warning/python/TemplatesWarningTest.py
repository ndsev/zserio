import unittest

from testutils import getZserioApi, assertWarningsPresent

class TemplatesWarningTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.warnings = {}
        cls.api = getZserioApi(__file__, "templates_warning.zs",
                               extraArgs=["-withWarnings", "default-instantiation"],
                               expectedWarnings=5, errorOutputDict=cls.warnings)

    def testDefaultInstantiation(self):
        assertWarningsPresent(self,
            "templates_warning.zs",
            [
                "default_instantiation_warning.zs:15:5: "
                "Default instantiation of 'Template' as 'Template_uint32."
            ]
        )

        assertWarningsPresent(self,
            "templates_warning.zs",
            [
                "default_instantiation_warning.zs:17:5: "
                "Default instantiation of 'Subpackage1Template' as 'Subpackage1Template_string."
            ]
        )

        assertWarningsPresent(self,
            "templates_warning.zs",
            [
                "default_instantiation_warning.zs:19:5: "
                "Default instantiation of 'Subpackage2Template' as 'Subpackage2Template_string."
            ]
        )

        assertWarningsPresent(self,
            "templates_warning.zs",
            [
                "default_instantiation_warning.zs:20:5: "
                "    In instantiation of 'Subpackage3Template' required from here",
                "default_instantiation_subpackage3.zs:10:5: "
                "Default instantiation of 'Subpackage3InnerTemplate' as 'Subpackage3InnerTemplate_uint32."
            ]
        )
