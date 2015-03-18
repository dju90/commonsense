package transpose;

import java.io.*;
import java.util.*;

/**
 * Examines a directory and determines if the first column contains colons, but
 * the other columns do not then it should be transposed so that all the column 
 * headings will be in the first row. Used to read in web tables converted into
 * a comma separated values (csv) file
 */
public class Transpose {
	
	public static String transposeDirectory = "../transposeTables";
	/**
	 * Takes in a directory, scans through all the files to transpose the file to the same format
	 * @param args Reads in a single directory to transpose all files within
	 * @throws IOException if there is a problem with the directory
	 */
    public static void main(String[] args) {
    	if (args.length == 1) {
    		File[] fileDir = new File(args[0]).listFiles();
			for (File f : fileDir) {
				scanFile(f);
			}
    	} else {
    		System.err.println("Usage: java Transpose <directory name>");
    		System.exit(0);
    	}
    }


    /**
     * Scans a file and searches to check if the first column ends with colons, but the next column does not
     * @param file the file to scan to check if it needs to be transposed
     */
    private static void scanFile(File file) {
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
                    }
                }
            }
            if (count > 0) {
                prepareTranspose(file);
            } else {
                writeToDir(transposeDirectory, file);
            }
            fileScan.close();
        } catch (Exception e) {
            System.err.println(file + " failed to read");
        }
    }

    /**
     * Read in the entire file to be transposed
     * @param file that will be fully read in as a string[][]
     */
    private static void prepareTranspose(File file) {
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

    /**
     * Transposes the file and writes back to file
     * @param matrix the file in matrix form to the transposed
     * @param file the filename to write back to that file
     */
    private static void transpose(String[][] matrix, File file) {
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

   /**
    * Takes the transposed matrix to back to the file
    * @param matrix take entire matrix to write to the file
    * @param file the file to be written
    */
    private static void writeToFile(String[][] matrix, File file) {
        try {
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
            writeToDir(transposeDirectory, file);
            fileWriter.close();
        } catch (IOException e) {
            logError(file, e);
        }
    }

    /**
     * Takes that file and writes to a different directory, will create if does not exist
     * @param dir the directory to copy the file to
     * @param file the file that should be copied to the directory
     */
    private static void writeToDir(String dir, File file) {
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

    /**
     * Copies a file from source to destination
     * @param source file from starting location
     * @param dest file to the ending location
     */
    private static void copyFile(File source, File dest) {
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

    /**
     * Write a failed operation and its error to an error log
     * @param file the file that failed or triggered an exception
     * @param e the exception that occurred
     */
    private static void logError(File file, Exception e) {
        try {
            FileWriter fw = new FileWriter("errorlog.txt", true);
            fw.write(file + ": " + e + "\n");
            fw.close();
        } catch (Exception io) {
            System.err.println("Failed to write error to log for " + file);
        }
    }

   /**
    * Extracts the last char of a word
    * @param word the string to find the last character (given the string is surrounded by quotes)
    * @return the last char of the word
    */
    private static char extractLastChar(String word) {
        return word.charAt(word.length() - 2);
    }
}