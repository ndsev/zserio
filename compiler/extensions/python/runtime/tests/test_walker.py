import unittest

from test_object.api import WalkerBitmask, WalkerNested, WalkerObject, WalkerUnion

from zserio import (
    WalkObserver,
    WalkFilter,
    Walker,
    DefaultWalkObserver,
    PythonRuntimeException,
    ArrayLengthWalkFilter,
    DepthWalkFilter,
    RegexWalkFilter,
    AndWalkFilter,
)
from zserio.typeinfo import TypeInfo, TypeAttribute, MemberInfo, MemberAttribute


class TestWalkObserver(WalkObserver):

    def __init__(self):
        self._captures = {
            "begin_root": None,
            "end_root": None,
            "begin_array": [],
            "end_array": [],
            "begin_compound": [],
            "end_compound": [],
            "visit_value": [],
        }

    @property
    def captures(self):
        return self._captures

    def begin_root(self, compound):
        self._captures["begin_root"] = compound

    def end_root(self, compound):
        self._captures["end_root"] = compound

    def begin_array(self, array, member_info):
        self._captures["begin_array"].append(array)

    def end_array(self, array, member_info):
        self._captures["end_array"].append(array)

    def begin_compound(self, compound, member_info, element_index=None):
        self._captures["begin_compound"].append(compound)

    def end_compound(self, compound, member_info, element_index=None):
        self._captures["end_compound"].append(compound)

    def visit_value(self, value, member_info, element_index=None):
        self._captures["visit_value"].append(value)


class TestWalkFilter(WalkFilter):

    def __init__(
        self,
        *,
        before_array=True,
        after_array=True,
        only_first_element=False,
        before_compound=True,
        after_compound=True,
        before_value=True,
        after_value=True
    ):
        self._config = {
            "before_array": before_array,
            "after_array": after_array,
            "only_first_element": only_first_element,
            "before_compound": before_compound,
            "after_compound": after_compound,
            "before_value": before_value,
            "after_value": after_value,
        }
        self._is_first_element = False

    def before_array(self, array, member_info):
        self._is_first_element = True
        return self._config["before_array"]

    def after_array(self, array, member_info):
        self._is_first_element = False
        return self._config["after_array"]

    def before_compound(self, compound, member_info, element_index=None):
        return self._config["before_compound"]

    def after_compound(self, compound, member_info, element_index=None):
        go_to_next = not (self._config["only_first_element"] and self._is_first_element)
        self._is_first_element = False
        return go_to_next and self._config["after_compound"]

    def before_value(self, value, member_info, element_index=None):
        return self._config["before_value"]

    def after_value(self, value, member_info, element_index=None):
        return self._config["after_value"]


