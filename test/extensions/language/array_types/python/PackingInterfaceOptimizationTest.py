import os
import zserio

import ArrayTypes

from testutils import getApiDir, assertMethodPresent, assertMethodNotPresent


class PackingInterfaceOptimizationTest(ArrayTypes.TestCase):
    def testPackingInterfaceOptimizationMethods(self):
        self._assertPackingInterfaceMethodsNotPresent(self.api.PackingInterfaceOptimization)
        self.assertFalse(hasattr(self.api.PackingInterfaceOptimization, "ZserioPackingContext"))

    def testUnpackedColorsHolderMethods(self):
        self._assertPackingInterfaceMethodsNotPresent(self.api.UnpackedColorsHolder)
        self.assertFalse(hasattr(self.api.UnpackedColorsHolder, "ZserioPackingContext"))

        assertMethodPresent(self, self.api.UnpackedColorsHolder._ZserioElementFactory_unpacked_colors, "create")
        assertMethodNotPresent(
            self, self.api.UnpackedColorsHolder._ZserioElementFactory_unpacked_colors, "create_packing_context"
        )
        assertMethodNotPresent(
            self, self.api.UnpackedColorsHolder._ZserioElementFactory_unpacked_colors, "create_packed"
        )

        assertMethodPresent(self, self.api.UnpackedColorsHolder._ZserioElementFactory_mixed_colors, "create")
        assertMethodPresent(
            self, self.api.UnpackedColorsHolder._ZserioElementFactory_mixed_colors, "create_packing_context"
        )
        assertMethodPresent(
            self, self.api.UnpackedColorsHolder._ZserioElementFactory_mixed_colors, "create_packed"
        )

    def testPackedColorsHolderMethods(self):
        self._assertPackingInterfaceMethodsNotPresent(self.api.PackedColorsHolder)
        self.assertFalse(hasattr(self.api.PackedColorsHolder, "ZserioPackingContext"))

        assertMethodPresent(self, self.api.PackedColorsHolder._ZserioElementFactory_mixed_colors, "create")
        assertMethodPresent(
            self, self.api.PackedColorsHolder._ZserioElementFactory_mixed_colors, "create_packing_context"
        )
        assertMethodPresent(
            self, self.api.PackedColorsHolder._ZserioElementFactory_mixed_colors, "create_packed"
        )

        assertMethodPresent(self, self.api.PackedColorsHolder._ZserioElementFactory_packed_colors, "create")
        assertMethodPresent(
            self, self.api.PackedColorsHolder._ZserioElementFactory_packed_colors, "create_packing_context"
        )
        assertMethodPresent(
            self, self.api.PackedColorsHolder._ZserioElementFactory_packed_colors, "create_packed"
        )

    def testUnpackedColorStructMethods(self):
        self._assertPackingInterfaceMethodsNotPresent(self.api.UnpackedColorStruct)
        self.assertFalse(hasattr(self.api.UnpackedColorStruct, "ZserioPackingContext"))

    def testUnpackedColorChoiceMethods(self):
        self._assertPackingInterfaceMethodsNotPresent(self.api.UnpackedColorChoice)
        self.assertFalse(hasattr(self.api.UnpackedColorChoice, "ZserioPackingContext"))

    def testUnpackedColorUnionMethods(self):
        self._assertPackingInterfaceMethodsNotPresent(self.api.UnpackedColorUnion)
        self.assertFalse(hasattr(self.api.UnpackedColorUnion, "ZserioPackingContext"))

    def testUnpackedColorEnumMethods(self):
        userType = self.api.UnpackedColorEnum
        assertMethodNotPresent(self, userType, "from_reader_packed")
        assertMethodNotPresent(self, userType, "init_packing_context")
        assertMethodNotPresent(self, userType, "bitsizeof_packed")
        assertMethodNotPresent(self, userType, "initialize_offsets_packed")
        assertMethodNotPresent(self, userType, "write_packed")

    def testUnpackedColorBitmaskMethods(self):
        userType = self.api.UnpackedColorBitmask
        assertMethodNotPresent(self, userType, "from_reader_packed")
        assertMethodNotPresent(self, userType, "init_packing_context")
        assertMethodNotPresent(self, userType, "bitsizeof_packed")
        assertMethodNotPresent(self, userType, "initialize_offsets_packed")
        assertMethodNotPresent(self, userType, "write_packed")

    def testMixedColorStructMethods(self):
        self._assertPackingInterfaceMethodsPresent(self.api.MixedColorStruct)
        self.assertTrue(hasattr(self.api.MixedColorStruct, "ZserioPackingContext"))

    def testMixedColorChoiceMethods(self):
        self._assertPackingInterfaceMethodsPresent(self.api.MixedColorChoice)
        self.assertTrue(hasattr(self.api.MixedColorChoice, "ZserioPackingContext"))

    def testMixedColorUnionMethods(self):
        self._assertPackingInterfaceMethodsPresent(self.api.MixedColorUnion)
        self.assertTrue(hasattr(self.api.MixedColorUnion, "ZserioPackingContext"))

    def testMixedColorEnumMethods(self):
        userType = self.api.MixedColorEnum
        assertMethodPresent(self, userType, "from_reader_packed")
        assertMethodPresent(self, userType, "init_packing_context")
        assertMethodPresent(self, userType, "bitsizeof_packed")
        assertMethodPresent(self, userType, "initialize_offsets_packed")
        assertMethodPresent(self, userType, "write_packed")

    def testMixedColorBitmaskMethods(self):
        userType = self.api.MixedColorBitmask
        assertMethodPresent(self, userType, "from_reader_packed")
        assertMethodPresent(self, userType, "init_packing_context")
        assertMethodPresent(self, userType, "bitsizeof_packed")
        assertMethodPresent(self, userType, "initialize_offsets_packed")
        assertMethodPresent(self, userType, "write_packed")

    def testPackedColorStructMethods(self):
        self._assertPackingInterfaceMethodsPresent(self.api.PackedColorStruct)
        self.assertTrue(hasattr(self.api.PackedColorStruct, "ZserioPackingContext"))

    def testPackedColorChoiceMethods(self):
        self._assertPackingInterfaceMethodsPresent(self.api.PackedColorChoice)
        self.assertTrue(hasattr(self.api.PackedColorChoice, "ZserioPackingContext"))

    def testPackedColorUnionMethods(self):
        self._assertPackingInterfaceMethodsPresent(self.api.PackedColorUnion)
        self.assertTrue(hasattr(self.api.PackedColorUnion, "ZserioPackingContext"))

    def testPackedColorEnumMethods(self):
        userType = self.api.PackedColorEnum
        assertMethodPresent(self, userType, "from_reader_packed")
        assertMethodPresent(self, userType, "init_packing_context")
        assertMethodPresent(self, userType, "bitsizeof_packed")
        assertMethodPresent(self, userType, "initialize_offsets_packed")
        assertMethodPresent(self, userType, "write_packed")

    def testPackedColorBitmaskMethods(self):
        userType = self.api.PackedColorBitmask
        assertMethodPresent(self, userType, "from_reader_packed")
        assertMethodPresent(self, userType, "init_packing_context")
        assertMethodPresent(self, userType, "bitsizeof_packed")
        assertMethodPresent(self, userType, "initialize_offsets_packed")
        assertMethodPresent(self, userType, "write_packed")

    def testWriteReadFile(self):
        packingInterfaceOptimization = self.api.PackingInterfaceOptimization(
            self._createUnpackedColorsHolder(), self._createPackedColorsHolder()
        )

        zserio.serialize_to_file(packingInterfaceOptimization, self.BLOB_NAME)
        readPackingInterfaceOptimization = zserio.deserialize_from_file(
            self.api.PackingInterfaceOptimization, self.BLOB_NAME
        )
        self.assertEqual(packingInterfaceOptimization, readPackingInterfaceOptimization)

    def _createUnpackedColorsHolder(self):
        return self.api.UnpackedColorsHolder(self._createUnpackedColors(), self._createMixedColors())

    def _createPackedColorsHolder(self):
        return self.api.PackedColorsHolder(self._createMixedColors(), self._createPackedColors())

    def _createUnpackedColors(self):
        return [
            self.api.UnpackedColorStruct(True, self.api.UnpackedColorChoice(True, color_name_="yellow")),
            self.api.UnpackedColorStruct(
                False,
                self.api.UnpackedColorChoice(
                    False,
                    color_union_=self.api.UnpackedColorUnion(
                        color_bitmask_=(
                            self.api.UnpackedColorBitmask.Values.GREEN
                            | self.api.UnpackedColorBitmask.Values.RED
                        )
                    ),
                ),
            ),
            self.api.UnpackedColorStruct(
                False,
                self.api.UnpackedColorChoice(
                    False, color_union_=self.api.UnpackedColorUnion(color_enum_=self.api.UnpackedColorEnum.BLUE)
                ),
            ),
        ]

    def _createMixedColors(self):
        return [
            self.api.MixedColorStruct(True, self.api.MixedColorChoice(True, color_name_="purple")),
            self.api.MixedColorStruct(
                False,
                self.api.MixedColorChoice(
                    False,
                    color_union_=self.api.MixedColorUnion(
                        color_bitmask_=(
                            self.api.MixedColorBitmask.Values.BLUE | self.api.MixedColorBitmask.Values.GREEN
                        )
                    ),
                ),
            ),
            self.api.MixedColorStruct(
                False,
                self.api.MixedColorChoice(
                    False, color_union_=self.api.MixedColorUnion(color_enum_=self.api.MixedColorEnum.RED)
                ),
            ),
        ]

    def _createPackedColors(self):
        return [
            self.api.PackedColorStruct(True, self.api.PackedColorChoice(True, color_name_="grey")),
            self.api.PackedColorStruct(
                False,
                self.api.PackedColorChoice(
                    False,
                    color_union_=self.api.PackedColorUnion(
                        color_bitmask_=(
                            self.api.PackedColorBitmask.Values.BLUE | self.api.PackedColorBitmask.Values.RED
                        )
                    ),
                ),
            ),
            self.api.PackedColorStruct(
                False,
                self.api.PackedColorChoice(
                    False, color_union_=self.api.PackedColorUnion(color_enum_=self.api.PackedColorEnum.GREEN)
                ),
            ),
        ]

    def _assertPackingInterfaceMethodsPresent(self, userType):
        assertMethodPresent(self, userType, "from_reader_packed")
        assertMethodPresent(self, userType, "init_packing_context")
        assertMethodPresent(self, userType, "bitsizeof_packed")
        assertMethodPresent(self, userType, "initialize_offsets_packed")
        assertMethodPresent(self, userType, "read_packed")
        assertMethodPresent(self, userType, "write_packed")

    def _assertPackingInterfaceMethodsNotPresent(self, userType):
        assertMethodNotPresent(self, userType, "from_reader_packed")
        assertMethodNotPresent(self, userType, "init_packing_context")
        assertMethodNotPresent(self, userType, "bitsizeof_packed")
        assertMethodNotPresent(self, userType, "initialize_offsets_packed")
        assertMethodNotPresent(self, userType, "read_packed")
        assertMethodNotPresent(self, userType, "write_packed")

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "packing_interface_optimization.blob")
