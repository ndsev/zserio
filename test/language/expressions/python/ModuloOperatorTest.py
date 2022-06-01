import unittest

from testutils import getZserioApi

class LengthOfOperatorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").modulo_operator

    def testIsValueDivBy4(self):
        moduleFunction = self.api.ModuloFunction()
        self.assertTrue(moduleFunction.is_value_div_by4())
