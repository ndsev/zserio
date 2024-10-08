"""
Test utilities.
"""

import os
import re
import sys
import importlib
import subprocess
import inspect


class TestConfig:
    """
    Test config as a special singleton, init must be called before any access to the configuration
    values via TestConfig[key].
    """

    def __new__(cls):
        raise RuntimeError("TestConfig constructor is forbidden! Use init method instead.")

    @classmethod
    def init(cls, configDict):
        if not hasattr(cls, "instance"):
            cls.instance = object.__new__(cls)
        cls.instance.configDict = configDict  # pylint: disable=attribute-defined-outside-init

    def __class_getitem__(cls, key):
        return cls._get().configDict[key]

    @staticmethod
    def _get():
        if not hasattr(TestConfig, "instance"):
            raise RuntimeError("TestConfig was not initialized!")
        return TestConfig.instance


# set containing all compiled main zs files to prevent multiple compilations of the same zserio sources
COMPILED_ZS = {}  # keys are zs definition tuples: (zsDir, mainZsFile), value is zserio tool error log


def getZserioApi(
    testFile,
    mainZsFile,
    *,
    hasPackage=True,
    hasApi=True,
    topLevelPackage=None,
    extraArgs=None,
    expectedWarnings=0,
    errorOutputDict=None,
):
    """
    Compiles given zserio source and gets Zserio API.

    :param testFile: Current test file (i.e. test case).
    :param mainZsFile: Main zserio source file for the current test suite.
    :param hasPackage: Whether the mainZsFile has a package definition. Default is True.
    :param hasApi: Whether the api.py is supposed to be generated. Default is True.
    :param topLevelPackage: Top level package. By default it's guessed from the mainZsFile.
    :param extraArgs: Extra arguments to zserio compiler.
    :param expectedWarnings: Number of expected zserio warnings to check.
    :param errorOutputDict: Dictionary where to store error output from zserio compiler (key is the mainZsFile).
    :returns: Generated python API if available, None otherwise.
    """
    testDir = os.path.dirname(testFile)  # current test directory
    zsDir = getZsDir(testDir)  # directory where test zs files are located
    apiDir = getApiDir(testDir)

    zsDef = (zsDir, mainZsFile)
    if zsDef not in COMPILED_ZS:
        zserioResult = _compileZserio(zsDef, apiDir, _processExtraArgs(extraArgs))
        COMPILED_ZS[zsDef] = zserioResult.stderr

    _checkExpectedWarnings(COMPILED_ZS[zsDef], expectedWarnings)
    if errorOutputDict is not None:
        errorOutputDict[mainZsFile] = COMPILED_ZS[zsDef]

    apiModule = "api"
    if hasPackage:
        # we need to find out the first left most part of path
        if topLevelPackage is not None:
            apiModulePathPrefix = topLevelPackage.split(".")[0]
        else:
            # normalize mainZs path for current OS
            mainZsWithoutExt = os.path.splitext(mainZsFile)[0].replace("/", os.sep)
            apiModulePathPrefix = mainZsWithoutExt.split(os.sep)[0]

        apiModule = apiModulePathPrefix + "." + apiModule

    if hasApi:
        return _importModule(apiDir, apiModule)
    else:
        return None


def getZsDir(testDir):
    """
    Gets directory where the Zserio schema for is current test suite is located.

    :param testDir: Current test directory.
    :returns: Directory where the Zserio schema for the current test suite is located.
    """

    testDataDir = TestConfig["test_data_dir"]  # test data root directory
    testSuiteName = getTestSuiteName(testDir)
    return os.path.join(testDataDir, testSuiteName, "zs")


def getApiDir(testDir):
    """
    Gets directory where the API for current test suite will be generated.

    :param testDir: Current test directory.
    :returns: Directory where the API for current test suite will be generated.
    """

    buildDir = TestConfig["build_dir"]  # python test root build directory
    testSuiteName = getTestSuiteName(testDir)
    return os.path.join(buildDir, testSuiteName)


def getTestSuiteName(testDir):
    """
    Extracts test suite name from the given directory structure.

    Example:
    testDir = "/home/user/zserio/tests/language/enumeration_types/python
    testSuiteName = getTestSuiteName(testDir)
    print(testSuiteName) # prints "language/enumeration_types"

    :param testDir: Current test directory.
    :returns: Test suite name.
    """

    testDir = os.path.dirname(testDir)  # python dir
    testDir, secondDir = os.path.split(testDir)
    firstDir = os.path.split(testDir)[1]
    return os.path.join(firstDir, secondDir)


