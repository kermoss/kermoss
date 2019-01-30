# /bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"


mvn -f $DIR/pom.xml clean package -DskipTests
