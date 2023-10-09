import os
import unittest

from testutils import getApiDir, getZserioApi, getTestCaseName

class TestCase(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getattr(getZserioApi(__file__, "sql_without_rowid_tables.zs"), getTestCaseName(cls.__name__))

class TestCaseWithDb(TestCase):
    @classmethod
    def setUpClass(cls):
        super(TestCaseWithDb, cls).setUpClass()
        cls.dbFileName = os.path.join(getApiDir(os.path.dirname(__file__)),
                                      getTestCaseName(cls.__name__) + "_test.sqlite")
