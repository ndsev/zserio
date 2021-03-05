import os
import pathlib

import unittest

from testutils import getZserioApi

class WithoutServiceCodeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "without_service_code.zs", extraArgs=["-withoutServiceCode"])

    def testCheckService(self):
        self.assertFalse(hasattr(self.api, "Service"))
        self.assertFalse(self._isFilePresent("service.py"))

    def testCheckResponse(self):
        self.assertTrue(hasattr(self.api, "Response"))
        self.assertTrue(self._isFilePresent("response.py"))

    def testCheckRequest(self):
        self.assertTrue(hasattr(self.api, "Request"))
        self.assertTrue(self._isFilePresent("request.py"))

    def testCheckPubsub(self):
        self.assertTrue(hasattr(self.api, "Pubsub"))
        self.assertTrue(self._isFilePresent("pubsub.py"))

    def _isFilePresent(self, filename):
        fileFullPath = os.path.join(os.path.abspath(os.path.join(self.api.__file__, "..")), filename)
        file = pathlib.Path(fileFullPath)
        return file.is_file()
