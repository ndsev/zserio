import sys
import os
from pathlib import Path
from pybind11.setup_helpers import Pybind11Extension, ParallelCompile, build_ext
from setuptools import setup
from distutils.command.build import build

os.chdir(Path(__file__).parent.resolve())

OPTIONS = [
    ('cpp-runtime-dir=', None, 'Directory containing C++ runtime sources.')
]

class ZserioBuild(build):
    user_options = build.user_options + OPTIONS

    def initialize_options(self):
        build.initialize_options(self)
        self.cpp_runtime_dir = None

    def finalize_options(self):
        build.finalize_options(self)
        if not self.cpp_runtime_dir:
            raise Exception("Parameter '--cpp-runtime-dir' is missing!")
        if not os.path.exists(Path(self.cpp_runtime_dir) / 'zserio'):
            raise Exception("Parameter '--cpp-runtime-dir' does not point to Zserio C++ runtime sources!")

class ZserioBuildExt(build_ext):
    user_options = build_ext.user_options + OPTIONS

    def initialize_options(self):
        build_ext.initialize_options(self)
        self.cpp_runtime_dir = None

    def finalize_options(self):
        build_ext.finalize_options(self)
        self.set_undefined_options('build', ('cpp_runtime_dir', 'cpp_runtime_dir'))

        zserio_cpp = self.extensions[0]

        zserio_cpp.depends.append(__file__)

        zserio_cpp.sources.extend((str(filename) for filename in Path('.').rglob('*.cpp')))
        zserio_cpp.depends.extend((str(filename) for filename in Path('.').rglob('*.h')))

        zserio_cpp.sources.extend(
            (str(filename) for filename in Path(self.cpp_runtime_dir).resolve().rglob('*.cpp'))
        )
        zserio_cpp.depends.extend(
            (str(filename) for filename in Path(self.cpp_runtime_dir).resolve().rglob('*.h'))
        )

        zserio_cpp.include_dirs.append(".")
        zserio_cpp.include_dirs.append(self.cpp_runtime_dir)

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
    cmdclass={"build": ZserioBuild, "build_ext": ZserioBuildExt},

    python_requires='>=3.8',

    license = "BSD-3 Clause",
)
