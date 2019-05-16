#!/bin/sh

COMMAND_NAME=$0
JAR="Labs-1.0.0-SNAPSHOT.jar"

function usage() {
  echo "Usage: $COMMAND_NAME [-l index]"
  echo "  -l index    Number of lab to execute"
  echo "  -p          Profile during 60s"
  echo "  -r          Turn on Flight Recorder"
  echo "  -s          Avoid Safepoint bias"
  echo "  -t          Timeout in seconds"
  echo "  -h          Display help"
  exit 1
}

function main() {
  while getopts ":l:t: hprs" opt
  do
    case $opt in
    h )
      usage
      ;;
    r )
      RECORDING="yes"
      ;;
    p )
      RECORDING="yes"
      PROFILER="yes"
      ;;
    s )
      AVOID_SAFEPOINT_BIAS="yes"
      ;;
    t )
      TIMEOUT=$OPTARG
      ;;
    l )
      LAB_INDEX=$OPTARG
      ;;
    \? )
      echo "Invalid option -$OPTARG"
      ;;
    : )
      echo "Option -$OPTARG requires an argument"
      ;;
  esac
  done

  if [[ -z "$LAB_INDEX" ]]; then
    usage
  fi

  if [[ ! -z "$RECORDING" ]]; then
    JVM_OPTIONS="-XX:+FlightRecorder "
  fi

  if [[ ! -z "$PROFILER" ]]; then
    JVM_OPTIONS+="-XX:StartFlightRecording=duration=60s,settings=profile,filename=./tools/recording_${LAB_INDEX}.jfr "
  fi

  if [[ ! -z "$AVOID_SAFEPOINT_BIAS" ]]; then
    JVM_OPTIONS+="-XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints "
  fi

  MAIN_CLASS="ar.com.javacuriosities.labs.lab$LAB_INDEX.Main"

  java ${JVM_OPTIONS} -cp target/$JAR $MAIN_CLASS "$@"
}

main "$@"
