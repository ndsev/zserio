import unittest
import typing

from zserio import Walker, WalkFilter, PythonRuntimeException
from zserio.typeinfo import TypeInfo, TypeAttribute, MemberInfo, MemberAttribute
from zserio.array import Array, ObjectArrayTraits

class DummyUnion:

    def __init__(
            self,
            *,
            value_: typing.Union[int, None]=None,
            text_: typing.Union[str, None]=None,
            nested_array_: typing.Optional[typing.List['DummyNested']]=None) -> None:
        self._choice_tag: int = self.UNDEFINED_CHOICE
        self._choice: typing.Any = None
        if value_ is not None:
            self._choice_tag = self.CHOICE_VALUE
            self._choice = value_
        if text_ is not None:
            self._choice_tag = self.CHOICE_TEXT
            self._choice = text_
        if nested_array_ is not None:
            self._choice_tag = self.CHOICE_NESTED_ARRAY
            self._choice = Array(ObjectArrayTraits(None, None, None), nested_array_, is_auto=True)

    @staticmethod
    def type_info() -> TypeInfo:
        field_list: typing.List[MemberInfo] = [
            MemberInfo(
                'value', TypeInfo('uint32', int),
                attributes={
                    MemberAttribute.PROPERTY_NAME: 'value'
                }
            ),
            MemberInfo(
                'text', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME: 'text'
                }
            ),
            MemberInfo(
                'nestedArray', DummyNested.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME: 'nested_array',
                    MemberAttribute.ARRAY_LENGTH: None
                }
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS: field_list,
            TypeAttribute.SELECTOR: None
        }

        return TypeInfo("DummyUnion", DummyUnion, attributes=attribute_list)

    @property
    def choice_tag(self) -> int:
        return self._choice_tag

    @property
    def value(self) -> int:
        return self._choice

    @property
    def text(self) -> str:
        return self._choice

    @property
    def nested_array(self) -> typing.Optional[typing.List['DummyNested']]:
        return self._choice.raw_array

    CHOICE_VALUE = 0
    CHOICE_TEXT = 1
    CHOICE_NESTED_ARRAY = 2
    UNDEFINED_CHOICE = -1


class DummyNested:

    def __init__(self, text_: str=str()) -> None:
        self._text = text_

    @staticmethod
    def type_info() -> TypeInfo:
        field_list: typing.List[MemberInfo] = [
            MemberInfo(
                'text', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME: 'text'
                }
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS: field_list
        }

        return TypeInfo("DummyNested", DummyNested, attributes=attribute_list)

    @property
    def text(self) -> str:
        return self._text


class DummyObject:

    def __init__(self, identifier_: int=int(),
                 nested_: typing.Optional[DummyNested]=None,
                 text_: str=str(),
                 union_array_: typing.List[DummyUnion]=None) -> None:
        self._identifier = identifier_
        self._nested = nested_
        self._text = text_
        self._union_array = Array(ObjectArrayTraits(None, None, None), union_array_, is_auto=True)

    @staticmethod
    def type_info() -> TypeInfo:
        field_list: typing.List[MemberInfo] = [
            MemberInfo(
                'identifier', TypeInfo('uint32', int),
                attributes={
                    MemberAttribute.PROPERTY_NAME: 'identifier'
                }
            ),
            MemberInfo(
                'nested', DummyNested.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME: 'nested'
                }
            ),
            MemberInfo(
                'text', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME: 'text'
                }
            ),
            MemberInfo(
                'unionArray', DummyUnion.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME: 'union_array',
                    MemberAttribute.ARRAY_LENGTH: None
                }
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS: field_list
        }

        return TypeInfo("DummyObject", DummyObject, attributes=attribute_list)

    @property
    def identifier(self) -> int:
        return self._identifier

    @property
    def nested(self) -> DummyNested:
        return self._nested

    @property
    def text(self) -> str:
        return self._text

    @property
    def union_array(self) -> typing.List[DummyUnion]:
        return self._union_array.raw_array


