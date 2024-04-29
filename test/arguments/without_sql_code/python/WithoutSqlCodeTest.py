import os
import pathlib

import unittest

from testutils import getZserioApi


class WithoutSqlCodeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "without_sql_code.zs", extraArgs=["-withoutSqlCode"])

    def testCheckGeoMapTable(self):
        self.assertFalse(hasattr(self.api, "GeoMapTable"))
        self.assertFalse(self._isFilePresent("geo_map_table.py"))

    def testCheckWorldDb(self):
        self.assertFalse(hasattr(self.api, "WorldDb"))
        self.assertFalse(self._isFilePresent("world_db.py"))

    def testCheckTile(self):
        self.assertTrue(hasattr(self.api, "Tile"))
        self.assertTrue(self._isFilePresent("tile.py"))

    def _isFilePresent(self, filename):
        fileFullPath = os.path.join(os.path.abspath(os.path.join(self.api.__file__, "..")), filename)
        file = pathlib.Path(fileFullPath)
        return file.is_file()
