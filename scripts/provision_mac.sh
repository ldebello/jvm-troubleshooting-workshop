#!/bin/sh

# List of dependencies to use
VISUAL_VM='visualvm'
MAT='mat'
ANT='ant'
MAVEN='maven'

JMC='jmc'
TDA='tda'
GC_VIEWER='gc_viewer'
JIT_WATCH='jitwatch'
J_ENV='jenv'
JVM_TOP='jvmtop'
FILE_LEAK_DETECTOR='file_leak_detector'

JMC_VERSION=b123

# Colors
red=$(tput setaf 1)
green=$(tput setaf 2)
normal=$(tput sgr0)

INSTALLATION_DIR=../tools

function confirmation_request() {
  read -p "Continue (y/N)? " choice
  case "$choice" in
    y|Y ) printf "\nRunning\n";;
    * ) printf "\nAborted!\n"; exit 1;;
  esac
}

function install_with_brew_cask() {
  if [[ `brew cask ls --versions $1` ]]; then
    printAlreadyInstalled $1
  else
    printNotInstalled $1
    printInstalling $1
    brew cask install $1
  fi
}

function uninstall_with_brew_cask() {
  printf "\nUninstalling $1...\n"
  brew cask uninstall --force $1
}

function install_with_brew() {
  if [[ `brew ls --versions $1` ]]; then
    printAlreadyInstalled $1
  else
    printNotInstalled $1
    printInstalling $1
    brew install $1
  fi
}

function uninstall_with_brew() {
  printf "\nUninstalling $1...\n"
  brew uninstall --force $1
}

function printInstalling() {
  printf "Installing $1...\n"
}

function printAlreadyInstalled() {
  printf "$1 is already installed"
  success
}

function printNotInstalled() {
  printf "$1 is not installed"
  fail
}

function success() {
  printf " ${green}✓${normal}\n"
}

function fail() {
  printf " ${red}✖${normal}\n"
}

function download() {
  printf "Downloading ${blue}%s${normal}.\n" $1
  curl --progress-bar -Lo $1 $2
}

function main () {
  printf "Welcome to the JVM Troubleshooting Workshop tool provision script!\n"

  if [[ "$1" == "check" ]]; then
    CHECK=YES
    printf "Checking currently installed dependencies\n"
  elif [[ "$1" == "remove" ]]; then
    REMOVE=YES
    printf "ARE YOU SURE YOU WANT TO REMOVE?\n"
    printf "This will ${red}delete EVERYTHING${normal}\n"
    confirmation_request
  else
    printf "Do you want to install all the required dependencies?\n"
    confirmation_request
  fi

  mkdir -p $INSTALLATION_DIR

  if [[ `command -v brew` ]]; then
    for dependency in $J_ENV $MAVEN $VISUAL_VM $MAT $ANT $JMC $TDA $JVM_TOP $GC_VIEWER $JIT_WATCH $FILE_LEAK_DETECTOR; do
      if [[ "$CHECK" == "YES" ]]; then
        check_installed_with_brew_cask $dependency
      elif [[ "$REMOVE" == "YES" ]]; then
        uninstall_with_brew_cask $dependency
      else
        if [[ $dependency == "$JMC" ||  $dependency == "$TDA" ||  $dependency == "$JVM_TOP" ||  $dependency == "$GC_VIEWER" ||  $dependency == "$JIT_WATCH" ||  $dependency == "$FILE_LEAK_DETECTOR" ]]; then
          install_$dependency $dependency
        elif [[ $dependency == "$ANT" ||  $dependency == "$MAVEN" ||  $dependency == "$J_ENV" ]]; then
          install_with_brew ${dependency}
        else
          install_with_brew_cask ${dependency}
        fi
      fi
    done
  else
    printf "Homebrew needs to be installed to continue\n"
    false
  fi
}

function install_jmc() {
  if [[ `ls /Applications | grep "JDK Mission Control"` ]]; then
    printAlreadyInstalled $1
  else
    printNotInstalled $1
    printInstalling $1
    download ${1}.tar.gz https://builds.shipilev.net/jmc/jmc-${JMC_VERSION}-macosx.tar.gz
    tar --extract --file ${1}.tar.gz *.app
    mv *.app /Applications/
    rm ${1}.tar.gz
  fi
}

