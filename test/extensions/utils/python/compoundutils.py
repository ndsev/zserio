"""
Test utilities.
"""

import zserio
from typing import Type, Any
import pprint
import sys


# for debugging of function calls. Call with sys.settrace(trace_calls)
def trace_calls(frame, event, arg):
    if not hasattr(trace_calls, "level"):
        trace_calls.level = 0  # Initialize on first call

    if event == "call":
        function_name = frame.f_code.co_name  # co_qualname available only in 3.11+
        lineno = frame.f_lineno
        # Get the arguments from the frame's local variables
        args = {k: v for k, v in frame.f_locals.items() if k in frame.f_code.co_varnames}
        print(" " * trace_calls.level * 2, f"!{function_name} {args} line {lineno}")
        trace_calls.level += 1
    elif event == "return":
        print(" " * trace_calls.level * 2, "return ", arg)
        trace_calls.level -= 1

    return trace_calls  # Important: the trace function must return itself


def writeReadTest(clazz: Type[Any], data: Any):
    bitSize = data.bitsizeof()
    bitBuffer = zserio.serialize(data)
    assert bitSize == bitBuffer.bitsize

    readData = zserio.deserialize(clazz, bitBuffer)

    if readData != data:
        pp = pprint.PrettyPrinter(indent=4)
        print("readData.size=", readData.bitsizeof(), "data.size=", bitSize)
        print("readData=")
        pp.pprint(vars(readData))
        print("data=")
        pp.pprint(vars(data))

    assert readData == data


def hashTest(value: Any, hashValue: int, equalValue: Any, diffValue: Any = None, diffHashValue: int = 0):
    # in python x32 hashes are 32bit so trim hash values first
    hashValue %= sys.maxsize
    assert hashValue == hash(value)
    assert hashValue == hash(equalValue)
    if diffValue is not None:
        diffHashValue %= sys.maxsize
        assert hashValue != diffHashValue
        assert diffHashValue == hash(diffValue)


def comparisonOperatorsTest(value: Any, equalValue: Any):
    assert value == equalValue
