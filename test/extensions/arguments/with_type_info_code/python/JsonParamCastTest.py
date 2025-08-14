import os
import unittest
import zserio

from testutils import getApiDir
from testutils import getZserioApi


class JsonParamCastTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "json_param_cast.zs", extraArgs=["-withTypeInfoCode"])

    def testToJsonFile(self):
        holder = self.api.Holder(
            10,
            self.api.Int8Param(0, 1),
            self.api.Int16Param(0o30, 2),
            self.api.Int32Param(0x30, 3),
            self.api.Int64Param(0b111, 4),
            self.api.Float16Param(0.2, 5),
            self.api.Float32Param(0.2, 6),
            self.api.Float64Param(0.1, 7),
            self.api.Int32Param(10, 8),
        )
        zserio.to_json_file(holder, self.JSON_NAME_PARAM_CAST)

        readHolder = zserio.from_json_file(self.api.Holder, self.JSON_NAME_PARAM_CAST)
        self.assertEqual(holder, readHolder)

    JSON_NAME_PARAM_CAST = os.path.join(getApiDir(os.path.dirname(__file__)), "json_param_cast.json")
