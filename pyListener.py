#!/usr/local/bin/python

# exe python code server
# POST a python code, and get the result back
# by psksvp@gmail.com
from flask import Flask
from flask import request
import sys
app = Flask(__name__)

@app.route('/exec', methods = ['POST', 'GET'])
def main():
  quit = request.form['quit']
  if 'True' == quit:
    print('listener is exiting')
    sys.exit(0)
    return 'quitting'
  else:
    code = request.form['code']
    return execCode(code)

def execCode(code):
  from cStringIO import StringIO
  old_stdout = sys.stdout
  redirected_output = sys.stdout = StringIO()
  exec(code)
  sys.stdout = old_stdout
  return redirected_output.getvalue()

if __name__ == '__main__':
  app.run()