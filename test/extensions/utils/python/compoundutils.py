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


def dumpToFile(obj: Any, file_name: str):
    with open(file_name, "w", encoding="utf-8") as file:
        file.write(dump(obj))


def dump(obj: Any, indent_level: int = 0, visited=None) -> str:
    if visited is None:
        visited = set()

    ret = ""
    # Avoid infinite recursion for circular references
    if id(obj) in visited:
        ret += " " * indent_level + f"-> (Circular Reference to {type(obj).__name__})\n"
        return ret
    visited.add(id(obj))

    ret += f"{type(obj).__name__} {{\n"

    # Iterate over instance attributes
    indent_str = "  " * indent_level
    if hasattr(obj, "__dict__"):
        for key, value in obj.__dict__.items():
            if not key.startswith("__"):  # Exclude special attributes
                ret += indent_str + f"  {key}: "
                if hasattr(value, "__dict__") and not isinstance(
                    value, (str, int, float, list, dict, set, tuple)
                ):
                    ret += dump(value, indent_level + 2, visited)
                else:
                    ret += str(value) + "\n"
        ret += indent_str + "}\n"
    else:
        ret += indent_str + "  (No __dict__ attribute, potentially a built-in type)\n"
    return ret


def readTest(bitBuffer: Any, clazz: Type[Any], data: Any, *args):
    bitSize = data.bitsizeof()
    assert bitSize == bitBuffer.bitsize

    readData = zserio.deserialize(clazz, bitBuffer, *args)

    if readData != data:
        pp = pprint.PrettyPrinter(indent=4)
        print("readData.size=", readData.bitsizeof(), "data.size=", bitSize)
        print("readData=")
        pp.pprint(vars(readData))
        print("data=")
        pp.pprint(vars(data))

    assert readData == data


def writeReadTest(clazz: Type[Any], data: Any, *args):
    bitBuffer = zserio.serialize(data)
    readTest(bitBuffer, clazz, data, *args)


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
