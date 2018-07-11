#!/usr/bin/env groovy

def doDebugTest() {
  // TODO: use docker containers here
  sh """
    docker run -d -t \
      -e POSTGRES_USER=${env.IROHA_POSTGRES_USER} \
      -e POSTGRES_PASSWORD=${env.IROHA_POSTGRES_PASSWORD} \
      --name ${env.IROHA_POSTGRES_HOST} \
      -p 127.0.0.1:5432:5432 postgres:9.5
  """
  sh "sudo cp $WS_DIR/build/shared_libs/* /usr/lib/aarch64-linux-gnu/"
  sh """
    cd build; ctest --output-on-failure
  """
}

return this
