#!/bin/sh

COMMAND_NAME=$0
DEFAULT_PROFILER_DURATION=60

JAR="JVMTroubleshootingWorkshop-1.0.0-SNAPSHOT.jar"

function usage() {
  echo "Usage: $COMMAND_NAME [-l name]"
  echo "  -l name       Lab name"
  echo "  -p [seconds]  Profile (Default ${DEFAULT_PROFILER_DURATION}s)"
  echo "  -r            Turn on Flight Recorder"
  echo "  -s            Avoid Safepoint bias"
  echo "  -t seconds    Timeout in seconds"
  echo "  -d            Heap Dump on OOM"
  echo "  -h            Display help"
  exit 1
}

function main() {
  while getopts ":l:t:p: hrds" opt
  do
    case $opt in
    h )
      usage
      ;;
    r )
      RECORDING="yes"
      ;;
    d )
      HEAP_DUMP_ON_OOM="yes"
      ;;
    p )
      RECORDING="yes"
      PROFILER="yes"
      PATH_TO_GC_ROOT="yes"
      PROFILER_DURATION=$OPTARG
      ;;
    s )
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

  if [[ ! -z "$AVOID_SAFEPOINT_BIAS" ]]; then
    JVM_OPTIONS+="-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints "
  fi

  if [[ ! -z "HEAP_DUMP_ON_OOM" ]]; then
    JVM_OPTIONS+="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp "
  fi

  MAIN_CLASS="ar.com.javacuriosities.labs.$LAB_NAME.Main"

  java ${JVM_OPTIONS} -cp target/$JAR $MAIN_CLASS "$@"
}

main "$@"