class WalkerTest(unittest.TestCase):

    def test_observer(self):
        observer = WalkObserver()

        walker_object = object()
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))

        with self.assertRaises(NotImplementedError):
            observer.begin_root(walker_object)
        with self.assertRaises(NotImplementedError):
            observer.end_root(walker_object)
        with self.assertRaises(NotImplementedError):
            observer.begin_array([], walker_member_info)
        with self.assertRaises(NotImplementedError):
            observer.end_array([], walker_member_info)
        with self.assertRaises(NotImplementedError):
            observer.begin_compound(walker_object, walker_member_info)
        with self.assertRaises(NotImplementedError):
            observer.end_compound(walker_object, walker_member_info)
        with self.assertRaises(NotImplementedError):
            observer.visit_value("", walker_member_info)

    def test_filter(self):
        walk_filter = WalkFilter()

        walker_object = object()
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))

        with self.assertRaises(NotImplementedError):
            walk_filter.before_array([], walker_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.after_array([], walker_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.before_compound(walker_object, walker_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.after_compound(walker_object, walker_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.before_value("", walker_member_info)
        with self.assertRaises(NotImplementedError):
            walk_filter.after_value("", walker_member_info)

    def test_walk_without_type_info(self):
        walker = Walker(TestWalkObserver())
        obj = object()
        with self.assertRaises(PythonRuntimeException):
            walker.walk(obj)

    def test_walk_non_compound(self):
        walker = Walker(TestWalkObserver())
        with self.assertRaises(PythonRuntimeException):
            walker.walk(WalkerBitmask())

    def test_walk(self):
        observer = TestWalkObserver()
        walker = Walker(observer)
        obj = _create_walker_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual(
            [obj.union_array, obj.union_array[2].nested_array],
            observer.captures["begin_array"],
        )
        self.assertEqual(
            [obj.union_array[2].nested_array, obj.union_array],
            observer.captures["end_array"],
        )
        self.assertEqual(
            [
                obj.nested,
                obj.union_array[0],
                obj.union_array[1],
                obj.union_array[2],
                obj.union_array[2].nested_array[0],
            ],
            observer.captures["begin_compound"],
        )
        self.assertEqual(
            [
                obj.nested,
                obj.union_array[0],
                obj.union_array[1],
                obj.union_array[2].nested_array[0],
                obj.union_array[2],
            ],
            observer.captures["end_compound"],
        )
        self.assertEqual(
            [13, "nested", "test", "1", 2, "nestedArray", None],
            observer.captures["visit_value"],
        )

    def test_walk_skip_compound(self):
        observer = TestWalkObserver()
        test_filter = TestWalkFilter(
            before_array=True,
            after_array=True,
            before_compound=False,
            after_compound=True,
            before_value=True,
            after_value=True,
        )
        walker = Walker(observer, AndWalkFilter([test_filter]))
        obj = _create_walker_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([obj.union_array], observer.captures["begin_array"])
        self.assertEqual([obj.union_array], observer.captures["end_array"])
        self.assertEqual([], observer.captures["begin_compound"])
        self.assertEqual([], observer.captures["end_compound"])
        self.assertEqual([13, "test", None], observer.captures["visit_value"])

    def test_walk_skip_siblings(self):
        observer = TestWalkObserver()
        test_filter = TestWalkFilter(
            before_array=True,
            after_array=True,
            before_compound=True,
            after_compound=True,
            before_value=True,
            after_value=False,
        )
        walker = Walker(observer, AndWalkFilter([test_filter]))
        obj = _create_walker_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([], observer.captures["begin_array"])
        self.assertEqual([], observer.captures["end_array"])
        self.assertEqual([], observer.captures["begin_compound"])
        self.assertEqual([], observer.captures["end_compound"])
        self.assertEqual([13], observer.captures["visit_value"])

    def test_walk_skip_after_nested(self):
        observer = TestWalkObserver()
        test_filter = TestWalkFilter(
            before_array=True,
            after_array=True,
            before_compound=True,
            after_compound=False,
            before_value=True,
            after_value=True,
        )
        walker = Walker(observer, AndWalkFilter([test_filter]))
        obj = _create_walker_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([], observer.captures["begin_array"])
        self.assertEqual([], observer.captures["end_array"])
        self.assertEqual([obj.nested], observer.captures["begin_compound"])
        self.assertEqual([obj.nested], observer.captures["end_compound"])
        self.assertEqual([13, "nested"], observer.captures["visit_value"])

    def test_walk_only_first_element(self):
        observer = TestWalkObserver()
        test_filter = TestWalkFilter(
            before_array=True,
            after_array=True,
            only_first_element=True,
            before_compound=True,
            after_compound=True,
            before_value=True,
            after_value=True,
        )
        walker = Walker(observer, AndWalkFilter([test_filter]))
        obj = _create_walker_object()
        walker.walk(obj)
        self.assertEqual(obj, observer.captures["begin_root"])
        self.assertEqual(obj, observer.captures["end_root"])
        self.assertEqual([obj.union_array], observer.captures["begin_array"])
        self.assertEqual([obj.union_array], observer.captures["end_array"])
        self.assertEqual([obj.nested, obj.union_array[0]], observer.captures["begin_compound"])
        self.assertEqual([obj.nested, obj.union_array[0]], observer.captures["end_compound"])
        self.assertEqual([13, "nested", "test", "1", None], observer.captures["visit_value"])


class DefaultObserverTest(unittest.TestCase):

    @staticmethod
    def test_default():
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))
        default_observer = DefaultWalkObserver()

        default_observer.begin_root(object())
        default_observer.end_root(object())
        default_observer.begin_array([], walker_member_info)
        default_observer.end_array([], walker_member_info)
        default_observer.begin_compound(object(), walker_member_info)
        default_observer.end_compound(object(), walker_member_info)
        default_observer.visit_value(None, walker_member_info)


class DepthWalkFilterTest(unittest.TestCase):

    def test_depth_0(self):
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))
        depth_filter = DepthWalkFilter(0)

        self.assertFalse(depth_filter.before_array([], walker_member_info))
        self.assertTrue(depth_filter.after_array([], walker_member_info))

        self.assertFalse(depth_filter.before_compound(object(), walker_member_info))
        self.assertTrue(depth_filter.after_compound(object(), walker_member_info))

        self.assertFalse(depth_filter.before_value(None, walker_member_info))
        self.assertTrue(depth_filter.after_value(None, walker_member_info))

    def test_depth_1(self):
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))
        depth_filter = DepthWalkFilter(1)

        self.assertTrue(depth_filter.before_array([], walker_member_info))  # 0
        self.assertFalse(depth_filter.before_array([], walker_member_info))  # 1
        self.assertTrue(depth_filter.after_array([], walker_member_info))  # 1
        self.assertFalse(depth_filter.before_compound(object(), walker_member_info, 0))  # 1
        self.assertTrue(depth_filter.after_compound(object(), walker_member_info, 0))  # 1
        self.assertFalse(depth_filter.before_value(None, walker_member_info, 1))  # 1
        self.assertTrue(depth_filter.after_value(None, walker_member_info, 1))  # 1
        self.assertTrue(depth_filter.after_array([], walker_member_info))  # 0

        self.assertTrue(depth_filter.before_compound(object(), walker_member_info))  # 0
        self.assertFalse(depth_filter.before_array([], walker_member_info))  # 1
        self.assertTrue(depth_filter.after_array([], walker_member_info))  # 1
        self.assertFalse(depth_filter.before_compound(object(), walker_member_info))  # 1
        self.assertTrue(depth_filter.after_compound(object(), walker_member_info))  # 1
        self.assertFalse(depth_filter.before_value(None, walker_member_info))  # 1
        self.assertTrue(depth_filter.after_value(None, walker_member_info))  # 1
        self.assertTrue(depth_filter.after_compound(object(), walker_member_info))  # 0

        self.assertTrue(depth_filter.before_value(None, walker_member_info))  # 0
        self.assertTrue(depth_filter.after_value(None, walker_member_info))  # 0


