# Basic barcode generator
Simple barcode generator for csv files
Uses barbecue library ref. http://barbecue.sourceforge.net

Tries to get the CSV files in a given folder and process each of them. The generated bar codes are saved under the output folder.

Example:
The code 11061991-20061911 in the file codes_to_process.csv under files_container folder
Path: files_container/codes_to_process.csv
Output folder: barcodes

The expected output is a png image in a specific folder. For the above example the output will be as follows:
An image named: 11061991-20061911.png
Saved under barcodes/codes_to_process/11061991-20061911.png

Please if you use this, fork it and if it really helps you give me a star.
