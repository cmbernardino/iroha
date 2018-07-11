def labels = ['x86_64_aws_cross']
def builders = [:]
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

for (x in labels) {
  def label = x
  builders[label] = {
    // transformDebugStep(label)
    node(label) {
      withEnv(['WS_DIR=/var/jenkins/workspace/09ea0b41fe86d884c6ecf57676d34ecacfb5411d-30']) {
        dir("${WS_DIR}") {
          debugBuild = load ".jenkinsci/debug-build-cross.groovy"
          checkout scm
          debugBuild.doDebugBuild()
        }
      }
    }
  }
}

parallel builders