#!/usr/bin/env groovy

def doDebugTest() {
  sh """
    docker run -d -t \
      -e POSTGRES_USER=${env.IROHA_POSTGRES_USER} \
      -e POSTGRES_PASSWORD=${env.IROHA_POSTGRES_PASSWORD} \
      --name ${env.IROHA_POSTGRES_HOST} \
      --ports 127.0.0.1:5432:5432 postgres:9.5
  """
  sh """
    cd build; \
    IROHA_POSTGRES_HOST=${env.IROHA_POSTGRES_HOST} \
    IROHA_POSTGRES_PORT=${env.IROHA_POSTGRES_PORT} \
    IROHA_POSTGRES_USER=${env.IROHA_POSTGRES_USER} \
    IROHA_POSTGRES_PASSWORD=${env.IROHA_POSTGRES_PASSWORD} \
    ctest --output-on-failure
  """
}

return this