class RegexWalkFilterTest(unittest.TestCase):

    def test_regex_all_match(self):
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))
        walker_array_member_info = MemberInfo(
            "walker",
            TypeInfo("Walker", None),
            attributes={MemberAttribute.ARRAY_LENGTH: None},
        )
        regex_filter = RegexWalkFilter(".*")

        self.assertTrue(regex_filter.before_array([], walker_array_member_info))
        self.assertTrue(regex_filter.after_array([], walker_array_member_info))
        self.assertTrue(regex_filter.before_compound(object(), walker_member_info))
        self.assertTrue(regex_filter.before_compound(object(), walker_member_info))
        self.assertTrue(regex_filter.before_value(None, walker_member_info))
        self.assertTrue(regex_filter.after_value(None, walker_member_info))

    def test_regex_prefix_match(self):
        walker_object = _create_walker_object()
        regex_filter = RegexWalkFilter("nested\\..*")

        identifier_member_info = walker_object.type_info().attributes[TypeAttribute.FIELDS][0]
        self.assertFalse(regex_filter.before_value(walker_object.identifier, identifier_member_info))
        self.assertTrue(regex_filter.after_value(walker_object.identifier, identifier_member_info))

        nested_member_info = walker_object.type_info().attributes[TypeAttribute.FIELDS][1]
        self.assertTrue(regex_filter.before_compound(walker_object.nested, nested_member_info))
        text_member_info = nested_member_info.type_info.attributes[TypeAttribute.FIELDS][0]
        self.assertTrue(regex_filter.before_value(walker_object.nested.text, text_member_info))
        self.assertTrue(regex_filter.after_value(walker_object.nested.text, text_member_info))
        self.assertTrue(regex_filter.after_compound(walker_object.nested, nested_member_info))

        # ignore text

        union_array_member_info = walker_object.type_info().attributes[TypeAttribute.FIELDS][3]
        self.assertFalse(regex_filter.before_array(walker_object.union_array, union_array_member_info))
        self.assertTrue(regex_filter.after_array(walker_object.union_array, union_array_member_info))

    def test_regex_array_match(self):
        walker_object = _create_walker_object()
        regex_filter = RegexWalkFilter("unionArray\\[\\d+\\]\\.nes.*")

        union_array_member_info = walker_object.type_info().attributes[TypeAttribute.FIELDS][3]
        self.assertTrue(regex_filter.before_array(walker_object.union_array, union_array_member_info))

        self.assertFalse(regex_filter.before_compound(walker_object.union_array[0], union_array_member_info, 0))
        self.assertTrue(regex_filter.after_compound(walker_object.union_array[0], union_array_member_info, 0))

        self.assertFalse(regex_filter.before_compound(walker_object.union_array[1], union_array_member_info, 1))
        self.assertTrue(regex_filter.after_compound(walker_object.union_array[1], union_array_member_info, 1))

        self.assertTrue(regex_filter.before_compound(walker_object.union_array[2], union_array_member_info, 2))
        self.assertTrue(regex_filter.after_compound(walker_object.union_array[2], union_array_member_info, 2))

        self.assertTrue(regex_filter.after_array(walker_object.union_array, union_array_member_info))

    def test_regex_array_no_match(self):
        walker_object = WalkerObject(
            13,
            WalkerNested("nested"),
            "test",
            [WalkerUnion(nested_array_=[WalkerNested("nestedArray")])],
        )

        regex_filter = RegexWalkFilter("^unionArray\\[\\d*\\]\\.te.*")

        union_array_member_info = walker_object.type_info().attributes[TypeAttribute.FIELDS][3]
        self.assertFalse(regex_filter.before_array(walker_object.union_array, union_array_member_info))
        self.assertTrue(regex_filter.after_array(walker_object.union_array, union_array_member_info))

    def test_regex_none_compound_match(self):
        walker_object = WalkerObject(13, None, "test", [WalkerUnion(text_="1"), WalkerUnion(value_=2)])

        regex_filter = RegexWalkFilter("nested")

        nested_member_info = walker_object.type_info().attributes[TypeAttribute.FIELDS][1]
        # note that the None compounds are processed as values!
        self.assertTrue(regex_filter.before_value(walker_object.nested, nested_member_info))
        self.assertTrue(regex_filter.after_value(walker_object.nested, nested_member_info))

    def test_regex_none_compound_no_match(self):
        walker_object = WalkerObject(13, None, "test", [WalkerUnion(text_="1"), WalkerUnion(value_=2)])

        regex_filter = RegexWalkFilter("^nested\\.text$")

        nested_member_info = walker_object.type_info().attributes[TypeAttribute.FIELDS][1]
        # note that the None compounds are processed as values!
        self.assertFalse(regex_filter.before_value(walker_object.nested, nested_member_info))
        self.assertTrue(regex_filter.after_value(walker_object.nested, nested_member_info))

    def test_regex_none_array_match(self):
        regex_filter = RegexWalkFilter("optionalUnionArray")

        optional_union_array_member_info = WalkerObject.type_info().attributes[TypeAttribute.FIELDS][4]
        # note that the None arrays are processed as values!
        self.assertTrue(regex_filter.before_value(None, optional_union_array_member_info))
        self.assertTrue(regex_filter.after_value(None, optional_union_array_member_info))

    def test_regex_none_array_no_match(self):
        regex_filter = RegexWalkFilter("^optionalUnionArray\\[\\d+\\]\\.nestedArray.*")

        union_array_member_info = WalkerObject.type_info().attributes[TypeAttribute.FIELDS][3]
        # note that the None arrays are processed as values!
        self.assertFalse(regex_filter.before_value(None, union_array_member_info))
        self.assertTrue(regex_filter.after_value(None, union_array_member_info))


