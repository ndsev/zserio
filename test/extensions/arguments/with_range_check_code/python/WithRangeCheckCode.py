import unittest

from testutils import getZserioApi, getTestCaseName


class TestCase(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getattr(
            getZserioApi(__file__, "with_range_check_code.zs", extraArgs=["-withRangeCheckCode"]),
            getTestCaseName(cls.__name__),
        )
