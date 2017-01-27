## Automark
## fix bad submission for automark
## by psksvp@gmail.com
## pongsak.suvanpong@mq.edu.au
## department of computing, Macquarie University


import zipfile,os.path
import os, glob, shutil

def unzip(zipFilePath, outputDirectory):
    with zipfile.ZipFile(zipFilePath) as zf:
        for member in zf.infolist():
            words = member.filename.split('/')
            #print(member.filename)
            path = outputDirectory
            for word in words[:-1]:
                drive, word = os.path.splitdrive(word)
                head, word = os.path.split(word)
                if word in (os.curdir, os.pardir, ''): 
                     continue
                path = os.path.join(path, word)
            zf.extract(member, path)

def zipDirectory(directoryPathToZip, outputZipFilePath):
    zip = zipfile.ZipFile(outputZipFilePath, 'w', zipfile.ZIP_DEFLATED)
    rootlen = len(directoryPathToZip) + 1
    for base, dirs, files in os.walk(directoryPathToZip):
          for file in files:
               fn = os.path.join(base, file)
               zip.write(fn, fn[rootlen:])        

def traverseForFilesRecursively(inDirectoryPath, fileTypeExtension):
     files = []
     for root, dirnames, filenames in os.walk(inDirectoryPath):
         files.extend(glob.glob(root + "/" + fileTypeExtension))
     return files


def putFilesInPath(directoryPath, listOfFiles):
    for filePath in listOfFiles:
        shutil.copy2(filePath, directoryPath)

def scanJavaSourceLineForPackageName(pathToJavaSourceFile):
    with open(pathToJavaSourceFile, 'r') as javaSourceFile:
        data = javaSourceFile.readlines()
        for line in data:
            line = line.strip()
            semiColonPos = line.find(';')
            if 0 == line.find('package') and semiColonPos > 0 and len(line) > 7:
                return line[7:semiColonPos].strip()
        
        return ''
   
def javaPackageNameToPath(packageString):
    return packageString.replace('.', os.sep)


def readTextFromFile(filepath):
    return open(filepath, "r").read()

def write(String, toFile):
    outputFile = open(toFile, "w")
    outputFile.write(String)
    


def extractOneFileFromZip(theZipFile, fileToGet, outputDir):
    zipArc = zipfile.ZipFile(theZipFile, "r")
    for filename in zipArc.namelist():
        if fileToGet == os.path.basename(filename):
            print("process " + filename)
            bytes = zipArc.read(filename)
            justNameOfZip = os.path.basename(theZipFile)
            outputFilePath = os.path.join(outputDir, justNameOfZip[:23] + "_" + fileToGet)
            with open(outputFilePath, "wb") as outputFile:
                outputFile.write(bytes)
            return
                
def doAllInDir(dir, outputDir): 
    allZipFiles = traverseForFilesRecursively(dir, "*.zip") 
    for aZipFile in allZipFiles:
        extractOneFileFromZip(aZipFile, "Game.java", outputDir)
        
        
def doIt4G():
    doAllInDir("/Users/psksvp/Workspace/temp2/gaurav/a1submissions", 
               "/Users/psksvp/Workspace/temp2/gaurav/output")                 
        


