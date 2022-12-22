import sys
from pathlib import Path
from pybind11.setup_helpers import Pybind11Extension, build_ext
from setuptools import setup

is_64bits = sys.maxsize > 2**32

cpp_dir = 'src'
sources = ["pybind/ZserioCpp.cpp"]
sources.extend([str(filename) for filename in Path(cpp_dir).rglob('*.cpp')])
macros = [("ZSERIO_RUNTIME_64BIT", None)] if is_64bits else []

zserio_cpp = Pybind11Extension(
    'zserio_cpp', sources,
    include_dirs=[cpp_dir],
    extra_compile_args=['-O3'],
    define_macros=macros
)

setup(
    name='zserio_cpp',
    version=0.1,
    author='Milan Kříž',
    author_email='milan.kriz@eccam.com',
    description='Zserio C++ runtime binding to Python',
    ext_modules=[zserio_cpp],
    cmdclass={"build_ext": build_ext},
)
