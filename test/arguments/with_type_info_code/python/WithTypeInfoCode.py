import unittest

from testutils import getZserioApi


class TestCase(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(
            __file__, "with_type_info_code.zs", extraArgs=["-withTypeInfoCode", "-allowImplicitArrays"]
        )
