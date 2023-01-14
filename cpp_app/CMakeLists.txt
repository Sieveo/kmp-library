cmake_minimum_required(VERSION 3.24)
project(cpp_app)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

include_directories("headers")
link_directories("libs")

add_executable(cpp_app main.cpp)
target_link_libraries(cpp_app popcorn)
