"""
Module used as a test runner for integration tests.
This module also executes pylint for test files and generated files.
"""

import unittest
import sys
import os
import argparse
import glob
import pylint.lint

def main():
    testRoot = os.path.dirname(os.path.realpath(__file__))
    sys.path.append(os.path.abspath(os.path.join(testRoot, "utils", "python")))
    from testutils import TEST_ARGS, getApiDir

    argParser = argparse.ArgumentParser()
    argParser.add_argument("--build_dir")
    argParser.add_argument("--release_dir")
    argParser.add_argument("--java")
    argParser.add_argument("--verbosity", type=int)
    argParser.add_argument("--filter")
    argParser.add_argument("--pylint_rcfile")
    argParser.set_defaults(filter="**", verbosity=2)
    args = argParser.parse_args()
    if args.build_dir:
        TEST_ARGS["build_dir"] = args.build_dir
    if args.release_dir:
        TEST_ARGS["release_dir"] = args.release_dir
    if args.java:
        TEST_ARGS["java"] = args.java

    # path to zserio runtime release
    sys.path.append(os.path.join(TEST_ARGS["release_dir"], "runtime_libs", "python"))

    # load tests
    loader = unittest.TestLoader()
    testSuite = unittest.TestSuite()

    testPattern = "*Test.py"
    if "**" in args.filter:
        testFilesPattern = os.path.join(testRoot, args.filter, testPattern)
    else:
        testFilesPattern = os.path.join(testRoot, args.filter, "**", testPattern)

    testFiles = glob.glob(testFilesPattern, recursive=True)
    testDirs = set()
    for globResult in testFiles:
        testDir = os.path.dirname(globResult)
        if testDir not in testDirs:
            testDirs.add(testDir)
            loadedTests = loader.discover(testDir, pattern=testPattern, top_level_dir=testDir)
            testSuite.addTest(loadedTests)
    runner = unittest.TextTestRunner(verbosity=args.verbosity)
    testResult = runner.run(testSuite)
    if not testResult.wasSuccessful():
        return 1

    # run pylint
    pylintOptions = ["--persistent=n"]
    if args.pylint_rcfile:
        pylintOptions.append("--rcfile=%s" % (args.pylint_rcfile))

    # pylint for test files
    print("\nRunning pylint on python tests.")
    pylintResult = _runPylint(testFiles, pylintOptions,
                              "missing-docstring, invalid-name, duplicate-code, too-many-public-methods")
    if pylintResult != 0:
        return pylintResult

    # pylint for generated code (for all tests which were run)
    apiFiles = [] # only api.py
    genFiles = [] # all other generated files
    for testDir in testDirs:
        apiDir = getApiDir(testDir)
        globResult = glob.glob(os.path.join(apiDir, "**", "*.py"), recursive=True)
        apiFiles += [apiFile for apiFile in globResult if apiFile.endswith("api.py")]
        genFiles += [genFile for genFile in globResult if not genFile.endswith("api.py")]

    print("Running pylint on generated files.")
    pylintResult = _runPylint(genFiles, pylintOptions, ("missing-docstring, invalid-name, no-self-use,"
                                                        "duplicate-code, line-too-long, singleton-comparison, "
                                                        "too-many-instance-attributes, too-many-arguments, "
                                                        "too-many-public-methods"))
    if pylintResult != 0:
        return pylintResult

    print("Running pylint on generated api.py files.")
    pylintResult = _runPylint(apiFiles, pylintOptions, "missing-docstring, unused-import, line-too-long")

    return pylintResult

def _runPylint(files, options, disableOption=None):
    if not files:
        return 0

    pylintOptions = list(files)
    pylintOptions += options
    if disableOption:
        pylintOptions.append("--disable=%s" % disableOption)
    try:
        pylint.lint.Run(pylintOptions)
    except SystemExit as systemExit:
        if systemExit.code != 0:
            return systemExit.code
    return 0

if __name__ == "__main__":
    sys.exit(main())
