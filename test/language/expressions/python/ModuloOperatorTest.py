import Expressions

class ModuloOperatorTest(Expressions.TestCase):
    def testIsValueDivBy4(self):
        moduleFunction = self.api.ModuloFunction()
        self.assertTrue(moduleFunction.is_value_div_by4())