class TestObserver(Walker.Observer):

    def __init__(self):
        self._captures = {
            "begin_root": None,
            "end_root": None,
            "begin_array": [],
            "end_array": [],
            "begin_compound": [],
            "end_compound": [],
            "visit_value": []
        }

    @property
    def captures(self):
        return self._captures

    def begin_root(self, compound):
        self._captures["begin_root"] = compound

    def end_root(self, compound):
        self._captures["end_root"] = compound

    def begin_array(self, array, member_info: MemberInfo):
        self._captures["begin_array"].append(array)

    def end_array(self, array, member_info: MemberInfo):
        self._captures["end_array"].append(array)

    def begin_compound(self, compound, member_info, element_index: typing.Optional[int]):
        self._captures["begin_compound"].append(compound)

    def end_compound(self, compound, member_info, element_index: typing.Optional[int]):
        self._captures["end_compound"].append(compound)

    def visit_value(self, value, member_info, element_index: typing.Optional[int]):
        self._captures["visit_value"].append(value)


class TestFilter(Walker.Filter):

    def __init__(self, *, before_array=True, after_array=True, only_first_element=False,
                 before_compound=True, after_compound=True, before_value=True, after_value=True):
        self._config = {
            "before_array": before_array,
            "after_array": after_array,
            "only_first_element": only_first_element,
            "before_compound": before_compound,
            "after_compound": after_compound,
            "before_value": before_value,
            "after_value": after_value
        }
        self._is_first_element = False

    def before_array(self, array, member_info):
        self._is_first_element = True
        return self._config["before_array"]

    def after_array(self, array, member_info):
        self._is_first_element = False
        return self._config["after_array"]

    def before_compound(self, compound, member_info, element_index):
        return self._config["before_compound"]

    def after_compound(self, compound, member_info, element_index):
        if self._config["only_first_element"] and self._is_first_element:
            return False
        self._is_first_element = False
        return self._config["after_compound"]

    def before_value(self, value, member_info, element_index):
        return self._config["before_value"]

    def after_value(self, value, member_info, element_index):
        return self._config["after_value"]


