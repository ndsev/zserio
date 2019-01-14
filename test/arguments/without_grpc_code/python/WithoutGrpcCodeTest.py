import os
import pathlib

import unittest

from testutils import getZserioApi

class WithoutWriterCodeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "without_grpc_code.zs", extraArgs=["-withoutGrpcCode"])

    def testCheckService(self):
        self.assertFalse(hasattr(self.api, "Service"))
        self.assertFalse(self._isFilePresent("Service.py"))

    def testCheckResponse(self):
        self.assertTrue(hasattr(self.api, "Response"))
        self.assertTrue(self._isFilePresent("Response.py"))

    def testCheckRequest(self):
        self.assertTrue(hasattr(self.api, "Request"))
        self.assertTrue(self._isFilePresent("Request.py"))

    def _isFilePresent(self, filename):
        fileFullPath = os.path.join(os.path.abspath(os.path.join(self.api.__file__, "..")), filename)
        file = pathlib.Path(fileFullPath)
        return file.is_file()
