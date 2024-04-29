import os
import json
import zserio

import WithTypeInfoCode

from WithTypeInfoCodeCreator import createWithTypeInfoCode

from testutils import getApiDir


class DebugStringTest(WithTypeInfoCode.TestCase):
    def testJsonWriterWithOptionals(self):
        withTypeInfoCode = createWithTypeInfoCode(self.api, createOptionals=True)
        withTypeInfoCode.initialize_offsets()
        zserio.to_json_file(withTypeInfoCode, self.JSON_NAME_WITH_OPTIONALS)

        self._checkWithTypeInfoCodeJson(self.JSON_NAME_WITH_OPTIONALS, createdOptionals=True)

        readWithTypeInfoCode = zserio.from_json_file(self.api.WithTypeInfoCode, self.JSON_NAME_WITH_OPTIONALS)
        self.assertEqual(withTypeInfoCode, readWithTypeInfoCode)

    def testJsonWriterWithoutOptionals(self):
        withTypeInfoCode = createWithTypeInfoCode(self.api, createOptionals=False)
        withTypeInfoCode.initialize_offsets()
        zserio.to_json_file(withTypeInfoCode, self.JSON_NAME_WITHOUT_OPTIONALS)

        self._checkWithTypeInfoCodeJson(self.JSON_NAME_WITHOUT_OPTIONALS, createdOptionals=False)

        readWithTypeInfoCode = zserio.from_json_file(
            self.api.WithTypeInfoCode, self.JSON_NAME_WITHOUT_OPTIONALS
        )
        self.assertEqual(withTypeInfoCode, readWithTypeInfoCode)

    def testJsonWriterWithArrayLengthFilter(self):
        withTypeInfoCode = createWithTypeInfoCode(self.api)
        withTypeInfoCode.initialize_offsets()
        for i in range(11):
            jsonFileName = self._getJsonNameWithArrayLengthFilter(i)
            with open(jsonFileName, "w", encoding="utf-8") as jsonFile:
                walkFilter = zserio.ArrayLengthWalkFilter(i)
                walker = zserio.Walker(zserio.JsonWriter(text_io=jsonFile, indent=4), walkFilter)
                walker.walk(withTypeInfoCode)
            self._checkWithTypeInfoCodeJson(jsonFileName, maxArrayLength=i)

            with open(jsonFileName, "r", encoding="utf-8") as jsonFile:
                jsonReader = zserio.JsonReader(text_io=jsonFile)
                readWithTypeInfoCode = jsonReader.read(self.api.WithTypeInfoCode.type_info())
                self._checkWithTypeInfoCodeArrayLength(readWithTypeInfoCode, i)

    def testJsonWriterWithDepth0Filter(self):
        withTypeInfoCode = createWithTypeInfoCode(self.api)
        withTypeInfoCode.initialize_offsets()
        with open(self.JSON_NAME_WITH_DEPTH0_FILTER, "w", encoding="utf-8") as jsonFile:
            walkFilter = zserio.DepthWalkFilter(0)
            walker = zserio.Walker(zserio.JsonWriter(text_io=jsonFile, indent=4), walkFilter)
            walker.walk(withTypeInfoCode)

        with open(self.JSON_NAME_WITH_DEPTH0_FILTER, "r", encoding="utf-8") as jsonFile:
            jsonData = json.load(jsonFile)
        self.assertEqual({}, jsonData)

        with open(self.JSON_NAME_WITH_DEPTH0_FILTER, "r", encoding="utf-8") as jsonFile:
            jsonReader = zserio.JsonReader(text_io=jsonFile)
            readWithTypeInfoCode = jsonReader.read(self.api.WithTypeInfoCode.type_info())
            self._checkWithTypeInfoCodeDepth0(readWithTypeInfoCode)

    def testJsonWriterWithDepth1ArrayLength0Filter(self):
        withTypeInfoCode = createWithTypeInfoCode(self.api)
        withTypeInfoCode.initialize_offsets()
        with open(self.JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER, "w", encoding="utf-8") as jsonFile:
            walkFilter = zserio.AndWalkFilter([zserio.DepthWalkFilter(1), zserio.ArrayLengthWalkFilter(0)])
            walker = zserio.Walker(zserio.JsonWriter(text_io=jsonFile, indent=4), walkFilter)
            walker.walk(withTypeInfoCode)
        self._checkWithTypeInfoCodeDepth1ArrayLength0Json(self.JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER)

        with open(self.JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER, "r", encoding="utf-8") as jsonFile:
            jsonReader = zserio.JsonReader(text_io=jsonFile)
            readWithTypeInfoCode = jsonReader.read(self.api.WithTypeInfoCode.type_info())
            self._checkWithTypeInfoCodeDepth1ArrayLength0(readWithTypeInfoCode)

    def testJsonWriterWithDepth5Filter(self):
        withTypeInfoCode = createWithTypeInfoCode(self.api)
        withTypeInfoCode.initialize_offsets()
        with open(self.JSON_NAME_WITH_DEPTH5_FILTER, "w", encoding="utf-8") as jsonFile:
            walkFilter = zserio.DepthWalkFilter(5)
            walker = zserio.Walker(zserio.JsonWriter(text_io=jsonFile, indent=4), walkFilter)
            walker.walk(withTypeInfoCode)
        self._checkWithTypeInfoCodeJson(self.JSON_NAME_WITH_DEPTH5_FILTER)

        with open(self.JSON_NAME_WITH_DEPTH5_FILTER, "r", encoding="utf-8") as jsonFile:
            jsonReader = zserio.JsonReader(text_io=jsonFile)
            readWithTypeInfoCode = jsonReader.read(self.api.WithTypeInfoCode.type_info())
            self.assertEqual(withTypeInfoCode, readWithTypeInfoCode)

    def testJsonWriterWithRegexFilter(self):
        withTypeInfoCode = createWithTypeInfoCode(self.api, createOptionals=False)
        withTypeInfoCode.initialize_offsets()
        with open(self.JSON_NAME_WITH_REGEX_FILTER, "w", encoding="utf-8") as jsonFile:
            walkFilter = zserio.RegexWalkFilter(".*fieldOffset")
            walker = zserio.Walker(zserio.JsonWriter(text_io=jsonFile, indent=4), walkFilter)
            walker.walk(withTypeInfoCode)
        self._checkWithTypeInfoCodeRegexJson(self.JSON_NAME_WITH_REGEX_FILTER)

        with open(self.JSON_NAME_WITH_REGEX_FILTER, "r", encoding="utf-8") as jsonFile:
            jsonReader = zserio.JsonReader(text_io=jsonFile)
            readWithTypeInfoCode = jsonReader.read(self.api.WithTypeInfoCode.type_info())
            self._checkWithTypeInfoCodeRegex(readWithTypeInfoCode)

    def _checkWithTypeInfoCodeArrayLength(self, withTypeInfoCode, maxArrayLength):
        self.assertLessEqual(len(withTypeInfoCode.complex_struct.array), maxArrayLength)
        self.assertLessEqual(len(withTypeInfoCode.complex_struct.array_with_len), maxArrayLength)
        self.assertLessEqual(len(withTypeInfoCode.complex_struct.param_struct_array), maxArrayLength)
        for paramStruct in withTypeInfoCode.complex_struct.param_struct_array:
            self.assertLessEqual(len(paramStruct.array), maxArrayLength)
        self.assertLessEqual(len(withTypeInfoCode.complex_struct.dynamic_bit_field_array), maxArrayLength)

        self.assertLessEqual(len(withTypeInfoCode.parameterized_struct.array), maxArrayLength)
        self.assertLessEqual(len(withTypeInfoCode.templated_parameterized_struct.array), maxArrayLength)
        self.assertLessEqual(len(withTypeInfoCode.extern_array), maxArrayLength)
        self.assertLessEqual(len(withTypeInfoCode.bytes_array), maxArrayLength)
        self.assertLessEqual(len(withTypeInfoCode.implicit_array), maxArrayLength)

    def _checkWithTypeInfoCodeDepth0(self, withTypeInfoCode):
        self.assertEqual(None, withTypeInfoCode.simple_struct)
        self.assertEqual(None, withTypeInfoCode.complex_struct)
        self.assertEqual(None, withTypeInfoCode.parameterized_struct)
        self.assertEqual(None, withTypeInfoCode.recursive_struct)
        self.assertEqual(None, withTypeInfoCode.recursive_union)
        self.assertEqual(None, withTypeInfoCode.recursive_choice)
        self.assertEqual(None, withTypeInfoCode.selector)
        self.assertEqual(None, withTypeInfoCode.simple_choice)
        self.assertEqual(None, withTypeInfoCode.templated_struct)
        self.assertEqual(None, withTypeInfoCode.templated_parameterized_struct)
        self.assertEqual(None, withTypeInfoCode.extern_data)
        self.assertEqual(bytes(), withTypeInfoCode.bytes_data)
        self.assertEqual([], withTypeInfoCode.extern_array)
        self.assertEqual([], withTypeInfoCode.bytes_array)
        self.assertEqual([], withTypeInfoCode.implicit_array)

    def _checkWithTypeInfoCodeDepth1ArrayLength0(self, withTypeInfoCode):
        self.assertNotEqual(None, withTypeInfoCode.simple_struct)
        self.assertEqual(None, withTypeInfoCode.complex_struct.simple_struct)
        self.assertEqual(None, withTypeInfoCode.complex_struct.another_simple_struct)
        self.assertEqual(None, withTypeInfoCode.complex_struct.optional_simple_struct)
        self.assertEqual([], withTypeInfoCode.complex_struct.array)
        self.assertEqual(0, withTypeInfoCode.complex_struct.dynamic_bit_field)
        self.assertEqual([], withTypeInfoCode.complex_struct.dynamic_bit_field_array)
        self.assertNotEqual(None, withTypeInfoCode.parameterized_struct)
        self.assertNotEqual(None, withTypeInfoCode.recursive_struct)
        self.assertNotEqual(None, withTypeInfoCode.recursive_union)
        self.assertNotEqual(None, withTypeInfoCode.recursive_choice)
        self.assertNotEqual(None, withTypeInfoCode.selector)
        self.assertNotEqual(None, withTypeInfoCode.simple_choice)
        self.assertNotEqual(None, withTypeInfoCode.templated_struct)
        self.assertNotEqual(None, withTypeInfoCode.templated_parameterized_struct)
        self.assertNotEqual(None, withTypeInfoCode.extern_data)
        self.assertNotEqual(None, withTypeInfoCode.bytes_data)
        self.assertEqual([], withTypeInfoCode.extern_array)
        self.assertEqual([], withTypeInfoCode.bytes_array)
        self.assertEqual([], withTypeInfoCode.implicit_array)

    def _checkWithTypeInfoCodeRegex(self, withTypeInfoCode):
        self.assertNotEqual(0, withTypeInfoCode.simple_struct.field_offset)
        self.assertNotEqual(0, withTypeInfoCode.complex_struct.simple_struct.field_offset)
        self.assertNotEqual(0, withTypeInfoCode.complex_struct.another_simple_struct.field_offset)

    def _checkWithTypeInfoCodeJson(self, jsonFileName, *, createdOptionals=True, maxArrayLength=None):
        with open(jsonFileName, "r", encoding="utf-8") as jsonFile:
            jsonData = json.load(jsonFile)

        testEnum = self.api.TestEnum._TWO
        testEnumStringified = "_TWO"
        ts32 = self.api.TS32(0xDEAD)
        self._checkSimpleStructJson(jsonData["simpleStruct"], 8)
        self._checkComplexStructJson(jsonData["complexStruct"], createdOptionals, maxArrayLength)
        self._checkParameterizedStructJson(jsonData["parameterizedStruct"], 10, maxArrayLength)
        self._checkRecursiveStructJson(jsonData["recursiveStruct"], maxArrayLength)
        self._checkRecursiveUnionJson(jsonData["recursiveUnion"], maxArrayLength)
        self._checkRecursiveChoiceJson(jsonData["recursiveChoice"], True, False, maxArrayLength)
        self.assertEqual(testEnumStringified, jsonData["selector"])
        self._checkSimpleChoiceJson(jsonData["simpleChoice"], testEnum)
        self._checkTS32Json(jsonData["templatedStruct"])
        self._checkTemplatedParameterizedStruct_TS32Json(
            jsonData["templatedParameterizedStruct"], ts32, maxArrayLength
        )
        self._checkExternDataJson(jsonData["externData"])
        self._checkExternArrayJson(jsonData["externArray"], maxArrayLength)
        self._checkBytesDataJson(jsonData["bytesData"])
        self._checkBytesArrayJson(jsonData["bytesArray"], maxArrayLength)
        self._checkImplicitArrayJson(jsonData["implicitArray"], maxArrayLength)

        self.assertEqual(15, len(jsonData.keys()))

    def _checkWithTypeInfoCodeDepth1ArrayLength0Json(self, jsonFileName):
        with open(jsonFileName, "r", encoding="utf-8") as jsonFile:
            jsonData = json.load(jsonFile)

        self.assertEqual({}, jsonData["simpleStruct"])
        self.assertEqual({}, jsonData["complexStruct"])
        self.assertEqual({}, jsonData["parameterizedStruct"])
        self.assertEqual({}, jsonData["recursiveStruct"])
        self.assertEqual({}, jsonData["recursiveUnion"])
        self.assertEqual({}, jsonData["recursiveChoice"])
        self.assertEqual("_TWO", jsonData["selector"])
        self.assertEqual({}, jsonData["simpleChoice"])
        self.assertEqual({}, jsonData["templatedStruct"])
        self.assertEqual({}, jsonData["templatedParameterizedStruct"])
        self._checkExternDataJson(jsonData["externData"])
        self.assertEqual([], jsonData["externArray"])
        self._checkBytesDataJson(jsonData["bytesData"])
        self.assertEqual([], jsonData["bytesArray"])
        self.assertEqual([], jsonData["implicitArray"])
        self.assertEqual(15, len(jsonData.keys()))

    def _checkWithTypeInfoCodeRegexJson(self, jsonFileName):
        with open(jsonFileName, "r", encoding="utf-8") as jsonFile:
            jsonData = json.load(jsonFile)

        self.assertEqual(8, jsonData["simpleStruct"]["fieldOffset"])
        self.assertEqual(1, len(jsonData["simpleStruct"].keys()))
        self.assertEqual(40, jsonData["complexStruct"]["simpleStruct"]["fieldOffset"])
        self.assertEqual(1, len(jsonData["complexStruct"]["simpleStruct"].keys()))
        self.assertEqual(72, jsonData["complexStruct"]["anotherSimpleStruct"]["fieldOffset"])
        self.assertEqual(1, len(jsonData["complexStruct"]["anotherSimpleStruct"].keys()))
        self.assertEqual(2, len(jsonData["complexStruct"].keys()))
        self.assertEqual(2, len(jsonData.keys()))

    def _checkSimpleStructJson(self, simpleStruct, fieldOffset):
        self.assertEqual(10, simpleStruct["fieldU32"])
        self.assertEqual(fieldOffset, simpleStruct["fieldOffset"])
        self.assertEqual("MyString", simpleStruct["fieldString"])
        self.assertEqual(False, simpleStruct["fieldBool"])
        self.assertEqual(1.0, simpleStruct["fieldFloat16"])
        self.assertEqual(4.0, simpleStruct["fieldFloat32"])
        self.assertEqual(2.0, simpleStruct["fieldFloat64"])
        self.assertEqual(7, len(simpleStruct.keys()))

    def _checkComplexStructJson(self, complexStruct, createdOptionals, maxArrayLength):
        self._checkSimpleStructJson(complexStruct["simpleStruct"], 40)

        self._checkSimpleStructJson(complexStruct["anotherSimpleStruct"], 72)

        if createdOptionals:
            self._checkSimpleStructJson(complexStruct["optionalSimpleStruct"], 104)
        else:
            self.assertEqual(None, complexStruct["optionalSimpleStruct"])

        array = [3, 0xABCD2, 0xABCD3, 0xABCD4, 0xABCD5]
        arrayLength = len(array) if maxArrayLength is None or len(array) <= maxArrayLength else maxArrayLength
        self.assertEqual(arrayLength, len(complexStruct["array"]))
        for i, jsonArrayElement in enumerate(complexStruct["array"]):
            self.assertEqual(array[i], jsonArrayElement)

        arrayWithLenLength = 3 if maxArrayLength is None or maxArrayLength > 3 else maxArrayLength
        self.assertEqual(list(range(3, 3 - arrayWithLenLength, -1)), complexStruct["arrayWithLen"])

        if createdOptionals:
            if maxArrayLength is None or maxArrayLength > 0:
                self._checkParameterizedStructJson(complexStruct["paramStructArray"][0], 10, maxArrayLength)
            if maxArrayLength is None or maxArrayLength > 1:
                self._checkParameterizedStructJson(complexStruct["paramStructArray"][1], 10, maxArrayLength)
        else:
            self.assertEqual(None, complexStruct["paramStructArray"])

        self.assertEqual(8, complexStruct["dynamicBitField"])

        dynamicBitFieldArrayLength = (
            65536 if (maxArrayLength is None or maxArrayLength > 65536 // 2) else maxArrayLength * 2
        )
        self.assertEqual(list(range(1, dynamicBitFieldArrayLength, 2)), complexStruct["dynamicBitFieldArray"])

        if createdOptionals:
            self.assertEqual("ItemThree", complexStruct["optionalEnum"])
            self.assertEqual("RED | _Green | ColorBlue", complexStruct["optionalBitmask"])
            self._checkOptionalExternDataJson(complexStruct["optionalExtern"])
            self._checkOptionalBytesDataJson(complexStruct["optionalBytes"])
        else:
            self.assertEqual(None, complexStruct["optionalEnum"])
            self.assertEqual(None, complexStruct["optionalBitmask"])
            self.assertEqual(None, complexStruct["optionalExtern"])
            self.assertEqual(None, complexStruct["optionalBytes"])

        enumArray = ["_TWO", "ItemThree"]  # stringified
        enumArrayLength = (
            len(enumArray) if (maxArrayLength is None or len(enumArray) <= maxArrayLength) else maxArrayLength
        )
        self.assertEqual(enumArrayLength, len(complexStruct["enumArray"]))
        for i, jsonArrayElement in enumerate(complexStruct["enumArray"]):
            self.assertEqual(enumArray[i], jsonArrayElement)

        bitmaskArrayLen = 5 if maxArrayLength is None or maxArrayLength > 5 else maxArrayLength
        self.assertEqual(bitmaskArrayLen, len(complexStruct["bitmaskArray"]))
        for jsonArrayElement in complexStruct["bitmaskArray"]:
            self.assertEqual("_Green", jsonArrayElement)

        self.assertEqual(14, len(complexStruct.keys()))

    def _checkParameterizedStructJson(self, parameterizedStruct, fieldU32, maxArrayLength):
        arrayLength = fieldU32 if maxArrayLength is None or maxArrayLength > fieldU32 else maxArrayLength
        self.assertEqual(list(range(arrayLength)), parameterizedStruct["array"])
        self.assertEqual(1, len(parameterizedStruct.keys()))

    def _checkRecursiveStructJson(self, recursiveStruct, maxArrayLength):
        self.assertEqual(0xDEAD1, recursiveStruct["fieldU32"])
        self.assertEqual(0xDEAD2, recursiveStruct["fieldRecursion"]["fieldU32"])
        self.assertEqual(None, recursiveStruct["fieldRecursion"]["fieldRecursion"])
        self.assertEqual([], recursiveStruct["fieldRecursion"]["arrayRecursion"])
        if maxArrayLength is None or maxArrayLength > 0:
            self.assertEqual(0xDEAD3, recursiveStruct["arrayRecursion"][0]["fieldU32"])
            self.assertEqual(None, recursiveStruct["arrayRecursion"][0]["fieldRecursion"])
            self.assertEqual([], recursiveStruct["arrayRecursion"][0]["arrayRecursion"])
        if maxArrayLength is None or maxArrayLength > 1:
            self.assertEqual(0xDEAD4, recursiveStruct["arrayRecursion"][1]["fieldU32"])
            self.assertEqual(None, recursiveStruct["arrayRecursion"][1]["fieldRecursion"])
            self.assertEqual([], recursiveStruct["arrayRecursion"][1]["arrayRecursion"])
        self.assertEqual(3, len(recursiveStruct.keys()))

    def _checkRecursiveUnionJson(self, recursiveUnion, maxArrayLength):
        if maxArrayLength is None or maxArrayLength > 0:
            self.assertEqual(0xDEAD, recursiveUnion["recursive"][0]["fieldU32"])
        self.assertEqual(1, len(recursiveUnion.keys()))

    def _checkRecursiveChoiceJson(self, recursiveChoice, param1, param2, maxArrayLength):
        if param1:
            recursiveLength = (
                len(recursiveChoice["recursive"])
                if maxArrayLength is None or maxArrayLength > len(recursiveChoice["recursive"])
                else maxArrayLength
            )
            for i in range(recursiveLength):
                self._checkRecursiveChoiceJson(recursiveChoice["recursive"][i], param2, False, maxArrayLength)
        else:
            self.assertEqual(0xDEAD, recursiveChoice["fieldU32"])
        self.assertEqual(1, len(recursiveChoice.keys()))

    def _checkSimpleUnionJson(self, simpleUnion):
        self.assertEqual("_Green", simpleUnion["testBitmask"])
        self.assertEqual(1, len(simpleUnion.keys()))

    def _checkSimpleChoiceJson(self, simpleChoice, testEnum):
        if testEnum == self.api.TestEnum._TWO:
            self._checkSimpleUnionJson(simpleChoice["fieldTwo"])
        else:
            self.assertEqual("text", simpleChoice["fieldDefault"])
        self.assertEqual(1, len(simpleChoice.keys()))

    def _checkTS32Json(self, ts32):
        self.assertEqual(0xDEAD, ts32["field"])
        self.assertEqual(1, len(ts32.keys()))

    def _checkTemplatedParameterizedStruct_TS32Json(
        self, templatedParameterizedStruct_TS32, ts32, maxArrayLength
    ):
        arrayLength = ts32.field if maxArrayLength is None or maxArrayLength > ts32.field else maxArrayLength
        for i in range(ts32.field, ts32.field - arrayLength, -1):
            self.assertEqual(i, templatedParameterizedStruct_TS32["array"][ts32.field - i])
        self.assertEqual(1, len(templatedParameterizedStruct_TS32.keys()))

    def _checkExternDataJson(self, externData):
        self.assertEqual([202, 254], externData["buffer"])
        self.assertEqual(15, externData["bitSize"])
        self.assertEqual(2, len(externData.keys()))

    def _checkExternArrayJson(self, externArray, maxArrayLength):
        externArrayLen = 2
        filteredExternArrayLength = (
            externArrayLen if (maxArrayLength is None or externArrayLen <= maxArrayLength) else maxArrayLength
        )
        self.assertEqual(filteredExternArrayLength, len(externArray))
        for jsonArrayElement in externArray:
            self._checkExternDataJson(jsonArrayElement)

    def _checkBytesDataJson(self, bytesData):
        self.assertEqual([0xAB, 0xCD], bytesData["buffer"])
        self.assertEqual(1, len(bytesData.keys()))

    def _checkBytesArrayJson(self, bytesArray, maxArrayLength):
        bytesArrayLen = 2
        filteredBytesArrayLength = (
            bytesArrayLen if (maxArrayLength is None or bytesArrayLen <= maxArrayLength) else maxArrayLength
        )
        self.assertEqual(filteredBytesArrayLength, len(bytesArray))
        for jsonArrayElement in bytesArray:
            self._checkBytesDataJson(jsonArrayElement)

    def _checkImplicitArrayJson(self, implicitArray, maxArrayLength):
        expectedImplicitArray = [1, 4, 6, 4, 6, 1]
        filteredImplicitArrayLength = (
            len(expectedImplicitArray)
            if (maxArrayLength is None or len(expectedImplicitArray) <= maxArrayLength)
            else maxArrayLength
        )
        self.assertEqual(filteredImplicitArrayLength, len(implicitArray))
        for i, jsonArrayElement in enumerate(implicitArray):
            self.assertEqual(expectedImplicitArray[i], jsonArrayElement)

    def _checkOptionalExternDataJson(self, externData):
        self.assertEqual([203, 240], externData["buffer"])
        self.assertEqual(12, externData["bitSize"])
        self.assertEqual(2, len(externData.keys()))

    def _checkOptionalBytesDataJson(self, bytesData):
        self.assertEqual([0xAB, 0xCD], bytesData["buffer"])
        self.assertEqual(1, len(bytesData.keys()))

    @staticmethod
    def _getJsonNameWithArrayLengthFilter(arrayLength):
        return os.path.join(
            getApiDir(os.path.dirname(__file__)),
            "with_type_info_code_array_length_" + str(arrayLength) + ".json",
        )

    JSON_NAME_WITH_OPTIONALS = os.path.join(
        getApiDir(os.path.dirname(__file__)), "with_type_info_code_optionals.json"
    )
    JSON_NAME_WITHOUT_OPTIONALS = os.path.join(getApiDir(os.path.dirname(__file__)), "with_type_info_code.json")
    JSON_NAME_WITH_DEPTH0_FILTER = os.path.join(
        getApiDir(os.path.dirname(__file__)), "with_type_info_code_depth0.json"
    )
    JSON_NAME_WITH_DEPTH5_FILTER = os.path.join(
        getApiDir(os.path.dirname(__file__)), "with_type_info_code_depth5.json"
    )
    JSON_NAME_WITH_DEPTH1_ARRAY_LENGTH0_FILTER = os.path.join(
        getApiDir(os.path.dirname(__file__)), "with_type_info_code_depth1_array_length0.json"
    )
    JSON_NAME_WITH_REGEX_FILTER = os.path.join(
        getApiDir(os.path.dirname(__file__)), "with_type_info_code_regex.json"
    )
