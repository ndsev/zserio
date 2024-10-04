import unittest

from testutils import getZserioApi, getTestCaseName


class TestCase(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getattr(getZserioApi(__file__, "offsets.zs"), getTestCaseName(cls.__name__))
