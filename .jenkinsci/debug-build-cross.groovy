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
    sh "cmake --build build -- -j${PARALLELISM}"
    sh "ccache --show-stats"
    sh "mkdir $WS_DIR/build/shared_libs"
    sh """
      for solib in \$(\$CROSS_TRIPLE_PREFIX-ldd --root \$STAGING $WS_DIR/build/bin/irohad | \
      	grep -v 'not found' | \
      	awk '/\\.so/{print \$1}' | \
      	sort -u); do \
      	  find \$STAGING -name \$solib -exec cp {} $WS_DIR/build/shared_libs \\; ; \
      done
    """
  }
}

return this
