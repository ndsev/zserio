import sys
import os
from pathlib import Path
from pybind11.setup_helpers import Pybind11Extension, ParallelCompile, build_ext
from setuptools import setup
from distutils.command.build import build

os.chdir(Path(__file__).parent.resolve())

class ZserioBuildExt(build_ext):
    def initialize_options(self):
        build_ext.initialize_options(self)
        self.cpp_runtime_dir = None

    def finalize_options(self):
        build_ext.finalize_options(self)
        if not os.path.exists('zserio'):
            raise Exception("Subdirectory 'zserio' is missing (must contains Zserio C++ runtime sources)!")

        self.set_undefined_options('build')

        zserio_cpp = self.extensions[0]

        zserio_cpp.depends.append(os.path.relpath(__file__))

        zserio_cpp.sources.extend((str(filename) for filename in Path('.').rglob('*.cpp')))
        zserio_cpp.depends.extend((str(filename) for filename in Path('.').rglob('*.h')))

        print("!!! Sources:")
        for source in zserio_cpp.sources:
            print(source)
        print("!!! Depends:")
        for depend in zserio_cpp.depends:
            print(depend)

        zserio_cpp.include_dirs.append(".")

        zserio_cpp.define_macros.append(('PYBIND11_DETAILED_ERROR_MESSAGES', None))

    def build_extensions(self):
        # compiler is not known in finalize_options yet
        if self.compiler.compiler_type == "unix":
             zserio_cpp = self.extensions[0]
             zserio_cpp.extra_compile_args.extend(['-O3'])

        build_ext.build_extensions(self)

ParallelCompile(default=0).install()

setup(
    name='zserio_cpp',
    version=0.1,
    url="https://github.com/ndsev/zserio",
    author='Navigation Data Standard e.V.',
    author_email='support@nds-association.org',
    description='Zserio C++ runtime binding to Python',

    ext_modules=[Pybind11Extension('zserio_cpp', sources=[])], # will be set-up in ZserioBuildExt
    cmdclass={"build_ext": ZserioBuildExt},

    python_requires='>=3.8',

    license = "BSD-3 Clause",
)
