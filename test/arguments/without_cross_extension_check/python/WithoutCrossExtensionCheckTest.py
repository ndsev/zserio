import unittest

from testutils import getZserioApi

class WithoutPubsubCodeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.invalid_in_java = getZserioApi(__file__, "invalid_in_java.zs",
                                           extraArgs=["-withoutCrossExtensionCheck"])
        cls.invalid_in_cpp = getZserioApi(__file__, "invalid_in_cpp.zs",
                                          extraArgs=["-withoutCrossExtensionCheck"])

    def testInvalidInJava(self):
        test = self.invalid_in_java.Test()
        self.assertEqual(13, test.abstract())

    def testInvalidInCpp(self):
        test = self.invalid_in_cpp.Test(dynamic_cast_="dynamic_cast")
        self.assertEqual("dynamic_cast", test.dynamic_cast)
