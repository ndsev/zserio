import unittest

from testutils import getZserioApi

class WithPythonPropertiesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_python_properties.zs", extraArgs=["-withPythonProperties"])

    def testVersionAvailabilityMethods(self):
        userType = self.api.VersionAvailability

        self._assertMethodNotPresent(userType, "getValue")
        self._assertPropertyPresent(userType, "value", readOnly=True)

    def testExtraParamUnionMethods(self):
        userType = self.api.ExtraParamUnion

        self._assertMethodNotPresent(userType, "setValue16")
        self._assertMethodNotPresent(userType, "getValue16")
        self._assertMethodNotPresent(userType, "setValue32")
        self._assertMethodNotPresent(userType, "getValue32")

        self._assertPropertyPresent(userType, "value16", readOnly=False)
        self._assertPropertyPresent(userType, "value32", readOnly=False)

        self._assertPropertyPresent(userType, "choiceTag", readOnly=True)

    def testItemMethods(self):
        userType = self.api.Item

        self._assertMethodNotPresent(userType, "getItemType")
        self._assertMethodNotPresent(userType, "setParam")
        self._assertMethodNotPresent(userType, "getParam")
        self._assertMethodNotPresent(userType, "setExtraParam")
        self._assertMethodNotPresent(userType, "getExtraParam")

        self._assertMethodPresent(userType, "isExtraParamOptionalClauseMet")

        self._assertPropertyPresent(userType, "itemType", readOnly=True)
        self._assertPropertyPresent(userType, "param", readOnly=False)
        self._assertPropertyPresent(userType, "extraParam", readOnly=False)

    def testItemChoiceMethods(self):
        userType = self.api.ItemChoice

        self._assertMethodNotPresent(userType, "getHasItem")
        self._assertMethodNotPresent(userType, "setItem")
        self._assertMethodNotPresent(userType, "getItem")
        self._assertMethodNotPresent(userType, "setParam")
        self._assertMethodNotPresent(userType, "getParam")

        self._assertPropertyPresent(userType, "hasItem", readOnly=True)
        self._assertPropertyPresent(userType, "item", readOnly=False)
        self._assertPropertyPresent(userType, "param", readOnly=False)

    def testItemChoiceHolderMethods(self):
        userType = self.api.ItemChoiceHolder

        self._assertMethodNotPresent(userType, "setHasItem")
        self._assertMethodNotPresent(userType, "getHasItem")
        self._assertMethodNotPresent(userType, "setItemChoice")
        self._assertMethodNotPresent(userType, "getItemChoice")

        self._assertPropertyPresent(userType, "hasItem", readOnly=False)
        self._assertPropertyPresent(userType, "itemChoice", readOnly=False)

    def testTileMethods(self):
        userType = self.api.Tile

        self._assertMethodNotPresent(userType, "setVersionAvailability")
        self._assertMethodNotPresent(userType, "getVersionAvailability")
        self._assertMethodNotPresent(userType, "getVersion")
        self._assertMethodNotPresent(userType, "setVersion")
        self._assertMethodNotPresent(userType, "getVersionString")
        self._assertMethodNotPresent(userType, "setVersionString")
        self._assertMethodNotPresent(userType, "getNumElementsOffset")
        self._assertMethodNotPresent(userType, "setNumElementsOffset")
        self._assertMethodNotPresent(userType, "getNumElements")
        self._assertMethodNotPresent(userType, "setNumElements")
        self._assertMethodNotPresent(userType, "getOffsets")
        self._assertMethodNotPresent(userType, "setOffsets")
        self._assertMethodNotPresent(userType, "getData")
        self._assertMethodNotPresent(userType, "setData")

        self._assertMethodPresent(userType, "isVersionOptionalClauseMet")
        self._assertMethodPresent(userType, "isVersionStringOptionalClauseMet")

        self._assertPropertyPresent(userType, "versionAvailability", readOnly=False)
        self._assertPropertyPresent(userType, "version", readOnly=False)
        self._assertPropertyPresent(userType, "versionString", readOnly=False)
        self._assertPropertyPresent(userType, "numElementsOffset", readOnly=False)
        self._assertPropertyPresent(userType, "numElements", readOnly=False)
        self._assertPropertyPresent(userType, "offsets", readOnly=False)
        self._assertPropertyPresent(userType, "data", readOnly=False)

    # nothing to check for GeoMapTable

    def testWorldDbMethods(self):
        userType = self.api.WorldDb

        self._assertMethodNotPresent(userType, "getEurope")
        self._assertMethodNotPresent(userType, "getAmerica")

        self._assertPropertyPresent(userType, "europe", readOnly=True)
        self._assertPropertyPresent(userType, "america", readOnly=True)

    def testReadWriteWorldDB(self):
        worldDb = self.api.WorldDb.fromFile(":memory:")
        # sqlite3_db_filename returns NULL or empty string for in-memory databases
        self.assertFalse(worldDb.connection.filename)
        worldDb.createSchema()

        tiles = []
        for i in range(2):
            tile = self.api.Tile()
            tile.versionAvailability = self.api.VersionAvailability.Values.VERSION_NUMBER
            tile.version = i
            tile.numElements = NUM_ELEMENTS
            tile.offsets = [0] * NUM_ELEMENTS
            data = []
            for j in range(NUM_ELEMENTS):
                itemChoiceHolder = self.api.ItemChoiceHolder()
                itemChoiceHolder.hasItem = j % 2 == 0 # hasItem == True for even elements
                itemChoice = self.api.ItemChoice(itemChoiceHolder.hasItem)
                if itemChoiceHolder.hasItem:
                    item = self.api.Item(self.api.ItemType.WITH_EXTRA_PARAM)
                    item.param = PARAMS[j]
                    extraParam = self.api.ExtraParamUnion()
                    extraParam.value32 = EXTRA_PARAM
                    self.assertEqual(extraParam.choiceTag, self.api.ExtraParamUnion.CHOICE_value32)
                    item.extraParam = extraParam
                    self.assertTrue(item.isExtraParamOptionalClauseMet())
                    itemChoice.item = item
                else:
                    itemChoice.param = PARAMS[j]
                itemChoiceHolder.itemChoice = itemChoice
                data.append(itemChoiceHolder)
            tile.data = data
            tiles.append(tile)

        europeTiles = list(enumerate(tiles))
        worldDb.europe.write(europeTiles)
        americaTiles = list(enumerate(reversed(tiles)))
        worldDb.america.write(americaTiles)

        tileIdCol = 0
        tileCol = 1

        readWorldDb = self.api.WorldDb(worldDb.connection)
        readEuropeTiles = [(row[tileIdCol], row[tileCol]) for row in readWorldDb.europe.read()]
        self.assertEqual(europeTiles, readEuropeTiles)
        readAmericaTiles = [(row[tileIdCol], row[tileCol]) for row in readWorldDb.america.read()]
        self.assertEqual(americaTiles, readAmericaTiles)
        self.assertNotEqual(readEuropeTiles, readAmericaTiles)

    def _assertMethodNotPresent(self, userType, method):
        self.assertFalse(hasattr(userType, method),
                         msg=("Method '%s' is present in '%s'!" % (method, userType.__name__)))

    def _assertMethodPresent(self, userType, method):
        self.assertTrue(hasattr(userType, method),
                        msg=("Method '%s' is not present in '%s'!" % (method, userType.__name__)))

    def _assertPropertyPresent(self, userType, prop, *, readOnly):
        self.assertTrue(
            hasattr(userType, prop),
            msg=("Property '%s' is not present in '%s'!" % (prop, userType.__name__))
        )
        propAttr = getattr(userType, prop)
        self.assertTrue(
            isinstance(propAttr, property),
            msg=("Attribute '%s' is not a property in '%s'!" % (prop, userType.__name__))
        )
        self.assertIsNotNone(
            propAttr.fget,
            msg=("Property '%s' getter is not set in '%s'!" % (prop, userType.__name__))
        )
        if readOnly:
            self.assertIsNone(
                propAttr.fset,
                msg=("Read-only property '%s' setter is set in '%s'!" % (prop, userType.__name__))
            )
        else:
            self.assertIsNotNone(
                propAttr.fset,
                msg=("Property '%s' setter is not set in '%s'!" % (prop, userType.__name__))
            )


NUM_ELEMENTS = 2
PARAMS = [13, 21]
EXTRA_PARAM = 42
