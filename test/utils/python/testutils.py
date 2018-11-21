"""
Test utilities.
"""

import os
import sys
import importlib

# global arguments with default values (stored here to allow running of particular tests)
TEST_ARGS = {}
TEST_ARGS["zserio_root_dir"] = os.path.join(os.path.dirname(os.path.realpath(__file__)), "..", "..", "..")
TEST_ARGS["build_dir"] = os.path.join(TEST_ARGS["zserio_root_dir"], "build", "test", "python")
TEST_ARGS["release_dir"] = os.path.join(TEST_ARGS["zserio_root_dir"], "distr")
TEST_ARGS["java"] = "java"

def compileZserio(testFile, mainZsFile, extraArgs=None):
    """
    Compiles test zserio sources for the current python test file (i.e. test suite) and
    directly imports the generated python sources.

    :param testFile: Current test file (i.e. test suite).
    :param mainZsFile: Main zserio source file for the current test suite.
    :param extraArgs: Extra arguments to zserio compiler.

    :returns: Generated python API.
    :raises Exception: When zserio tool fails.
    """

    if extraArgs is None:
        extraArgs = []
    zserioLibsDir = os.path.join(TEST_ARGS["release_dir"], "zserio_libs")
    zserioCore = os.path.join(zserioLibsDir, "zserio_core.jar")
    zserioPython = os.path.join(zserioLibsDir, "zserio_python.jar")
    testDir = os.path.dirname(testFile) # current test directory
    testZsDir = os.path.join(testDir, "..", "zs") # directory where test zs files are located
    apiDir = getApiDir(testDir)
    zserioCmd = ('"{java}" -cp "{core}{sep}{python}" zserio.tools.ZserioTool '
                 '{extraArgs} -python "{pythonDir}" -src "{sourceDir}" "{mainSource}"')
    zserioResult = os.system(zserioCmd.format(java=TEST_ARGS["java"],
                                              core=zserioCore,
                                              sep=os.pathsep,
                                              python=zserioPython,
                                              extraArgs=' '.join(extraArgs),
                                              pythonDir=apiDir,
                                              sourceDir=testZsDir,
                                              mainSource=mainZsFile))
    if zserioResult != 0:
        raise Exception("Zserio compilation failed!")

    # import generated api
    sys.path.append(apiDir)
    apiModule = os.path.splitext(mainZsFile)[0] + ".api"
    api = importlib.import_module(apiModule)
    sys.path.remove(apiDir)

    return api

def getApiDir(testDir):
    """
    Gets directory where the API for current test suite will be generated.

    :param testDir: Current test directory.
    :returns: Directory where the API for current test suite will be generated.
    """

    buildDir = TEST_ARGS["build_dir"] # python test root build directory
    testSuiteName = _getTestSuiteName(testDir)
    return os.path.join(buildDir, testSuiteName)

def _getTestSuiteName(testDir):
    """
    Extracts test suite name from the given directory structure.

    Example:
    testDir = "/home/user/zserio/tests/language/enumeration_types/python
    testSuiteName = _getTestSuiteName(testDir)
    print(testSuiteName) # prints "language/enumeration_types"

    :param testDir: Current test directory.
    :returns: Test suite name.
    """

    testDir = os.path.dirname(testDir) # python dir
    testDir, secondDir = os.path.split(testDir)
    firstDir = os.path.split(testDir)[1]
    return os.path.join(firstDir, secondDir)
