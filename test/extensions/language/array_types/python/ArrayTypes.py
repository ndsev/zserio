import unittest

from testutils import getZserioApi, getTestCaseName


class TestCase(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getattr(
            getZserioApi(__file__, "array_types.zs", expectedWarnings=15), getTestCaseName(cls.__name__)
        )