class ArrayLengthWalkFilterTest(unittest.TestCase):

    def test_array_length_0(self):
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))
        walker_array_member_info = MemberInfo(
            "walker_array",
            TypeInfo("Walker", None),
            attributes={MemberAttribute.ARRAY_LENGTH: None},
        )
        array_length_filter = ArrayLengthWalkFilter(0)

        self.assertTrue(array_length_filter.before_array([], walker_array_member_info))
        self.assertFalse(array_length_filter.before_compound(object(), walker_array_member_info, 0))
        self.assertFalse(array_length_filter.after_compound(object(), walker_array_member_info, 0))
        self.assertFalse(array_length_filter.before_value(None, walker_array_member_info, 1))
        self.assertFalse(array_length_filter.after_value(None, walker_array_member_info, 1))
        self.assertTrue(array_length_filter.after_array([], walker_array_member_info))

        self.assertTrue(array_length_filter.before_compound(object(), walker_member_info))
        self.assertTrue(array_length_filter.before_value(None, walker_member_info))
        self.assertTrue(array_length_filter.after_value(None, walker_member_info))
        self.assertTrue(array_length_filter.before_array([], walker_array_member_info))
        self.assertFalse(array_length_filter.before_value(None, walker_array_member_info, 0))
        self.assertFalse(array_length_filter.after_value(None, walker_array_member_info, 0))
        self.assertTrue(array_length_filter.after_array([], walker_array_member_info))
        self.assertTrue(array_length_filter.after_compound(object(), walker_member_info))


