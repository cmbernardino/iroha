// properties([
//   parameters([
//     booleanParam(defaultValue: true, description: 'Build `iroha`', name: 'iroha'),
//     booleanParam(defaultValue: false, description: 'Build `bindings`', name: 'bindings'),
//     booleanParam(defaultValue: true, description: '', name: 'x86_64_linux'),
//     booleanParam(defaultValue: false, description: '', name: 'armv7_linux'),
//     booleanParam(defaultValue: false, description: '', name: 'armv8_linux'),
//     booleanParam(defaultValue: false, description: '', name: 'x86_64_macos'),
//     booleanParam(defaultValue: false, description: '', name: 'x86_64_win'),
//     choice(choices: 'Debug\nRelease', description: 'Iroha build type', name: 'build_type'),
//     booleanParam(defaultValue: false, description: 'Build Java bindings', name: 'JavaBindings'),
//     choice(choices: 'Release\nDebug', description: 'Java bindings build type', name: 'JBBuildType'),
//     string(defaultValue: 'jp.co.soramitsu.iroha', description: 'Java bindings package name', name: 'JBPackageName'),
//     booleanParam(defaultValue: false, description: 'Build Python bindings', name: 'PythonBindings'),
//     choice(choices: 'Release\nDebug', description: 'Python bindings build type', name: 'PBBuildType'),
//     choice(choices: 'python3\npython2', description: 'Python bindings version', name: 'PBVersion'),
//     booleanParam(defaultValue: false, description: 'Build Android bindings', name: 'AndroidBindings'),
//     choice(choices: '26\n25\n24\n23\n22\n21\n20\n19\n18\n17\n16\n15\n14', description: 'Android Bindings ABI Version', name: 'ABABIVersion'),
//     choice(choices: 'Release\nDebug', description: 'Android bindings build type', name: 'ABBuildType'),
//     choice(choices: 'arm64-v8a\narmeabi-v7a\narmeabi\nx86_64\nx86', description: 'Android bindings platform', name: 'ABPlatform'),
//     booleanParam(defaultValue: false, description: 'Build docs', name: 'Doxygen'),
//     string(defaultValue: '4', description: 'How much parallelism should we exploit. "4" is optimal for machines with modest amount of memory and at least 4 cores', name: 'PARALLELISM')
//   ]),
//   buildDiscarder(logRotator(numToKeepStr: '20')),
//   timestamps()
// ])

def environmentList = []
def environment = [:]
def tasks = [:]

node('master') {
  def scmVars = checkout scm
  environment = [
    "CCACHE_DIR": "/opt/.ccache",
    "DOCKER_REGISTRY_BASENAME": "hyperledger/iroha",
    "IROHA_NETWORK": "iroha-0${scmVars.CHANGE_ID}-${scmVars.GIT_COMMIT}-${env.BUILD_NUMBER}",
    "IROHA_POSTGRES_HOST": "pg-0${scmVars.CHANGE_ID}-${scmVars.GIT_COMMIT}-${env.BUILD_NUMBER}",
    "IROHA_POSTGRES_USER": "pguser${scmVars.GIT_COMMIT}",
    "IROHA_POSTGRES_PASSWORD": "${scmVars.GIT_COMMIT}",
    "IROHA_POSTGRES_PORT": "5432",
    "WS_BASE_DIR": "/var/jenkins/workspace"
  ]
}
environment.each { it ->
  environmentList.add("${it.key}=${it.value}")
}
//x86_64_aws_cross
def agentLabels = ['x86_64-agent': 'ec2-fleet', 'arm64-agent': 'armv8-cross']
// def targetOS = ['ubuntu-xenial', 'ubuntu-bionic', 'debian-stretch', 'macos']
def targetOS = ['debian-stretch']
//def targetArch = ['x86_64': agentLabels['x86_64-agent'], 'arm64': agentLabels['armv8-agent']]
def targetArch = ['arm64': agentLabels['armv8-agent']]

def buildSteps(label, arch, os, buildType, environment) {
  return {
    node(label) {
      withEnv(environment) {
        // checkout to expose env vars
        def scmVars = checkout scm
        def workspace = "/var/jenkins/workspace/97acaa2bc1fa1db62e6a0531901e0f41886422ce-99-arm64-debian-stretch"
        //def workspace = "${env.WS_BASE_DIR}/${scmVars.GIT_COMMIT}-${env.BUILD_NUMBER}-${arch}-${os}"
        sh("mkdir -p $workspace")
        sh("echo git commit is: ${scmVars.GIT_COMMIT}")
        dir(workspace) {
          // then checkout into actual workspace
          checkout scm
          debugBuild = load ".jenkinsci/debug-build-cross.groovy"
          debugBuild.doDebugBuild(workspace)
        }
      }
    }
  }
}

def testSteps(label, arch, os, environment) {
  return {
    node(label) {
      withEnv(environment) {
        def scmVars = checkout scm
        //def workspace = "${env.WS_BASE_DIR}/${scmVars.GIT_COMMIT}-${env.BUILD_NUMBER}-${arch}-${os}"
        //dir(workspace) {
        def workspace = "/var/jenkins/workspace/97acaa2bc1fa1db62e6a0531901e0f41886422ce-99-arm64-debian-stretch"
        dir(workspace) {
          testBuild = load ".jenkinsci/debug-test.groovy"
          testBuild.doDebugTest(workspace)
        }
      }
    }
  }
}

for(int i=0; i < targetOS.size(); i++) {
  def axisOS = targetOS[i]
  targetArch.each { arch ->
    tasks["${axisOS}-${arch.key}"] = {
      buildSteps(agentLabels['x86_64-agent'], arch.key, axisOS, "Debug", environmentList)()
      testSteps(arch.value, arch.key, axisOS, environmentList)()
    }
  }
}

stage('Debug build') {
  parallel tasks
}
