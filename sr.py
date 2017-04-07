import sys

s = open(sys.argv[1], "r").read()
s = s.replace("0x", "static_cast<char>(0x");
s = s.replace(",", "),");
s = s.replace("};", ")};");
print(s);
#outputFile = open(sys.argv[1], "w")
#outputFile.write(s);
#print(sys.argv[1])
