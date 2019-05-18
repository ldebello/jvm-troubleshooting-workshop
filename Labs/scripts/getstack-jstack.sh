#!/bin/sh

COMMAND_NAME=$0
WAIT_TIME=0.1
EXTENSION=jstacks
OUTPUT=output

function usage() {
  echo "Script to get multiples stacks using jstack"
  echo " -p       PID for the Java process"
  echo " -o       Output file name (Default to ${OUTPUT}.${EXTENSION})"
  echo " -c       Specific a counter for the number of thread dumps (by default until process died)"
  echo " -w       Time to wait between each stack, value in seconds (Default to 0.1 --> 100ms)"
  exit 1
}

function main() {
  while getopts ":w:p:o:c: h" opt
  do
    case ${opt} in
    h )
      usage
      ;;
    w )
      WAIT_TIME=$OPTARG
      ;;
    p )
      PID=$OPTARG
      ;;
    o )
      OUTPUT=$OPTARG
      ;;
    c )
      MAX_COUNTER=$OPTARG
      ;;
    \? )
      echo "Invalid option -$OPTARG"
      ;;
    : ) echo "Option -$OPTARG requires an argument"
      ;;
    esac
  done

  if [[ -z ${PID} ]]; then
    echo "PID is necessary" >&2
    usage
  fi

  if [[ -z ${MAX_COUNTER} ]]; then
    while ps -p ${PID} &>/dev/null; do
      jstack -l ${PID} >> ${OUTPUT}.${EXTENSION}
      sleep ${WAIT_TIME}
    done
  else
    COUNTER=0
    while [[  ${COUNTER} -lt ${MAX_COUNTER} ]]; do
      jstack -l ${PID} >> ${OUTPUT}-${COUNTER}.${EXTENSION}
      sleep ${WAIT_TIME}
      let COUNTER=COUNTER+1
    done
  fi
}

main "$@"