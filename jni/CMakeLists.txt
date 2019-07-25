cmake_minimum_required(VERSION 3.4.1)
#todo 注意下jni包,打包2个so文件会存在一定优化所以,2个so分开打,修改cmake
add_library( # Sets the name of the library.
             voiccore

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp )


add_library( # Sets the name of the library.
             voice

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/voice-core.cpp )


find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
                       voiccore
                       voice
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )