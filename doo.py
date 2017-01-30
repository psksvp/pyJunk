#!/usr/bin/env python3

# created by psksvp (psksvp@gmail.com)
# doo.listener@gmail.com is a robot that downloads vdos from youtube when it gets an email with 
# urls to the vdos. 
# doo uses youtube-dl to do the actual download. 

import subprocess
import os
import time
import urllib.request
import logging


destinationDirectory = "/media/psksvp/sd32/videos/"

def mainLoop():
    while(True):
        logging.info("running..")
        run()
        logging.info("sleeping..")
        time.sleep(60)
          
def run():
    text = getMail()
    urlList =  getUrlList(text)
    for url in urlList:
        if False == isInHistory(url):
            logging.info("getting url " + url)
            download(url)
        else:
            logging.info("ignoring url " + url)

def getMail():
    return urlRequest("https://mail.google.com/mail/feed/atom", 
                      user=readText("address"), 
                      password=readText("pass"))

def addToHistory(url):
    with open("history", "a") as file:
        file.write(url)
        file.close()

def isInHistory(url):
    with open("history", "r") as file:
        text = file.read()
        if text.find(url) >= 0:
          return True
        else:
          return False        
    return False   
    
def download(url):
    subprocess.Popen(["youtube-dl", "-o", destinationDirectory + "%(title)s.%(ext)s", url]).wait()
    addToHistory(url+"\n")             

## lazy man parsing
def getUrlList(text):
    a = substringList(text, "https://", " ")
    b = substringList(text, "http://", " ")
    #a = substringList(text, "https://you", " ")   
    #b = substringList(text, "https://www.you", " ") 
    #c = substringList(text, "http://www.you", " ")
    return a + b



#############
## generic 
#############

def readText(fileName):
    with open(fileName, "r") as file:
        text = file.read()
        file.close()
        return text
    logging.warning("Fail readText from file " + fileName)    
    return ""

def substringList(text, startMarker, endMarker):
    result = []
    keepGoing = True
    idx1 = text.find(startMarker)
    while(idx1 >= 0):
        idx2 = text.find(endMarker, idx1)
        if idx2 >= 0:
            result.append(text[idx1:idx2])
            idx1 = text.find(startMarker, idx2 + 1)
        else:
            idx1 = -1     
    return result  
    
def urlRequest(url, user="", password=""):
    try:
        if "" == user:
            return urllib.request.urlopen(url).read().decode('utf-8')
        else:
            passman = urllib.request.HTTPPasswordMgrWithDefaultRealm()
            passman.add_password(None, url, user, password)
            authhandler = urllib.request.HTTPBasicAuthHandler(passman)
            opener = urllib.request.build_opener(authhandler)
            urllib.request.install_opener(opener) 
            return urllib.request.urlopen(url).read().decode('utf-8') 
    except:
        logging.warning("Fail urlRequest with URL " + url)
        return ""      
    
    
if __name__ == "__main__":
    logging.basicConfig(format='%(asctime)s %(message)s', level=logging.DEBUG)
    mainLoop()       
   

