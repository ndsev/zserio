import unittest

from testutils import getZserioApi


class CommentsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "comments.zs")

    def testComments(self):
        pass
