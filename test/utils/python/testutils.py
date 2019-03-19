"""
Test utilities.
"""

import os
import sys
import importlib
import subprocess

# global arguments with default values (stored here to allow running of particular tests)
TEST_ARGS = {}
TEST_ARGS["zserio_root_dir"] = os.path.join(os.path.dirname(os.path.realpath(__file__)), "..", "..", "..")
TEST_ARGS["build_dir"] = os.path.join(TEST_ARGS["zserio_root_dir"], "build", "test", "python")
TEST_ARGS["release_dir"] = os.path.join(TEST_ARGS["zserio_root_dir"], "distr")
TEST_ARGS["java"] = "java"

# set containing all compiled main zs files to prevent multiple compilations of the same zserio sources
COMPILED_ZS_SET = set() # contains zs definition tuples: (zsDir, mainZsFile)

def getZserioApi(testFile, mainZsFile, hasPackage=True, hasApi=True, topLevelPackage=None, extraArgs=None):
    """
    :param testFile: Current test file (i.e. test case).
    :param mainZsFile: Main zserio source file for the current test suite.
    :param hasPackage: Whether the mainZsFile has a package definition. Default is True.
    :param hasApi: Whether the api.py is supposed to be generated. Default is True.
    :param topLevelPackage: Top level package. By default it's guessed from the mainZsFile.
    :param extraArgs: Extra arguments to zserio compiler.
    :returns: Generated python API if available, None otherwise.
    """

    testDir = os.path.dirname(testFile) # current test directory
    zsDir = os.path.join(testDir, "..", "zs") # directory where test zs files are located
    apiDir = getApiDir(testDir)

    zsDef = (zsDir, mainZsFile)
    if zsDef not in COMPILED_ZS_SET:
        COMPILED_ZS_SET.add(zsDef)
        _compileZserio(zsDef, apiDir, extraArgs)

    apiModule = "api"
    if hasPackage:
        if topLevelPackage is None:
            topLevelPackage = os.path.splitext(mainZsFile)[0]
        apiModule = topLevelPackage + "." + apiModule

    if hasApi:
        return _importModule(apiDir, apiModule)
    else:
        return None

def getApiDir(testDir):
    """
    Gets directory where the API for current test suite will be generated.

    :param testDir: Current test directory.
    :returns: Directory where the API for current test suite will be generated.
    """

    buildDir = TEST_ARGS["build_dir"] # python test root build directory
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

    testDir = os.path.dirname(testDir) # python dir
    testDir, secondDir = os.path.split(testDir)
    firstDir = os.path.split(testDir)[1]
    return os.path.join(firstDir, secondDir)

def _compileZserio(zsDef, apiDir, extraArgs=None):
    """
    Compiles test zserio sources for the current python test file (i.e. test suite) and
    directly imports the generated python sources.

    :param zsDef: Tuple defining the zs source to compile (zsDir, mainZsFile).
    :param apiDir: Output directory for the generated API.
    :param extraArgs: Extra arguments to zserio compiler.

    :raises Exception: When zserio tool fails.
    """

    if extraArgs is None:
        extraArgs = []
    zserioLibsDir = os.path.join(TEST_ARGS["release_dir"], "zserio_libs")
    zserioCore = os.path.join(zserioLibsDir, "zserio_core.jar")
    zserioPython = os.path.join(zserioLibsDir, "zserio_python.jar")
    zserioCmd = [TEST_ARGS["java"],
                 "-cp",
                 os.pathsep.join([zserioCore, zserioPython]),
                 "zserio.tools.ZserioTool",
                 "-python",
                 "{pythonDir}".format(pythonDir=apiDir),
                 "-src",
                 "{sourceDir}".format(sourceDir=zsDef[0]),
                 zsDef[1]]
    zserioCmd += extraArgs
    zserioResult = subprocess.call(zserioCmd)
    if zserioResult != 0:
        raise Exception("Zserio compilation failed!")

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
