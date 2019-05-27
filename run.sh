#!/bin/sh

COMMAND_NAME=$0
DEFAULT_PROFILER_DURATION=60

JAR="JVMTroubleshootingWorkshop-1.0.0-SNAPSHOT.jar"

function usage() {
  echo "Usage: $COMMAND_NAME [-l name]"
  echo "  -l name       Lab name"
  echo "  -p [seconds]  Profile (Default ${DEFAULT_PROFILER_DURATION}s)"
  echo "  -g            GC Details"
  echo "  -d            Heap Dump on OOM"
  echo "  -m            Maximum Metaspace"
  echo "  -t seconds    Timeout in seconds"
  echo "  -s            Turn on safepoint info"
  echo "  -z            Avoid safepoint bias"
  echo "  -r            Turn on Flight Recorder"
  echo "  -c            Trace Class Loading/Unloading"
  echo "  -o            Use Java 8 Default GC"
  echo "  -h            Display help"
  exit 1
}

function main() {
  while getopts ":l:t:p:m: hrdszcog" opt
  do
    case $opt in
    h )
      usage
      ;;
    r )
      RECORDING="yes"
      ;;
    c )
      TRACE_CLASSES="yes"
      ;;
    o )
      DEFAULT_JAVA_8_GC="yes"
      ;;
    g )
      GC_DETAILS="yes"
      ;;
    d )
      HEAP_DUMP_ON_OOM="yes"
      ;;
    m )
      MAX_METASPACE=$OPTARG
      ;;
    p )
      RECORDING="yes"
      PROFILER="yes"
      PATH_TO_GC_ROOT="yes"
      PROFILER_DURATION=$OPTARG
      ;;
    s )
      SAFEPOINT_INFO="yes"
      ;;
    z )
      AVOID_SAFEPOINT_BIAS="yes"
      ;;
    t )
      TIMEOUT=$OPTARG
      ;;
    l )
      LAB_NAME=$OPTARG
      ;;
    \? )
      echo "Invalid option -$OPTARG"
      ;;
    : )
      if [[ "$OPTARG" == "p" ]]; then
        RECORDING="yes"
        PROFILER="yes"
      else
        echo "Option -$OPTARG requires an argument"
        if [[ "$OPTARG" == "l" ]]; then
          ls -1 src/main/java/ar/com/javacuriosities/labs/
          echo
        fi
      fi
      ;;
  esac
  done

  if [[ -z "$LAB_NAME" ]]; then
    usage
  fi

  if [[ ! -z "$RECORDING" ]]; then
    JVM_OPTIONS="-XX:+FlightRecorder "
  fi

  if [[ ! -z "$PATH_TO_GC_ROOT" ]]; then
    PATH_TO_GC_ROOT_OPTION=",path-to-gc-roots=true"
  fi

  if [[ ! -z "$PROFILER" ]]; then
    PROFILER_DURATION=${PROFILER_DURATION:-${TIMEOUT:-$DEFAULT_PROFILER_DURATION}}
    JVM_OPTIONS+="-XX:StartFlightRecording=duration=${PROFILER_DURATION}s$PATH_TO_GC_ROOT_OPTION,settings=profile,filename=./tools/recording_${LAB_NAME}.jfr "

    echo "Recording duration: ${PROFILER_DURATION}s"
  fi


  if [[ ! -z "$SAFEPOINT_INFO" ]]; then
    # JVM_OPTIONS+="-XX:-UseBiasedLocking "
    JVM_OPTIONS+="-XX:+PrintGCApplicationStoppedTime -XX:+PrintGCApplicationConcurrentTime "
    # JVM_OPTIONS+="-XX:+PrintSafepointStatistics  -XX:PrintSafepointStatisticsCount=1 "
  fi

  if [[ ! -z "$AVOID_SAFEPOINT_BIAS" ]]; then
    JVM_OPTIONS+="-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints "
  fi

  if [[ ! -z "$HEAP_DUMP_ON_OOM" ]]; then
    JVM_OPTIONS+="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp "
  fi

  if [[ ! -z "$MAX_METASPACE" ]]; then
    JVM_OPTIONS+="-XX:MaxMetaspaceSize=${MAX_METASPACE}m "
  fi

  if [[ ! -z "$TRACE_CLASSES" ]]; then
    JVM_OPTIONS+="-XX:+TraceClassLoading -XX:+TraceClassUnloading "
  fi

  if [[ ! -z "$DEFAULT_JAVA_8_GC" ]]; then
    JVM_OPTIONS+="-XX:+UseConcMarkSweepGC "
  fi

  if [[ ! -z "$GC_DETAILS" ]]; then
    JVM_OPTIONS+="-XX:+PrintGCDetails -Xloggc:./tools/gc_${LAB_NAME}.log -Xlog:gc*:file=./tools/gc_${LAB_NAME}.log "
  fi

  MAIN_CLASS="ar.com.javacuriosities.labs.$LAB_NAME.Main"

  java ${JVM_OPTIONS} -cp target/$JAR $MAIN_CLASS "$@"
}

main "$@"
