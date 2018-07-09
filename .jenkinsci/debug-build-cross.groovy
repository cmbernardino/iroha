#!/usr/bin/env groovy

def doDebugBuild() {
  docker.image("${DOCKER_REGISTRY_BASENAME}:crossbuild-debian-stretch-arm64").inside(""
  	+ " -v /opt/ccache:${CCACHE_DIR}"
  	+ " --user root") {
    sh """
      ccache --version
      ccache --show-stats
      ccache --zero-stats
      ccache --max-size=20G
    """
    sh """
      cmake \
        -H. \
        -Bbuild \
        -DCMAKE_BUILD_TYPE=Debug \
        -DCOVERAGE=OFF \
        -DTESTING=ON \
        -DCMAKE_TOOLCHAIN_FILE=/opt/toolchain.cmake
    """
    sh "cmake --build build -- -j4"
    sh "ccache --show-stats"
  }
}

return this
