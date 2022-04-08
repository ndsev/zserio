import unittest
import typing

from zserio import Walker, WalkFilter, PythonRuntimeException
from zserio.typeinfo import TypeInfo, TypeAttribute, MemberInfo, MemberAttribute
from zserio.array import Array, ObjectArrayTraits

class DummyUnion:
    def __init__(
            self,
            *,
            value_: typing.Union[int, None] = None,
            text_: typing.Union[str, None] = None) -> None:
        self._choice_tag: int = self.UNDEFINED_CHOICE
        self._choice: typing.Any = None
        if value_ is not None:
            self._choice_tag = self.CHOICE_VALUE
            self._choice = value_
        if text_ is not None:
            self._choice_tag = self.CHOICE_TEXT
            self._choice = text_

    @staticmethod
    def type_info() -> TypeInfo:
        field_list: typing.List[MemberInfo] = [
            MemberInfo(
                'value', TypeInfo('uint32', int),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'value'
                }
            ),
            MemberInfo(
                'text', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'text'
                }
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS : field_list,
            TypeAttribute.SELECTOR : None
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

    CHOICE_VALUE = 0
    CHOICE_TEXT = 1
    UNDEFINED_CHOICE = -1

class DummyNested:
    def __init__(self, text_ : str = str()) -> None:
        self._text = text_

    @staticmethod
    def type_info() -> TypeInfo:
        field_list: typing.List[MemberInfo] = [
            MemberInfo(
                'text', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'text'
                }
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS : field_list
        }

        return TypeInfo("DummyNested", DummyNested, attributes=attribute_list)

    @property
    def text(self) -> str:
        return self._text

class DummyObject:
    def __init__(self, identifier_: int = int(),
                 nested_: typing.Optional[DummyNested] = None,
                 text_: str = str(),
                 union_array_: typing.List[DummyUnion] = None) -> None:
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
                    MemberAttribute.PROPERTY_NAME : 'identifier'
                }
            ),
            MemberInfo(
                'nested', DummyNested.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'nested'
                }
            ),
            MemberInfo(
                'text', TypeInfo('string', str),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'text'
                }
            ),
            MemberInfo(
                'unionArray', DummyUnion.type_info(),
                attributes={
                    MemberAttribute.PROPERTY_NAME : 'union_array',
                    MemberAttribute.ARRAY_LENGTH : None
                }
            )
        ]
        attribute_list = {
            TypeAttribute.FIELDS : field_list
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
        return self._union_array

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

    def begin_compound(self, compound, member_info):
        self._captures["begin_compound"].append(compound)

    def end_compound(self, compound, member_info):
        self._captures["end_compound"].append(compound)

    def visit_value(self, value, member_info):
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

    def before_compound(self, compound, member_info):
        return self._config["before_compound"]

    def after_compound(self, compound, member_info):
        if self._config["only_first_element"] and self._is_first_element:
            return False
        self._is_first_element = False
        return self._config["after_compound"]

    def before_value(self, value, member_info):
        return self._config["before_value"]

    def after_value(self, value, member_info):
        return self._config["after_value"]

class WalkerTest(unittest.TestCase):

    def test_observer(self):
        observer = Walker.Observer()

        dummy_object = object()
        dummy_member_info = MemberInfo("dummy", TypeInfo("dummy", None))

        with self.assertRaises(NotImplementedError):
            observer.begin_root(dummy_object)
        with self.assertRaises(NotImplementedError):
            observer.end_root(dummy_object)
        with self.assertRaises(NotImplementedError):
            observer.begin_array([], dummy_member_info)
        with self.assertRaises(NotImplementedError):
            observer.end_array([], dummy_member_info)
        with self.assertRaises(NotImplementedError):
            observer.begin_compound(dummy_object, dummy_member_info)
        with self.assertRaises(NotImplementedError):
            observer.end_compound(dummy_object, dummy_member_info)
        with self.assertRaises(NotImplementedError):
            observer.visit_value("", dummy_member_info)

    def test_filter(self):
        walk_filter = Walker.Filter()

        dummy_object = object()
        dummy_member_info = MemberInfo("dummy", TypeInfo("dummy", None))

        with self.assertRaises(NotImplementedError):
            walk_filter.before_array([], dummy_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.after_array([], dummy_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.before_compound(dummy_object, dummy_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.after_compound(dummy_object, dummy_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.before_value("", dummy_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.after_value("", dummy_member_info)

    def test_walk_without_type_info(self):
        walker = Walker(TestObserver())
        obj = object()
        with self.assertRaises(PythonRuntimeException):
            walker.walk(obj)

    def test_walk(self):
        observer = TestObserver()
        walker = Walker(observer)
        obj = self._create_dummy_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([obj.union_array], observer.captures["begin_array"])
        self.assertEqual([obj.union_array], observer.captures["end_array"])
        self.assertEqual([obj.nested, obj.union_array[0], obj.union_array[1]],
                         observer.captures["begin_compound"])
        self.assertEqual([obj.nested, obj.union_array[0], obj.union_array[1]],
                         observer.captures["end_compound"])
        self.assertEqual([13, "nested", "test", '1', 2], observer.captures["visit_value"])

    def test_walk_skip_compound(self):
        observer = TestObserver()
        test_filter = TestFilter(before_array=True, after_array=True,
                                 before_compound=False, after_compound=True,
                                 before_value=True, after_value=True)
        walker = Walker(observer, WalkFilter().add(test_filter))
        obj = self._create_dummy_object()
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
        obj = self._create_dummy_object()
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
        obj = self._create_dummy_object()
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
        obj = self._create_dummy_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([obj.union_array], observer.captures["begin_array"])
        self.assertEqual([obj.union_array], observer.captures["end_array"])
        self.assertEqual([obj.nested, obj.union_array[0]], observer.captures["begin_compound"])
        self.assertEqual([obj.nested, obj.union_array[0]], observer.captures["end_compound"])
        self.assertEqual([13, "nested", "test", '1'], observer.captures["visit_value"])

    def test_depth_filter_1(self):
        observer = TestObserver()
        walker = Walker(observer, WalkFilter().add(WalkFilter.DepthFilter(1)))
        obj = self._create_dummy_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([obj.union_array], observer.captures["begin_array"])
        self.assertEqual([obj.union_array], observer.captures["end_array"])
        self.assertEqual([obj.nested, obj.union_array[0], obj.union_array[1]],
                         observer.captures["begin_compound"])
        self.assertEqual([obj.nested, obj.union_array[0], obj.union_array[1]],
                         observer.captures["end_compound"])
        self.assertEqual([13, "test"], observer.captures["visit_value"])


    @staticmethod
    def _create_dummy_object():
        return DummyObject(13, DummyNested("nested"), "test", [DummyUnion(text_="1"), DummyUnion(value_=2)])