def getTestCaseName(testName):
    """
    Extracts test case name from the given test name.

    Example:
    testName = "EmptyUnionTest"
    testCaseName = getTestCaseName(testName)
    print(testCaseName) # prints "empty_union"

    :param testName: Test class name.
    :returns: Test case name in camel case.
    """

    fixedReplacements = [
        ("Test$", ""),
        ("VarInt", "Varint"),
        ("VarUInt", "Varuint"),
        ("VarSize", "Varsize"),
        ("UInt", "Uint"),
        ("BitField", "Bitfield"),
        ("BuiltIn", "Builtin"),
        ("RowId", "Rowid"),
        ("UInt(\\d+)", "Uint\\1"),
        ("LengthOf", "Lengthof"),
        ("^IsSetOperator", "IssetOperator"),
        ("^NumBitsOperator", "NumbitsOperator"),
        ("^ValueOfOperator", "ValueofOperator"),
    ]

    testCaseName = testName

    for pattern, replacement in fixedReplacements:
        testCaseName = re.sub(pattern, replacement, testCaseName)

    return _camelCaseToSnakeCase(testCaseName)


def compileErroneousZserio(testFile, mainZsFile, errorOutputDict, extraArgs=None):
    """
    Compiles given zserio source and gets error output.

    :param testFile: Current test file (i.e. test case).
    :param mainZsFile: Main zserio source file for the current test suite.
    :param errorOutputDict: Dictionary where to store error output from zserio compiler (key is the mainZsFile).
    :param extraArgs: Extra arguments to zserio compiler.
    """

    testDir = os.path.dirname(testFile)  # current test directory
    zsDir = getZsDir(testDir)  # directory where test zs files are located
    apiDir = getApiDir(testDir)
    zsDef = (zsDir, mainZsFile)
    try:
        _compileZserio(zsDef, apiDir, _processExtraArgs(extraArgs))
    except ZserioCompilerError as zserioCompilerError:
        errorOutputDict[mainZsFile] = zserioCompilerError.stderr


def assertErrorsPresent(test, mainZsFile, expectedErrors):
    """
    Checks error output from zserio compiler for the test.

    :param test: Current test.
    :param mainZsFile: Main zserio source file for the current test suite.
    :param expectedErrors: List of expected error messages (in the right order!).
    """

    _assertMessagesPresent(test, mainZsFile, test.errors, expectedErrors, "error")


def assertWarningsPresent(test, mainZsFile, expectedWarnings):
    """
    Checks warning output from zserio compiler for the test.

    :param test: Current test.
    :param mainZsFile: Main zserio source file for the current test suite.
    :param expectedWarnings: List of expected warning messages (in the right order!).
    """

    _assertMessagesPresent(test, mainZsFile, test.warnings, expectedWarnings, "warning")


def assertMethodPresent(test, userType, method):
    """
    Checks that the method name is present in the given user defined type.

    :param test: Current test.
    :param userType: User defined type.
    :param method: The method to check.
    """

    test.assertTrue(
        hasattr(userType, method),
        msg=f"Method '{method}' is not present in '{userType.__name__}'! {_assertLocation()}",
    )


def assertMethodNotPresent(test, userType, method):
    """
    Checks that the method name is not present in the given user defined type.

    :param test: Current test.
    :param userType: User defined type.
    :param method: The method to check.
    """

    test.assertFalse(
        hasattr(userType, method),
        msg=f"Method '{method}' is present in '{userType.__name__}'! {_assertLocation()}",
    )


def assertPropertyPresent(test, userType, prop, *, readOnly):
    """
    Checks that the property name is present in the given user defined type.

    :param test: Current test.
    :param userType: User defined type.
    :param prop: The property to check.
    :param readOnly: Whether the property shall be read only.
    """

    test.assertTrue(
        hasattr(userType, prop),
        msg=f"Property '{prop}' is not present in '{userType.__name__}'! {_assertLocation()}",
    )
    propAttr = getattr(userType, prop)
    test.assertTrue(
        isinstance(propAttr, property),
        msg=f"Attribute '{prop}' is not a property in '{userType.__name__}'! {_assertLocation()}",
    )
    test.assertIsNotNone(
        propAttr.fget, msg=f"Property '{prop}' getter is not set in '{userType.__name__}'! {_assertLocation()}"
    )
    if readOnly:
        test.assertIsNone(
            propAttr.fset,
            msg=f"Read-only property '{prop}' setter is set in '{userType.__name__}'! {_assertLocation()}",
        )
    else:
        test.assertIsNotNone(
            propAttr.fset,
            msg=f"Property '{prop}' setter is not set in '{userType.__name__}'! {_assertLocation()}",
        )


