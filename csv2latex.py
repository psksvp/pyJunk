import csv 
import sys


def csv2latex(file):
    csvfile = open(file + '.csv', 'rU')
    csvreader = csv.reader(csvfile, delimiter = ',', quotechar='|')
    csv_table = []
    for row in csvreader:
        csv_table.append(row)
    myfile = open(file + '.txt', 'a')
    for row in csv_table:
        for item in row:
            temp_str = str() #temporary string 
            if item == row[-1]:
                temp_str +=  item +  " \\\\" + "\n"
                #We need to use the "\\\\" as python interprets
                #"\\" as an escaped "\" character
            elif item == row[0]:
                temp_str = item + ' & ' 
            else:
                temp_str +=  item + '&'
            
            myfile.write(temp_str)


if __name__ == "__main__":
    # stuff only to run when not called via 'import' here
    for file in sys.argv:
        if file != sys.argv[0]: #sys.argv[0] is the name of the script itself, e.g. "csv2latex.py"
            csv2latex(file)