class WalkerTest(unittest.TestCase):

    def test_observer(self):
        observer = Walker.Observer()

        dummy_object = object()
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        none_element_index = None

        with self.assertRaises(NotImplementedError):
            observer.begin_root(dummy_object)
        with self.assertRaises(NotImplementedError):
            observer.end_root(dummy_object)
        with self.assertRaises(NotImplementedError):
            observer.begin_array([], dummy_member_info)
        with self.assertRaises(NotImplementedError):
            observer.end_array([], dummy_member_info)
        with self.assertRaises(NotImplementedError):
            observer.begin_compound(dummy_object, dummy_member_info, none_element_index)
        with self.assertRaises(NotImplementedError):
            observer.end_compound(dummy_object, dummy_member_info, none_element_index)
        with self.assertRaises(NotImplementedError):
            observer.visit_value("", dummy_member_info, none_element_index)

    def test_filter(self):
        walk_filter = Walker.Filter()

        dummy_object = object()
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        none_element_index = None

        with self.assertRaises(NotImplementedError):
            walk_filter.before_array([], dummy_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.after_array([], dummy_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.before_compound(dummy_object, dummy_member_info, none_element_index)
        with self.assertRaises(NotImplementedError):
            walk_filter.after_compound(dummy_object, dummy_member_info, none_element_index)
        with self.assertRaises(NotImplementedError):
            walk_filter.before_value("", dummy_member_info, none_element_index)
        with self.assertRaises(NotImplementedError):
            walk_filter.after_value("", dummy_member_info, none_element_index)

    def test_walk_without_type_info(self):
        walker = Walker(TestObserver())
        obj = object()
        with self.assertRaises(PythonRuntimeException):
            walker.walk(obj)

    def test_walk(self):
        observer = TestObserver()
        walker = Walker(observer)
        obj = _create_dummy_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([obj.union_array, obj.union_array[2].nested_array], observer.captures["begin_array"])
        self.assertEqual([obj.union_array[2].nested_array, obj.union_array], observer.captures["end_array"])
        self.assertEqual(
            [
                obj.nested, obj.union_array[0], obj.union_array[1],
                obj.union_array[2], obj.union_array[2].nested_array[0]
            ],
            observer.captures["begin_compound"]
        )
        self.assertEqual(
            [
                obj.nested, obj.union_array[0], obj.union_array[1],
                obj.union_array[2].nested_array[0], obj.union_array[2]
            ],
            observer.captures["end_compound"]
        )
        self.assertEqual([13, "nested", "test", '1', 2, "nestedArray"], observer.captures["visit_value"])

    def test_walk_skip_compound(self):
        observer = TestObserver()
        test_filter = TestFilter(before_array=True, after_array=True,
                                 before_compound=False, after_compound=True,
                                 before_value=True, after_value=True)
        walker = Walker(observer, WalkFilter().add(test_filter))
        obj = _create_dummy_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([obj.union_array], observer.captures["begin_array"])
        self.assertEqual([obj.union_array], observer.captures["end_array"])
        self.assertEqual([], observer.captures["begin_compound"])
        self.assertEqual([], observer.captures["end_compound"])
        self.assertEqual([13, "test"], observer.captures["visit_value"])

    def test_walk_skip_siblings(self):
        observer = TestObserver()
        test_filter = TestFilter(before_array=True, after_array=True,
                                 before_compound=True, after_compound=True,
                                 before_value=True, after_value=False)
        walker = Walker(observer, WalkFilter().add(test_filter))
        obj = _create_dummy_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([], observer.captures["begin_compound"])
        self.assertEqual([], observer.captures["end_compound"])
        self.assertEqual([13], observer.captures["visit_value"])

    def test_walk_skip_after_nested(self):
        observer = TestObserver()
        test_filter = TestFilter(before_array=True, after_array=True,
                                 before_compound=True, after_compound=False,
                                 before_value=True, after_value=True)
        walker = Walker(observer, WalkFilter().add(test_filter))
        obj = _create_dummy_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([], observer.captures["begin_array"])
        self.assertEqual([], observer.captures["end_array"])
        self.assertEqual([obj.nested], observer.captures["begin_compound"])
        self.assertEqual([obj.nested], observer.captures["end_compound"])
        self.assertEqual([13, "nested"], observer.captures["visit_value"])

    def test_walk_only_first_element(self):
        observer = TestObserver()
        test_filter = TestFilter(before_array=True, after_array=True, only_first_element=True,
                                 before_compound=True, after_compound=True,
                                 before_value=True, after_value=True)
        walker = Walker(observer, WalkFilter().add(test_filter))
        obj = _create_dummy_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([obj.union_array], observer.captures["begin_array"])
        self.assertEqual([obj.union_array], observer.captures["end_array"])
        self.assertEqual([obj.nested, obj.union_array[0]], observer.captures["begin_compound"])
        self.assertEqual([obj.nested, obj.union_array[0]], observer.captures["end_compound"])
        self.assertEqual([13, "nested", "test", '1'], observer.captures["visit_value"])


class DefaultObserverTest(unittest.TestCase):

    @staticmethod
    def test_default():
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        default_observer = Walker.DefaultObserver()

        default_observer.begin_root(object())
        default_observer.end_root(object())
        default_observer.begin_array([], dummy_member_info)
        default_observer.end_array([], dummy_member_info)
        default_observer.begin_compound(object(), dummy_member_info, None)
        default_observer.end_compound(object(), dummy_member_info, None)
        default_observer.visit_value(None, dummy_member_info, None)

class WalkFilterTest(unittest.TestCase):

    def test_empty(self):
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        none_element_index = None
        walk_filter = WalkFilter()

        self.assertTrue(walk_filter.before_array([], dummy_member_info))
        self.assertTrue(walk_filter.after_array([], dummy_member_info))
        self.assertTrue(walk_filter.before_compound(object(), dummy_member_info, none_element_index))
        self.assertTrue(walk_filter.after_compound(object(), dummy_member_info, none_element_index))
        self.assertTrue(walk_filter.before_value(None, dummy_member_info, none_element_index))
        self.assertTrue(walk_filter.after_value(None, dummy_member_info, none_element_index))

    def test_true_true(self):
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        none_element_index = None
        # two filters returning True
        walk_filter = WalkFilter().add(WalkFilter()).add(WalkFilter())

        self.assertTrue(walk_filter.before_array([], dummy_member_info))
        self.assertTrue(walk_filter.after_array([], dummy_member_info))
        self.assertTrue(walk_filter.before_compound(object(), dummy_member_info, none_element_index))
        self.assertTrue(walk_filter.after_compound(object(), dummy_member_info, none_element_index))
        self.assertTrue(walk_filter.before_value(None, dummy_member_info, none_element_index))
        self.assertTrue(walk_filter.after_value(None, dummy_member_info, none_element_index))

    def test_false_false(self):
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        none_element_index = None
        # two filters returning False
        walk_filter = WalkFilter().add(
            TestFilter(before_array=False, after_array=False,
                       before_compound=False, after_compound=False,
                       before_value=False, after_value=False)
        ).add(
            TestFilter(before_array=False, after_array=False,
                       before_compound=False, after_compound=False,
                       before_value=False, after_value=False)
        )

        self.assertFalse(walk_filter.before_array([], dummy_member_info))
        self.assertFalse(walk_filter.after_array([], dummy_member_info))
        self.assertFalse(walk_filter.before_compound(object(), dummy_member_info, none_element_index))
        self.assertFalse(walk_filter.after_compound(object(), dummy_member_info, none_element_index))
        self.assertFalse(walk_filter.before_value(None, dummy_member_info, none_element_index))
        self.assertFalse(walk_filter.after_value(None, dummy_member_info, none_element_index))

    def test_true_false(self):
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        none_element_index = None
        walk_filter = WalkFilter().add(
            WalkFilter() # returning true
        ).add(
            TestFilter(before_array=False, after_array=False,
                       before_compound=False, after_compound=False,
                       before_value=False, after_value=False) # returning false
        )

        self.assertFalse(walk_filter.before_array([], dummy_member_info))
        self.assertFalse(walk_filter.after_array([], dummy_member_info))
        self.assertFalse(walk_filter.before_compound(object(), dummy_member_info, none_element_index))
        self.assertFalse(walk_filter.after_compound(object(), dummy_member_info, none_element_index))
        self.assertFalse(walk_filter.before_value(None, dummy_member_info, none_element_index))
        self.assertFalse(walk_filter.after_value(None, dummy_member_info, none_element_index))

class DepthFilterTest(unittest.TestCase):

    def test_depth_0(self):
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        none_element_index = None
        depth_filter = WalkFilter.Depth(0)

        self.assertFalse(depth_filter.before_array([], dummy_member_info))
        self.assertTrue(depth_filter.after_array([], dummy_member_info))
        self.assertFalse(depth_filter.before_compound(object(), dummy_member_info, none_element_index))
        self.assertTrue(depth_filter.after_compound(object(), dummy_member_info, none_element_index))
        self.assertFalse(depth_filter.before_value(None, dummy_member_info, none_element_index))
        self.assertTrue(depth_filter.after_value(None, dummy_member_info, none_element_index))

    def test_depth_1(self):
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        none_element_index = None
        depth_filter = WalkFilter.Depth(1)

        self.assertTrue(depth_filter.before_array([], dummy_member_info))  # 0
        self.assertFalse(depth_filter.before_compound(object(), dummy_member_info, 0))  # 1
        self.assertTrue(depth_filter.after_compound(object(), dummy_member_info, 0))  # 1
        self.assertFalse(depth_filter.before_array([], dummy_member_info))  # 1
        self.assertTrue(depth_filter.after_array([], dummy_member_info))  # 1
        self.assertFalse(depth_filter.before_value(None, dummy_member_info, 1))  # 1
        self.assertTrue(depth_filter.after_value(None, dummy_member_info, 1))  # 1
        self.assertTrue(depth_filter.after_array([], dummy_member_info))  # 0
        self.assertTrue(depth_filter.before_compound(object(), dummy_member_info, none_element_index))  # 0
        self.assertFalse(depth_filter.before_compound(object(), dummy_member_info, none_element_index))  # 1
        self.assertTrue(depth_filter.after_compound(object(), dummy_member_info, none_element_index))  # 1
        self.assertFalse(depth_filter.before_array([], dummy_member_info))  # 1
        self.assertTrue(depth_filter.after_array([], dummy_member_info))  # 1
        self.assertFalse(depth_filter.before_value(None, dummy_member_info, none_element_index))  # 1
        self.assertTrue(depth_filter.after_value(None, dummy_member_info, none_element_index))  # 1
        self.assertTrue(depth_filter.after_compound(object(), dummy_member_info, none_element_index))  # 0
        self.assertTrue(depth_filter.before_value(None, dummy_member_info, none_element_index))  # 0
        self.assertTrue(depth_filter.after_value(None, dummy_member_info, none_element_index))  # 0


class RegexFilterTest(unittest.TestCase):

    def test_regex_all(self):
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        none_element_index = None
        dummy_array_member_info = MemberInfo("dummy", TypeInfo("Dummy", None), attributes={
            MemberAttribute.ARRAY_LENGTH: None
        })
        regex_filter = WalkFilter.Regex(".*")

        self.assertTrue(regex_filter.before_array([], dummy_array_member_info))
        self.assertTrue(regex_filter.after_array([], dummy_array_member_info))
        self.assertTrue(regex_filter.before_compound(object(), dummy_member_info, none_element_index))
        self.assertTrue(regex_filter.before_compound(object(), dummy_member_info, none_element_index))
        self.assertTrue(regex_filter.before_value(None, dummy_member_info, none_element_index))
        self.assertTrue(regex_filter.after_value(None, dummy_member_info, none_element_index))

    def test_regex_prefix(self):
        dummy_object = _create_dummy_object()
        none_element_index = None
        regex_filter = WalkFilter.Regex("nested\\..*")

        identifier_member_info = dummy_object.type_info().attributes[TypeAttribute.FIELDS][0]
        self.assertFalse(regex_filter.before_value(dummy_object.identifier, identifier_member_info,
                                                   none_element_index))
        self.assertTrue(regex_filter.after_value(dummy_object.identifier, identifier_member_info,
                                                 none_element_index))

        nested_member_info = dummy_object.type_info().attributes[TypeAttribute.FIELDS][1]
        self.assertTrue(regex_filter.before_compound(dummy_object.nested, nested_member_info,
                                                     none_element_index))
        text_member_info = nested_member_info.type_info.attributes[TypeAttribute.FIELDS][0]
        self.assertTrue(regex_filter.before_value(dummy_object.nested.text, text_member_info,
                                                  none_element_index))
        self.assertTrue(regex_filter.after_value(dummy_object.nested.text, text_member_info,
                                                 none_element_index))
        self.assertTrue(regex_filter.after_compound(dummy_object.nested, nested_member_info,
                                                    none_element_index))

        # ignore text

        union_array_member_info = dummy_object.type_info().attributes[TypeAttribute.FIELDS][3]
        self.assertFalse(regex_filter.before_array(dummy_object.union_array, union_array_member_info))
        self.assertTrue(regex_filter.after_array(dummy_object.union_array, union_array_member_info))

    def test_regex_array(self):
        dummy_object = _create_dummy_object()
        regex_filter = WalkFilter.Regex("unionArray\\[\\d+\\]\\.nes.*")

        union_array_member_info = dummy_object.type_info().attributes[TypeAttribute.FIELDS][3]
        self.assertTrue(regex_filter.before_array(dummy_object.union_array, union_array_member_info))

        self.assertFalse(regex_filter.before_compound(dummy_object.union_array[0], union_array_member_info, 0))
        self.assertTrue(regex_filter.after_compound(dummy_object.union_array[0], union_array_member_info, 0))

        self.assertFalse(regex_filter.before_compound(dummy_object.union_array[1], union_array_member_info, 1))
        self.assertTrue(regex_filter.after_compound(dummy_object.union_array[1], union_array_member_info, 1))

        self.assertTrue(regex_filter.before_compound(dummy_object.union_array[2], union_array_member_info, 2))
        self.assertTrue(regex_filter.after_compound(dummy_object.union_array[2], union_array_member_info, 2))

        self.assertTrue(regex_filter.after_array(dummy_object.union_array, union_array_member_info))

    def test_regex_array_no_match(self):
        dummy_object = DummyObject(13, DummyNested("nested"), "test",
                                   [DummyUnion(nested_array_=[DummyNested("ahoj")])])

        regex_filter = WalkFilter.Regex("^unionArray\\[\\d*\\]\\.te.*")

        union_array_member_info = dummy_object.type_info().attributes[TypeAttribute.FIELDS][3]
        self.assertFalse(regex_filter.before_array(dummy_object.union_array, union_array_member_info))
        self.assertTrue(regex_filter.after_array(dummy_object.union_array, union_array_member_info))

    def test_regex_none_compound_match(self):
        dummy_object = DummyObject(13, None, "test", [DummyUnion(text_="1"), DummyUnion(value_=2)])

        regex_filter = WalkFilter.Regex("nested")

        nested_member_info = dummy_object.type_info().attributes[TypeAttribute.FIELDS][1]
        self.assertTrue(regex_filter.before_compound(dummy_object.nested, nested_member_info, None))
        self.assertTrue(regex_filter.after_compound(dummy_object.nested, nested_member_info, None))

    def test_regex_none_compound_no_match(self):
        dummy_object = DummyObject(13, None, "test", [DummyUnion(text_="1"), DummyUnion(value_=2)])

        regex_filter = WalkFilter.Regex("^nested\\.text$")

        nested_member_info = dummy_object.type_info().attributes[TypeAttribute.FIELDS][1]
        self.assertFalse(regex_filter.before_compound(dummy_object.nested, nested_member_info, None))
        self.assertTrue(regex_filter.after_compound(dummy_object.nested, nested_member_info, None))

    def test_regex_none_array_match(self):
        dummy_object = DummyObject(13, DummyNested("nested"), "test", None)

        regex_filter = WalkFilter.Regex("unionArray")

        union_array_member_info = dummy_object.type_info().attributes[TypeAttribute.FIELDS][3]
        self.assertTrue(regex_filter.before_array(dummy_object.union_array, union_array_member_info))
        self.assertTrue(regex_filter.after_array(dummy_object.union_array, union_array_member_info))

    def test_regex_none_array_no_match(self):
        dummy_object = DummyObject(13, DummyNested("nested"), "test", None)

        regex_filter = WalkFilter.Regex("^unionArray\\[\\d+\\]\\.nestedArray.*")

        union_array_member_info = dummy_object.type_info().attributes[TypeAttribute.FIELDS][3]
        self.assertFalse(regex_filter.before_array(dummy_object.union_array, union_array_member_info))
        self.assertTrue(regex_filter.after_array(dummy_object.union_array, union_array_member_info))

class ArrayLengthFilterTest(unittest.TestCase):

    def test_array_length_0(self):
        dummy_member_info = MemberInfo("dummy", TypeInfo("Dummy", None))
        none_element_index = None
        dummy_array_member_info = MemberInfo("dummy_array", TypeInfo("Dummy", None), attributes={
            MemberAttribute.ARRAY_LENGTH : None
        })
        array_length_filter = WalkFilter.ArrayLength(0)

        self.assertTrue(array_length_filter.before_array([], dummy_array_member_info))
        self.assertFalse(array_length_filter.before_compound(object(), dummy_array_member_info, 0))
        self.assertFalse(array_length_filter.after_compound(object(), dummy_array_member_info, 0))
        self.assertFalse(array_length_filter.before_value(None, dummy_array_member_info, 1))
        self.assertFalse(array_length_filter.after_value(None, dummy_array_member_info, 1))
        self.assertTrue(array_length_filter.after_array([], dummy_array_member_info))

        self.assertTrue(array_length_filter.before_compound(object(), dummy_member_info, none_element_index))
        self.assertTrue(array_length_filter.before_value(None, dummy_member_info, none_element_index))
        self.assertTrue(array_length_filter.after_value(None, dummy_member_info, none_element_index))
        self.assertTrue(array_length_filter.before_array([], dummy_array_member_info))
        self.assertFalse(array_length_filter.before_value(None, dummy_array_member_info, 0))
        self.assertFalse(array_length_filter.after_value(None, dummy_array_member_info, 0))
        self.assertTrue(array_length_filter.after_array([], dummy_array_member_info))
        self.assertTrue(array_length_filter.after_compound(object(), dummy_member_info, none_element_index))


def _create_dummy_object():
    return DummyObject(13, DummyNested("nested"), "test", [
        DummyUnion(text_="1"),
        DummyUnion(value_=2),
        DummyUnion(nested_array_=[DummyNested("nestedArray")])
    ])