class AndWalkFilterTest(unittest.TestCase):

    def test_empty(self):
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))
        walk_filter = AndWalkFilter([])

        self.assertTrue(walk_filter.before_array([], walker_member_info))
        self.assertTrue(walk_filter.after_array([], walker_member_info))
        self.assertTrue(walk_filter.before_compound(object(), walker_member_info))
        self.assertTrue(walk_filter.after_compound(object(), walker_member_info))
        self.assertTrue(walk_filter.before_value(None, walker_member_info))
        self.assertTrue(walk_filter.after_value(None, walker_member_info))

    def test_true_true(self):
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))
        # two filters returning True
        walk_filter = AndWalkFilter([TestWalkFilter(), TestWalkFilter()])

        self.assertTrue(walk_filter.before_array([], walker_member_info))
        self.assertTrue(walk_filter.after_array([], walker_member_info))
        self.assertTrue(walk_filter.before_compound(object(), walker_member_info))
        self.assertTrue(walk_filter.after_compound(object(), walker_member_info))
        self.assertTrue(walk_filter.before_value(None, walker_member_info))
        self.assertTrue(walk_filter.after_value(None, walker_member_info))

    def test_false_false(self):
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))
        # two filters returning False
        walk_filter = AndWalkFilter(
            [
                TestWalkFilter(
                    before_array=False,
                    after_array=False,
                    before_compound=False,
                    after_compound=False,
                    before_value=False,
                    after_value=False,
                ),
                TestWalkFilter(
                    before_array=False,
                    after_array=False,
                    before_compound=False,
                    after_compound=False,
                    before_value=False,
                    after_value=False,
                ),
            ]
        )

        self.assertFalse(walk_filter.before_array([], walker_member_info))
        self.assertFalse(walk_filter.after_array([], walker_member_info))
        self.assertFalse(walk_filter.before_compound(object(), walker_member_info))
        self.assertFalse(walk_filter.after_compound(object(), walker_member_info))
        self.assertFalse(walk_filter.before_value(None, walker_member_info))
        self.assertFalse(walk_filter.after_value(None, walker_member_info))

    def test_true_false(self):
        walker_member_info = MemberInfo("walker", TypeInfo("Walker", None))
        walk_filter = AndWalkFilter(
            [
                TestWalkFilter(),  # returning true
                TestWalkFilter(
                    before_array=False,
                    after_array=False,
                    before_compound=False,
                    after_compound=False,
                    before_value=False,
                    after_value=False,
                ),  # returning false
            ]
        )

        self.assertFalse(walk_filter.before_array([], walker_member_info))
        self.assertFalse(walk_filter.after_array([], walker_member_info))
        self.assertFalse(walk_filter.before_compound(object(), walker_member_info))
        self.assertFalse(walk_filter.after_compound(object(), walker_member_info))
        self.assertFalse(walk_filter.before_value(None, walker_member_info))
        self.assertFalse(walk_filter.after_value(None, walker_member_info))


def _create_walker_object():
    return WalkerObject(
        13,
        WalkerNested("nested"),
        "test",
        [
            WalkerUnion(text_="1"),
            WalkerUnion(value_=2),
            WalkerUnion(nested_array_=[WalkerNested("nestedArray")]),
        ],
    )
