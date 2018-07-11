environment {
  WS_DIR = "/var/jenkins/workspace/09ea0b41fe86d884c6ecf57676d34ecacfb5411d-30"
}

def debugBuild = load ".jenkinsci/debug-build-cross.groovy"
def labels = ['x86_64_aws_cross']
def builders = [:]
def transformDebugStep(label) {
  // We need to wrap what we return in a Groovy closure, or else it's invoked
  // when this method is called, not when we pass it to parallel.
  // To do this, you need to wrap the code below in { }, and either return
  // that explicitly, or use { -> } syntax.
  return {
    node(label) {
      ws("${WS_DIR}") {
        checkout scm
        debugBuild.doDebugBuild()
      }
    }
  }
}

for (x in labels) {
  def label = x // Need to bind the label variable before the closure - can't do 'for (label in labels)'
  // Create a map to pass in to the 'parallel' step so we can fire all the builds at once
  builders[label] = {
    transformDebugStep(label)
  }
}

parallel builders