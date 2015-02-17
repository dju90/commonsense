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
     * Scans a file and transposes if necessary
     */
    public static void scanFile(File file) {
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
            if (count > 0) {
                prepareTranspose(file);
            } else {
                writeToDir("../transposeTables", file);
            }
            fileScan.close();
        } catch (Exception e) {
            System.err.println(file + " failed to read");
        }
    }

    public static void prepareTranspose(File file) {
        String[][] fullFile;
        Scanner fullScan = null;
        String line;
        String[] lineArr;
        try {
            fullScan = new Scanner(file);
            List<String[]> lineRows = new ArrayList<String[]>();
            while (fullScan.hasNext()) {
                line = fullScan.nextLine();
                lineArr = line.split(",");
                lineRows.add(lineArr);
            }
            int col = lineRows.get(0).length;
            fullFile = new String[lineRows.size()][col];
            for (int i = 0; i < lineRows.size(); i++) {
                fullFile[i] = lineRows.get(i);
            }
            transpose(fullFile, file);
            fullScan.close();
        } catch (Exception e) {
            System.err.println(file + " failed to read");
        }
    }

    /*
     * Transposes the file and writes back to file
     */
    public static void transpose(String[][] matrix, File file) {
        try {
            String[][] transpose = new String[matrix[1].length][matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    transpose[j][i] = matrix[i][j];
                }
            }
            writeToFile(transpose, file);
        } catch (Exception e) {
            logError(file, e);
        }
    }

    /*
     * Overwrites a file with matrix in csv format
     */
    public static void writeToFile(String[][] matrix, File file) {
        // false to overwrite
        try {
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
            writeToDir("../transposeTables", file);
            fileWriter.close();
        } catch (IOException e) {
            logError(file, e);
        }
    }

    public static void writeToDir(String dir, File file) {
        try {
            File transposeDir = new File(dir);
            if (!transposeDir.exists()) {
                transposeDir.mkdir();
            }
            File copyFile = new File(dir + "/" + file.getName());
            copyFile(file, copyFile);
        } catch (Exception e) {
            logError(file, e);
        }
    }

    public static void copyFile(File source, File dest) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            input.close();
            output.close();
        } catch (Exception e) {
            logError(dest, e);
        }
    }

    public static void logError(File file, Exception e) {
        try {
            FileWriter fw = new FileWriter("errorlog.txt", true);
            fw.write(file + ": " + e + "\n");
            fw.close();
        } catch (Exception io) {
            System.err.println("Failed to write error to log for " + file);
        }
    }

    /*
     * Extracts the last char of a word
     */
    public static char extractLastChar(String word) {
        return word.charAt(word.length() - 2);
    }

}