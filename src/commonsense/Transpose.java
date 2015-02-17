package commonsense;

import java.io.*;
import java.util.*;

public class Transpose {
    public static void main(String[] args) throws IOException {
        File[] fileDir = new File(args[0]).listFiles();
        for (File f : fileDir) {
            scanFile(f);
        }
    }

    /*
     * Extracts the last char of a word
     */
    public static char extractLastChar(String word) {
        return word.charAt(word.length() - 1);
    }

    /*
     * Scans a file and transposes if necessary
     */
    public static void scanFile(File file) throws IOException {
        Scanner fileScan  = null;
        try {
            fileScan = new Scanner(file);
            String line;
            String[] lineArr;
            int count = 0;
            while (fileScan.hasNext()  && count < 2) {
                line = fileScan.nextLine();
                lineArr = line.split(",");
                if (lineArr.length > 1) {
                    char firstWordColon = extractLastChar(lineArr[0]);
                    char secondWordColon = extractLastChar(lineArr[1]);
                    if (firstWordColon == ':' && secondWordColon != ':') {
                        count++;
                    } else {
                        break;
                    }
                }
            }
            if(count > 0) {
                String[][] fullFile;
                Scanner fullScan = null;
                try {
                    fullScan = new Scanner(file);
                    int row = 0;
                    int col = 0;
                    List<String[]> lineRows = new LinkedList<String[]>();
                    while (fullScan.hasNext()) {
                        line = fullScan.nextLine();
                        lineArr = line.split(",");
                        lineRows.add(lineArr);
                        col = lineArr.length;
                        row++;
                    }
                    fullFile = new String[row][col];
                    int i = 0;
                    for (String[] lines : lineRows ) {
                        fullFile[i] = lines;
                        i++;
                    }
                    transpose(fullFile, file);
                } finally {
                    if (fullScan != null) {
                        fullScan.close();
                    }
                }
            }
        } finally {
            if (fileScan != null) {
                fileScan.close();
            }
        }
    }

    /*
     * Transposes the file and writes back to file
     */
    public static void transpose(String[][] matrix, File file) throws IOException{
        String[][] transpose = new String[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                transpose[j][i] = matrix[i][j];
            }
        }
        writeToFile(transpose, file);
    }

    /*
     * Overwrites a file with matrix in csv format
     * TODO determine if we want a full pipeline in memory
     */
    public static void writeToFile(String[][] matrix, File file) throws IOException{
        // false to overwrite
        FileWriter fileWriter = new FileWriter(file, false);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (j < matrix[0].length - 1) {
                    fileWriter.write(matrix[i][j] + ",");
                } else {
                    fileWriter.write(matrix[i][j]);
                }
            }
            fileWriter.write("\n");
        }
        fileWriter.close();
    }
}