function install_tda() {
  if [[ -d "${INSTALLATION_DIR}/$1" ]]; then
    printAlreadyInstalled $1
  else
    rm -f $1*

    git clone git@github.com:ldebello/tda.git ${INSTALLATION_DIR}/$1

    ant -f ${INSTALLATION_DIR}/$1/tda/build.xml makedist

    mv ${INSTALLATION_DIR}/$1/tda/builddist/$1.jar ${INSTALLATION_DIR}/$1.jar

    echo "#!/bin/sh" >> ${INSTALLATION_DIR}/$1.sh
    echo "DIR=\$( cd \$(dirname \$0) ; pwd -P )" >> ${INSTALLATION_DIR}/$1.sh
    echo "java -Xmx512m -jar \${DIR}/$1.jar" >> ${INSTALLATION_DIR}/$1.sh

    chmod 755 ${INSTALLATION_DIR}/$1.sh
  fi
}

function install_jvmtop() {
  if [[ -d "${INSTALLATION_DIR}/$1" ]]; then
    printAlreadyInstalled $1
  else
    rm -f $1*

    git clone git@github.com:ldebello/jvmtop.git ${INSTALLATION_DIR}/$1

    mvn clean package -f ${INSTALLATION_DIR}/$1/pom.xml

    tar xf ${INSTALLATION_DIR}/$1/target/jvmtop-0.9.0-SNAPSHOT.tar.gz -C ${INSTALLATION_DIR}/$1/target

    mv ${INSTALLATION_DIR}/$1/target/jvmtop-0.9.0-SNAPSHOT/jvmtop.sh ${INSTALLATION_DIR}/jvmtop.sh
    mv ${INSTALLATION_DIR}/$1/target/jvmtop-0.9.0-SNAPSHOT/jvmtop.jar ${INSTALLATION_DIR}/jvmtop.jar

    chmod 755 ${INSTALLATION_DIR}/$1.sh
  fi
}

function install_gc_viewer() {
  if [[ -d "${INSTALLATION_DIR}/$1" ]]; then
    printAlreadyInstalled $1
  else
    rm -f $1*

    git clone git@github.com:chewiebug/GCViewer.git ${INSTALLATION_DIR}/$1

    mvn clean package -f ${INSTALLATION_DIR}/$1/pom.xml -DskipTests -DskipITs

    mv ${INSTALLATION_DIR}/$1/target/*.jar ${INSTALLATION_DIR}/$1.jar

    echo "#!/bin/sh" >> ${INSTALLATION_DIR}/$1.sh
    echo "DIR=\$( cd \$(dirname \$0) ; pwd -P )" >> ${INSTALLATION_DIR}/$1.sh
    echo "java -Xmx512m -jar \${DIR}/$1.jar" >> ${INSTALLATION_DIR}/$1.sh

    chmod 755 ${INSTALLATION_DIR}/$1.sh
  fi
}

function install_jitwatch() {
  if [[ -d "${INSTALLATION_DIR}/$1" ]]; then
    printAlreadyInstalled $1
  else
    rm -f $1*

    git clone git@github.com:AdoptOpenJDK/jitwatch.git ${INSTALLATION_DIR}/$1

    mvn clean package -f ${INSTALLATION_DIR}/$1/pom.xml -DskipTests -DskipITs

    echo "#!/bin/sh" >> ${INSTALLATION_DIR}/$1.sh
    echo "DIR=\$( cd \$(dirname \$0) ; pwd -P )" >> ${INSTALLATION_DIR}/$1.sh
    echo "cd \${DIR}/jitwatch" >> ${INSTALLATION_DIR}/$1.sh
    echo "./launchUI.sh" >> ${INSTALLATION_DIR}/$1.sh

    chmod 755 ${INSTALLATION_DIR}/$1.sh
  fi
}

function install_file_leak_detector() {
  if [[ -d "${INSTALLATION_DIR}/$1" ]]; then
    printAlreadyInstalled $1
  else
    rm -f $1*

    git clone git@github.com:kohsuke/file-leak-detector.git ${INSTALLATION_DIR}/$1

    mvn clean package -f ${INSTALLATION_DIR}/$1/pom.xml -DskipTests -DskipITs

    mv ${INSTALLATION_DIR}/$1/target/file-leak-detector-*-SNAPSHOT-jar-with-dependencies.jar ${INSTALLATION_DIR}/$1.jar
  fi
}

main "$@"