# Basic barcode generator
Simple barcode generator for csv files
Uses barbecue library ref. http://barbecue.sourceforge.net/
Tries to get the CSV files in a given folder and process each of them. The generated bar codes are saved under the output folder.

Ej.
Code 11061991-20061911 in the file codes_to_process.csv under files_container folder
Path: files_container/codes_to_process.csv
Output folder: barcodes

The expected output and png image in a specific folder, for the aboc example
11061991-20061911.png is saved in barcodes/<codes_to_process>/11061991-20061911.png
