properties([
  parameters([
    booleanParam(defaultValue: true, description: 'Build `iroha`', name: 'iroha'),
    booleanParam(defaultValue: false, description: 'Build `bindings`', name: 'bindings'),
    booleanParam(defaultValue: true, description: '', name: 'x86_64_linux'),
    booleanParam(defaultValue: false, description: '', name: 'armv7_linux'),
    booleanParam(defaultValue: false, description: '', name: 'armv8_linux'),
    booleanParam(defaultValue: false, description: '', name: 'x86_64_macos'),
    booleanParam(defaultValue: false, description: '', name: 'x86_64_win'),
    choice(choices: 'Debug\nRelease', description: 'Iroha build type', name: 'build_type'),
    booleanParam(defaultValue: false, description: 'Build Java bindings', name: 'JavaBindings'),
    choice(choices: 'Release\nDebug', description: 'Java bindings build type', name: 'JBBuildType'),
    string(defaultValue: 'jp.co.soramitsu.iroha', description: 'Java bindings package name', name: 'JBPackageName'),
    booleanParam(defaultValue: false, description: 'Build Python bindings', name: 'PythonBindings'),
    choice(choices: 'Release\nDebug', description: 'Python bindings build type', name: 'PBBuildType'),
    choice(choices: 'python3\npython2', description: 'Python bindings version', name: 'PBVersion'),
    booleanParam(defaultValue: false, description: 'Build Android bindings', name: 'AndroidBindings'),
    choice(choices: '26\n25\n24\n23\n22\n21\n20\n19\n18\n17\n16\n15\n14', description: 'Android Bindings ABI Version', name: 'ABABIVersion'),
    choice(choices: 'Release\nDebug', description: 'Android bindings build type', name: 'ABBuildType'),
    choice(choices: 'arm64-v8a\narmeabi-v7a\narmeabi\nx86_64\nx86', description: 'Android bindings platform', name: 'ABPlatform'),
    booleanParam(defaultValue: false, description: 'Build docs', name: 'Doxygen'),
    string(defaultValue: '4', description: 'How much parallelism should we exploit. "4" is optimal for machines with modest amount of memory and at least 4 cores', name: 'PARALLELISM')
  ]),
  buildDiscarder(logRotator(numToKeepStr: '20')),
  timestamps()
])

def environment = [
  'CCACHE_DIR=/opt/.ccache',
  'DOCKER_REGISTRY_BASENAME=hyperledger/iroha',
  "IROHA_NETWORK=iroha-0${CHANGE_ID}-${GIT_COMMIT}-${BUILD_NUMBER}",
  "IROHA_POSTGRES_HOST=pg-0${CHANGE_ID}-${GIT_COMMIT}-${BUILD_NUMBER}",
  "IROHA_POSTGRES_USER=pguser${GIT_COMMIT}",
  "IROHA_POSTGRES_PASSWORD=${GIT_COMMIT}",
  'IROHA_POSTGRES_PORT=5432'
  'WS_DIR=/var/jenkins/workspace/09ea0b41fe86d884c6ecf57676d34ecacfb5411d-30'
]

def buildAgentLabels = ['x86_64_aws_cross']
def testAgentLabels = ['armv8-cross']
def buildBuilders = [:]
// def transformDebugStep(label) {
//   // We need to wrap what we return in a Groovy closure, or else it's invoked
//   // when this method is called, not when we pass it to parallel.
//   // To do this, you need to wrap the code below in { }, and either return
//   // that explicitly, or use { -> } syntax.
//   return {
//     node(label) {
//       dir("${WS_DIR}") {
//         debugBuild = load ".jenkinsci/debug-build-cross.groovy"
//         checkout scm
//         debugBuild.doDebugBuild()
//       }
//     }
//   }
// }

for (x in buildAgentLabels) {
  def label = x
  buildBuilders[label] = {
    // transformDebugStep(label)
    node(label) {
      withEnv(environment) {
        dir("${WS_DIR}") {
          debugBuild = load ".jenkinsci/debug-build-cross.groovy"
          checkout scm
          debugBuild.doDebugBuild()
        }
      }
    }
  }
}

parallel buildBuilders
buildBuilders = [:]

for (x in testAgentLabels) {
  def label = x
  buildBuilders[label] = {
    // transformDebugStep(label)
    node(label) {
      withEnv(environment) {
        dir("${WS_DIR}") {
          testBuild = load ".jenkinsci/debug-test.grovy"
          testBuild.doDebugTest()
        }
      }
    }
  }
}
