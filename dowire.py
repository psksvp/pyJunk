SetD = """#!/bin/sh
find . -type f -name "*.java" -exec javac -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar" {} \; > compile.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetD.CircleTest > CircleTest.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetD.Task1Test > Task1Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetD.Task3Test > Task3Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetD.Task4Test > Task4Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetD.Task6Test > Task6Test.output.txt
"""

SetC = """#!/bin/sh
find . -type f -name "*.java" -exec javac -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar" {} \; > compile.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetC.SquareTest > SquareTest.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetC.Task1Test > Task1Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetC.Task3Test > Task3Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetC.Task4Test > Task4Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetC.Task6Test > Task6Test.output.txt
"""

SetB = """#!/bin/sh
find . -type f -name "*.java" -exec javac -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar" {} \; > compile.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetB.LineTest > LineTest.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetB.Task1Test > Task1Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetB.Task3Test > Task3Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetB.Task4Test > Task4Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetB.Task6Test > Task6Test.output.txt
"""

SetA = """#!/bin/sh
find . -type f -name "*.java" -exec javac -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar" {} \; > compile.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetA.CircleTest > CircleTest.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetA.Task1Test > Task1Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetA.Task3Test > Task3Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetA.Task4Test > Task4Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetA.Task6Test > Task6Test.output.txt
"""

SetE = """#!/bin/sh
find . -type f -name "*.java" -exec javac -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar" {} \; > compile.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetE.LineTest > LineTest.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetE.Task1Test > Task1Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetE.Task3Test > Task3Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetE.Task4Test > Task4Test.output.txt
java -cp ".:/Users/psksvp/Downloads/junit-4.12.jar:/Users/psksvp/Downloads/hamcrest-core-1.3.jar"  org.junit.runner.JUnitCore midSemesterPracticalExamSetE.Task6Test > Task6Test.output.txt
"""


import zipfile
import shutil
import os
import subprocess
import stat 

def doWire(scriptSet, testFileDir):
    script = "echo no script"
    if scriptSet == 'SetD':
        script = SetD
    if scriptSet == "SetC":
        script = SetC 
    if scriptSet == "SetB":
        script = SetB 
    if scriptSet == "SetA":
        script = SetA 
    if scriptSet == "SetE":
        script = SetE         
    print('moving into ' + os.getcwd())
    os.chdir(os.getcwd())
    shutil.rmtree('output', True)
    zip = zipfile.ZipFile('./Archive.zip')
    zip.extractall('output')
    os.chdir('output')
    files = [f for f in os.listdir('.') if os.path.isfile(f)]
    for f in files:
        fn = os.path.splitext(os.path.basename(f))[0]
        dirN = fn.split('_')[1]
        print(dirN)
        os.mkdir(dirN)
        zip = zipfile.ZipFile(f)
        zip.extractall(dirN)
        os.remove(f)
        os.chdir(dirN)
        #unzip test file
        testFile = testFileDir + '/' + "midSemesterPracticalExam" + scriptSet +".zip"
        print('unzipping test file ' + testFile)
        
        try:
            subprocess.call(['unzip', '-o', testFile], timeout = 10)
        except TimeoutExpired:
            println("time out while running " + )     
        
        #running the test
        scriptFile = open('runtest.sh', 'w')
        scriptFile.write(script)
        scriptFile.flush()
        scriptFile.close()
        #st = os.stat('runtest.sh')
        #os.chmod('runtest.sh', st.st_mode | stat.S_IEXEC)
        print('going to run ' + dirN)
        subprocess.call(['sh', 'runtest.sh'])
        print("finish run " + dirN)
        #subprocess.popen(['sh', 'runtest.sh'], shell=False)
        #print('running')
        #p.wait
        #print('done')
        #print(p.returncode)
        os.chdir('..')
        
if __name__ == "__main__":
    import sys
    print('doing wire')
    if len(sys.argv) < 2:
        print("net set number")
    else:
        doWire(sys.argv[1], sys.argv[2])