"""
fixing comp225 assignment 2 submission
"""
def doIt(workingDirectory, pathToTheBadZipFile, packagePath):
     badOutputPath = os.path.join(workingDirectory, 'zipOutput')
     os.makedirs(badOutputPath)
     unzip(zipFilePath = pathToTheBadZipFile, 
           outputDirectory = badOutputPath)
     files = traverseForFilesRecursively(inDirectoryPath=badOutputPath,
                                         fileTypeExtension = '*.zip')
     absGoodPath = os.path.join(workingDirectory, 'good')
     os.makedirs(absGoodPath)
     for filePath in files:
          print('doing -->' + filePath)
          shutil.rmtree(os.path.join(workingDirectory, 'temp'), True)
          absPackagePath = os.path.join(workingDirectory, 'temp', 'src', packagePath)
          absJunkPath = os.path.join(workingDirectory, 'temp', 'junk')
          os.makedirs(absPackagePath)
          os.makedirs(absJunkPath)
          unzip(zipFilePath = filePath, outputDirectory = absJunkPath)
          javaSourceFiles = traverseForFilesRecursively(absJunkPath, '*.java')
          putFilesInPath(directoryPath = absPackagePath, listOfFiles = javaSourceFiles)
          theGoodZipFileName = filePath[filePath.index('zipOutput'):].replace('/', '_').replace(' ', '_')
          
          print('writing-->' + theGoodZipFileName)
          zipDirectory(directoryPathToZip = os.path.join(workingDirectory, 'temp', 'src'),
                       outputZipFilePath = os.path.join(absGoodPath, theGoodZipFileName))
          
"""
fixing comp225 assignment 2 submission
this one won't need the package name passed as parameter
it will scan off from the source file
"""          
def fixJavaSourceSubmission(workingDirectory, pathToTheBadZipFile):
     badOutputPath = os.path.join(workingDirectory, 'zipOutput')
     os.makedirs(badOutputPath)
     unzip(zipFilePath = pathToTheBadZipFile, 
           outputDirectory = badOutputPath)
     files = traverseForFilesRecursively(inDirectoryPath=badOutputPath,
                                         fileTypeExtension = '*.zip')
     absGoodPath = os.path.join(workingDirectory, 'good')
     os.makedirs(absGoodPath)
     count = 0
     for filePath in files:
          print('-------------------------------------')
          print('doing -->' + filePath)
          shutil.rmtree(os.path.join(workingDirectory, 'temp'), True)
          packagePathPrefix = os.path.join(workingDirectory, 'temp', 'src')
          absJunkPath = os.path.join(workingDirectory, 'temp', 'junk')
          os.makedirs(absJunkPath)
          try:
              unzip(zipFilePath = filePath, outputDirectory = absJunkPath)
              javaSourceFiles = traverseForFilesRecursively(absJunkPath, '*.java')
          
              for javaSourceFile in javaSourceFiles:
                  try:
                      packageName = scanJavaSourceLineForPackageName(javaSourceFile)
                      print('source file --> ' + javaSourceFile + '  has package name -->' + packageName)
                      packageDir = packagePathPrefix
                      if len(packageName) > 0:
                          packageDir = os.path.join(packagePathPrefix, javaPackageNameToPath(packageName))     
                      if not os.path.exists(packageDir):
                          os.makedirs(packageDir)
                      shutil.copy2(javaSourceFile, packageDir)
                  except:
                      print("Unicode Error: " + filePath)
   
              theGoodZipFileName = filePath[filePath.index('zipOutput'):].replace('/', '_').replace(' ', '_')
         
              print('writing-->' + theGoodZipFileName)
              zipDirectory(directoryPathToZip = os.path.join(workingDirectory, 'temp', 'src'),
              outputZipFilePath = os.path.join(absGoodPath, theGoodZipFileName))
              count += 1
          except zipfile.error:
              print("skipping Bad zipfile:" + filePath)
              
          
          
     print("did process " + str(count) + " files")
     zipDirectory(directoryPathToZip=absGoodPath, outputZipFilePath=os.path.join(workingDirectory, 'Archive.zip'))
     print("wrote output to " + os.path.join(workingDirectory, 'archive.zip'))
     shutil.rmtree(os.path.join(workingDirectory, 'temp'), True)
     shutil.rmtree(os.path.join(workingDirectory, 'zipOutput'), True)
     shutil.rmtree(os.path.join(workingDirectory, 'good'), True)

