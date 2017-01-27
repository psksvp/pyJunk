#!/usr/local/bin/python3

from sys import stdin
import time
import string
import shutil
import subprocess
import sys

print("versioning by psksvp@gmail.com")

while True:
    line = stdin.readline().replace("\n", "")
    new = line + "-" + time.strftime("%c").replace(" ", ".")
    print("new version -> " + new)
    shutil.copy(line, new)