class ZserioCompilerError(Exception):
    """
    Zserio compiler error.
    """

    def __init__(self, stderr):
        """
        Constructor.

        :param stderr: Error output of the Zserio compiler.
        """

        super().__init__("Zserio compilation failed!")
        self._stderr = stderr

    @property
    def stderr(self):
        """
        Error output of the Zserio compiler.
        """

        return self._stderr


def _processExtraArgs(extraArgs):
    """
    Processes given zserio extra arguments.

    :param extraArgs: Zserio extra arguments given by user or None.
    :returns: Processed zserio extra arguments.
    """

    if extraArgs is None:
        extraArgs = []
    if "ZSERIO_EXTRA_ARGS" in os.environ:
        extraArgs += os.environ["ZSERIO_EXTRA_ARGS"].split(" ")

    return extraArgs


def _compileZserio(zsDef, apiDir, extraArgs):
    """
    Compiles test zserio sources for the current python test file (i.e. test suite) and
    directly imports the generated python sources.

    :param zsDef: Tuple defining the zs source to compile (zsDir, mainZsFile).
    :param apiDir: Output directory for the generated API.
    :param extraArgs: Extra arguments to zserio compiler.
    :returns: CompletedProcess containing zserio result.

    :raises RuntimeError: When zserio tool fails.
    """

    zserioLibsDir = os.path.join(TestConfig["release_dir"], "zserio_libs")
    zserioCore = os.path.join(zserioLibsDir, "zserio_core.jar")
    zserioPython = os.path.join(zserioLibsDir, "zserio_python.jar")
    zserioCmd = [
        TestConfig["java"],
        "-cp",
        os.pathsep.join([zserioCore, zserioPython]),
        "zserio.tools.ZserioTool",
        "-python",
        f"{apiDir}",  # pythonDir
        "-src",
        f"{zsDef[0]}",  # apiDir
        zsDef[1],
    ]
    zserioCmd += extraArgs
    zserioResult = subprocess.run(zserioCmd, capture_output=True, text=True, check=False)
    if zserioResult.stdout:
        print(zserioResult.stdout)
    if zserioResult.stderr:
        print(zserioResult.stderr)
    if zserioResult.returncode != 0:
        raise ZserioCompilerError(zserioResult.stderr)
    return zserioResult


def _checkExpectedWarnings(zserioLog, expectedWarnings):
    """
    Checks that the zserio tool log contains expected number of warnings.

    :zserioLog: Zserio tool error log to check.
    :expectedWarnings: Number of expected zserio warnings.
    """

    content = zserioLog if zserioLog is not None else ""
    numWarnings = content.count("[WARNING]")

    if numWarnings != expectedWarnings:
        raise RuntimeError(f"Zserio tool produced {numWarnings} warnings (expected {expectedWarnings})!")


def _importModule(path, modulePath):
    """
    Imports a module specified by the given path and modulePath.

    :path: Root path for the requested module.
    :modulePath: Path to the requested module with respect to the path.
    :returns: Loaded module.
    """

    sys.path.append(path)
    api = importlib.import_module(modulePath)
    sys.path.remove(path)
    return api


def _assertLocation(depth=2):
    frame = inspect.currentframe()
    for _ in range(depth):
        frame = frame.f_back
    return f"{frame.f_code.co_filename}:{frame.f_lineno}"


def _assertMessagesPresent(test, mainZsFile, errorOutputDict, expectedMessages, messageType):
    """
    Checks error output from zserio compiler for the test.

    :param test: Current test.
    :param mainZsFile: Main zserio source file for the current test suite.
    :param errorOutputDict: Dictionary containing error output captured by the current test suite.
    :param expectedMessages: List of expected messages (in the right order!).
    :param messageType: String defining type of the message (e.g. error or warning).
    """

    test.assertIn(mainZsFile, errorOutputDict, msg=f"No error found for '{mainZsFile}'!")
    testErrorOutput = errorOutputDict[mainZsFile]

    lastIndex = 0
    for expectedMessage in expectedMessages:
        index = testErrorOutput.find(expectedMessage, lastIndex)
        if index < lastIndex:
            if index == -1:
                test.fail(f"Expected {messageType} not found! ('{expectedMessage}')")
            else:
                test.fail(f"Expected {messageType} found in wrong order! ('{expectedMessage}')")


def _camelCaseToSnakeCase(camelCase):
    camelCase = re.sub("([a-z])([A-Z])", "\\1_\\2", camelCase)
    camelCase = re.sub("([0-9A-Z])([A-Z][a-z])", "\\1_\\2", camelCase).lower()
    return camelCase
