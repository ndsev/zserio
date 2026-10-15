"""
The module implements ordering helpers used by the less than operator of the generated objects.
"""

import typing


def compare(lhs: typing.Any, rhs: typing.Any) -> int:
    """
    Compares two values.

    A value which is not set (None) is less than a value which is set. Generated objects and arrays of the same
    type are compared by their three-way method _zserio_cmp, which visits each nested value only once. Other
    values are compared using only the less than operator.

    :param lhs: Left hand side value.
    :param rhs: Right hand side value.

    :returns: Negative number when lhs is less than rhs, positive number when rhs is less than lhs, else zero.
    """

    if lhs is None:
        return 0 if rhs is None else -1
    if rhs is None:
        return 1
    if type(lhs) is type(rhs) and hasattr(lhs, "_zserio_cmp"):
        return lhs._zserio_cmp(rhs)  # pylint: disable=protected-access
    if lhs < rhs:
        return -1
    if rhs < lhs:
        return 1

    return 0
