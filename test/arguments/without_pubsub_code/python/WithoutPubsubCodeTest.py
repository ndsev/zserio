import os
import pathlib

import unittest

from testutils import getZserioApi


class WithoutPubsubCodeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "without_pubsub_code.zs", extraArgs=["-withoutPubsubCode"])

    def testCheckService(self):
        self.assertTrue(hasattr(self.api, "Service"))
        self.assertTrue(self._isFilePresent("service.py"))

    def testCheckResponse(self):
        self.assertTrue(hasattr(self.api, "Response"))
        self.assertTrue(self._isFilePresent("response.py"))

    def testCheckRequest(self):
        self.assertTrue(hasattr(self.api, "Request"))
        self.assertTrue(self._isFilePresent("request.py"))

    def testCheckPubsub(self):
        self.assertFalse(hasattr(self.api, "Pubsub"))
        self.assertFalse(self._isFilePresent("pubsub.py"))

    def _isFilePresent(self, filename):
        fileFullPath = os.path.join(os.path.abspath(os.path.join(self.api.__file__, "..")), filename)
        file = pathlib.Path(fileFullPath)
        return file.is_file()
