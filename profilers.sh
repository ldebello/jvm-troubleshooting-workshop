#!/bin/sh

COMMAND_NAME=$0

LAB_NAME=
JVM_PARAMETERS=
PROFILER=

JAR="JVMTroubleshootingWorkshop-1.0.0-SNAPSHOT.jar"

DEFAULT_TIMEOUT=60

function usage() {
  echo "Script to labs using a specific profiler"
  echo " -l name      Lab name"
  echo " -t seconds   Timeout in seconds (Default ${DEFAULT_TIMEOUT}s)"
  echo " -p           Profile with (jstack - honest_profiler - async_profiler - dtrace - perf)"
  exit 1
}

function main() {
  while getopts ":l:p:t: h" opt
  do
    case ${opt} in
    h )
        usage
        ;;
    p )
        PROFILER=$OPTARG
        ;;
    l )
        LAB_NAME=$OPTARG
        ;;
    t )
        DEFAULT_TIMEOUT=$OPTARG
        ;;
    \? )
        echo "Invalid option -$OPTARG"
        ;;
    : ) echo "Option -$OPTARG requires an argument"
        if [[ "$OPTARG" == "l" ]]; then
          ls -1 src/main/java/ar/com/javacuriosities/labs/
          echo
        fi
        ;;
  esac
  done

  if [[ -z "$LAB_NAME" ]]; then
    echo "Lab name is mandatory" >&2
    usage
  fi

  CURRENT_DIRECTORY=`pwd`
  TOOLS_DIRECTORY=${CURRENT_DIRECTORY}/tools

  if [[ ! -d "$TOOLS_DIRECTORY" ]]; then
    mkdir -p ${TOOLS_DIRECTORY}
  fi

  if [[ "$PROFILER" = "jfr" ]]; then
    # Moving to tools folder
    cd ${TOOLS_DIRECTORY}

    # Clone "FlameGraph" if necessary
    if [[ ! -d "FlameGraph" ]]; then
      git clone git@github.com:brendangregg/FlameGraph.git
    fi
    
    # Moving to tools folder
    cd ${TOOLS_DIRECTORY}

    # Build "jfr-flame-graph" if necessary
    if [[ ! -d "jfr-flame-graph" ]]; then
      git clone git@github.com:chrishantha/jfr-flame-graph.git

      # Move to working directory
      cd jfr-flame-graph

      # Build
      ./gradlew installDist

      # Clean up
      cd ..
    fi

    JFR_SCRIPTS="${TOOLS_DIRECTORY}/jfr-flame-graph/build/install/jfr-flame-graph/bin"

    # Preparing JVM parameters
    JVM_PARAMETERS="-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints -XX:+FlightRecorder"

    # Configure flame-graph path
    export FLAMEGRAPH_DIR="${TOOLS_DIRECTORY}/FlameGraph"

    # Clean up
    cd ..
  fi

  if [[ "$PROFILER" = "honest_profiler" ]]; then
    OS_TYPE=`uname`

    # Moving to tools folder
    cd ${TOOLS_DIRECTORY}

    # Build "honest-profiler.zip" if necessary
    if [[ ! -f "honest-profiler.zip" ]]; then
      # Clone repo
      git clone git@github.com:jvm-profiling-tools/honest-profiler.git

      # Move to repo
      cd honest-profiler

      # The build is OS dependent you can check the instructions here https://github.com/jvm-profiling-tools/honest-profiler/wiki/How-to-build

      if [[ "$OSTYPE" == "darwin"* ]]; then
        # Remove UnitTest++ for build in OSx
        sed -i .bk "s/find_package(UnitTest++ REQUIRED NO_MODULE)//g" CMakeLists.txt
      fi

      # Build C code
      cmake CMakeLists.txt
      export LC_ALL=C

      # Build java agent
      mvn clean package -DskipTests

      # Copy output
      cp target/honest-profiler.zip ../

      # Clean up
      cd ..
      rm -rf honest-profiler
    fi

    # Delete "honest-profiler" working directory
    rm -rf honest-profiler

    # Unzip "honest-profiler"
    unzip honest-profiler.zip -d honest-profiler

    if [[ "$OSTYPE" == "darwin"* ]]; then
      # Fixing script for OSx
      echo '#!/bin/sh' > honest-profiler/console
      echo 'java -cp "$JAVA_HOME/lib/tools.jar":honest-profiler.jar com.insightfullogic.honest_profiler.ports.console.ConsoleApplication $@' >> honest-profiler/console

      echo '#!/bin/sh' > honest-profiler/gui
      echo 'java -cp "$JAVA_HOME/lib/tools.jar":honest-profiler.jar com.insightfullogic.honest_profiler.ports.javafx.JavaFXApplication' >> honest-profiler/gui

      echo '#!/bin/sh' > honest-profiler/dump-flamegraph
      echo 'java -cp "$JAVA_HOME/lib/tools.jar":honest-profiler.jar com.insightfullogic.honest_profiler.ports.console.FlameGraphDumperApplication $@' >> honest-profiler/dump-flamegraph
    fi
    LIB_AGENT_NAME=liblagent.so

    # Preparing JVM parameters
    if [[ "$OSTYPE" == "darwin"* ]]; then
      LIB_AGENT_NAME=liblagent.dylib
    fi

    JVM_PARAMETERS="-agentpath:$TOOLS_DIRECTORY/honest-profiler/$LIB_AGENT_NAME=interval=7,logPath=$TOOLS_DIRECTORY/${LAB_NAME}.hpl"
  fi

  if [[ "$PROFILER" = "async_profiler" ]]; then
    # Moving to tools folder
    cd ${TOOLS_DIRECTORY}

    # Build "async-profiler" if necessary
    if [[ ! -d "async-profiler" ]]; then
      # Create working directory
      mkdir -p async-profiler

      # Move to working directory
      cd async-profiler

      # Clone repo
      git clone git@github.com:jvm-profiling-tools/async-profiler.git

      # Move to repo
      cd async-profiler

      # Build project
      make

      # Copy output
      cp build/* ../

      # Clean up
      cd ..
      rm -rf async-profiler
    fi

    LIB_AGENT_NAME=libasyncProfiler.so

    JVM_PARAMETERS="-agentpath:$TOOLS_DIRECTORY/async-profiler/$LIB_AGENT_NAME=start,svg,file=$TOOLS_DIRECTORY/${LAB_NAME}-async.svg"
  fi

  if [[ "$PROFILER" = "dtrace" ]]; then
    # Moving to tools folder
    cd ${TOOLS_DIRECTORY}

    # Clone "FlameGraph" if necessary
    if [[ ! -d "FlameGraph" ]]; then
      git clone git@github.com:brendangregg/FlameGraph.git
    fi
    
    # Moving to tools folder
    cd ${TOOLS_DIRECTORY}

    # Build "perf-map-agent" if necessary
    if [[ ! -d "perf-map-agent" ]]; then

      # Clone repo
      git clone git@github.com:jvm-profiling-tools/perf-map-agent.git

      # Move to perf-map-agent
      cd perf-map-agent

      # Build
      cmake .
      make
    fi

    DTRACE_SCRIPTS="${TOOLS_DIRECTORY}/perf-map-agent/bin"

    # Preparing JVM parameters
    JVM_PARAMETERS="-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints -XX:+PreserveFramePointer"

    # Configure flame-graph path
    export FLAMEGRAPH_DIR="${TOOLS_DIRECTORY}/FlameGraph"

    # Configure numbers of seconds for dtrace
    export DTRACE_SECONDS=150
  fi

  if [[ "$PROFILER" = "perf" ]]; then
    # Moving to tools folder
    cd ${TOOLS_DIRECTORY}

    # Clone "FlameGraph" if necessary
    if [[ ! -d "FlameGraph" ]]; then
      git clone git@github.com:brendangregg/FlameGraph.git
    fi
    
    # Moving to tools folder
    cd ${TOOLS_DIRECTORY}

    # Build "perf-map-agent" if necessary
    if [[ ! -d "perf-map-agent" ]]; then

      # Clone repo
      git clone git@github.com:jvm-profiling-tools/perf-map-agent.git

      # Move to perf-map-agent
      cd perf-map-agent

      # Build
      cmake .
      make
    fi

    PERF_SCRIPTS="${TOOLS_DIRECTORY}/perf-map-agent/bin"

    # Preparing JVM parameters
    JVM_PARAMETERS="-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints -XX:+PreserveFramePointer"

    # Configure flame-graph path
    export FLAMEGRAPH_DIR="${TOOLS_DIRECTORY}/FlameGraph"

    # Configure numbers of seconds for perf
    export PERF_RECORD_SECONDS=150
  fi

  # Build project
  mvn clean package

  # Run java project in background
  MAIN_CLASS="ar.com.javacuriosities.labs.$LAB_NAME.Main"

  nohup java ${JVM_PARAMETERS} -cp target/${JAR} ${MAIN_CLASS} "$@" &
  JAVA_PID=$!

  echo "Java PID: ${JAVA_PID}"
  
  if [[ "$PROFILER" = "jstack" ]]; then
    ${CURRENT_DIRECTORY}/scripts/get_thread_stacks.sh -p ${JAVA_PID} -o ${LAB_NAME}
  fi

  if [[ "$PROFILER" = "dtrace" ]]; then
    ${DTRACE_SCRIPTS}/dtrace-java-flames ${JAVA_PID}
    mv flamegraph-${JAVA_PID}.svg ${TOOLS_DIRECTORY}/${LAB_NAME}-dtrace.svg
  fi

  if [[ "$PROFILER" = "perf" ]]; then
    ${PERF_SCRIPTS}/perf-java-flames ${JAVA_PID}
    mv flamegraph-${JAVA_PID}.svg ${TOOLS_DIRECTORY}/${LAB_NAME}-perf.svg
  fi  

  if [[ "$PROFILER" = "jfr" ]]; then
    jcmd ${JAVA_PID} JFR.start settings=${CURRENT_DIRECTORY}/scripts/custom-profiling.jfc name=${LAB_NAME}_recording filename=${TOOLS_DIRECTORY}/${LAB_NAME}.jfr dumponexit=true

    # Waiting until java process finish
    while ps -p ${JAVA_PID} &>/dev/null; do
        sleep 1
    done

    ${JFR_SCRIPTS}/create_flamegraph.sh -f ${TOOLS_DIRECTORY}/${LAB_NAME}.jfr -i > ${TOOLS_DIRECTORY}/${LAB_NAME}-jfr.svg
  fi
}

main "$@"