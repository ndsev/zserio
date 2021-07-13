import unittest

from testutils import getZserioApi

class RulesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "rules.zs")

    def testRules(self):
        pass