"""
fix missing package name

"""
def fixMissingPackageName(pathToJavaSourceFile, packageName):
    foundPackageName = scanJavaSourceLineForPackageName(pathToJavaSourceFile)
    originalSource = open(pathToJavaSourceFile, "r").read()
    if(0 == len(foundPackageName)):
        #os.rename(pathToJavaSourceFile, pathToJavaSourceFile + ".bak")
        modifiedSource = "package " + packageName + ";\n" + originalSource
        outputFile = open(pathToJavaSourceFile, "w")
        outputFile.write(modifiedSource)
        print("insert package name " + packageName + " into source file " + pathToJavaSourceFile)
        

def fixBunchOfMissingPackageNameInZipFile(iLearnZipFilePath, packageName):
    outputDir = os.path.join(os.getcwd(), "fixingMissingPackageName")
    unzip(iLearnZipFilePath, outputDir)
    javaSourceFiles = traverseForFilesRecursively(outputDir, '*.java')
    for javaSourceFile in javaSourceFiles:
        fixMissingPackageName(javaSourceFile, packageName)

    zipDirectory(outputDir, os.path.basename(iLearnZipFilePath))
    print("wrote update to file " + os.path.basename(iLearnZipFilePath) + " in current directory")

def fixZipFileSubmissionForSingleFileAssignment(iLearnZipFilePath, classNameForThisAssignment):
    outputDir = os.path.join(os.getcwd(), "fixSubmitZipForSingleFile")
    unzip(iLearnZipFilePath, outputDir)
    zipFiles = traverseForFilesRecursively(outputDir, '*.zip')
    for zipFile in zipFiles:
        fixWhereStudentSubmitZipFile(zipFile, classNameForThisAssignment,
                                     ifFoundWriteToDir=outputDir)
        os.remove(zipFile)

    zipDirectory(outputDir, os.path.basename(iLearnZipFilePath))
    print("wrote update to file " + os.path.basename(iLearnZipFilePath) + " in current directory")


def fixWhereStudentSubmitZipFile(zipFilePath, classNameForThisAssignment, ifFoundWriteToDir):
    outputDir = os.path.join(os.getcwd(), "fixWhereStudentSubmitZipFile")    
    if(True == os.path.exists(outputDir)):
        shutil.rmtree(outputDir)    
    unzip(zipFilePath, outputDir)
    javaSourceFiles = traverseForFilesRecursively(outputDir, classNameForThisAssignment+'.java')
    if(0 == len(javaSourceFiles)):
        print("There is no " + classNameForThisAssignment+'.java' + " in zip file " + zipFilePath)
    else:
        sourceString = readTextFromFile(javaSourceFiles[0])
        newFileName = os.path.splitext(os.path.basename(zipFilePath))[0] + ".java"
        newFileNameWithPath = os.path.join(ifFoundWriteToDir, newFileName)
        print(newFileNameWithPath)
        write(sourceString, toFile=newFileNameWithPath)
        print("found " + classNameForThisAssignment+'.java ' +
              "in zip file " + zipFilePath + " wrote the file to " +
              newFileNameWithPath)
 
"""
read 2 columns csv file to python dict
""" 
def makeDictionaryFrom2ColumnsCSVFile(atPath, keyColumnIdx = 1, columnSeparatorChar=','):
    import csv
    csvReader = csv.reader(open(atPath, newline=''), delimiter=columnSeparatorChar)
    theDict = {}
    valueColumnIdx = 1
    if 1 == keyColumnIdx :
        valueColumnIdx = 0
    
    if 0 == keyColumnIdx :
        valueColumnIdx = 1
        
    for row in csvReader:
        #print(row[1] + "->", row[0]) 
        theDict[row[keyColumnIdx]] = row[valueColumnIdx]
    return theDict
    
    

            
if __name__ == "__main__":
    import sys
    print("fixing bad submission")
    if len(sys.argv) < 2:
        print("need path to iLearnDownload zip file in cmd argv")
    else:
        fixJavaSourceSubmission(workingDirectory = os.getcwd(),
                                pathToTheBadZipFile = sys.argv[1])
    
    
   