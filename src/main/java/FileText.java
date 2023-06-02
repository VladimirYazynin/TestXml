import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileText {

    private String pathFile;

    public FileText(String pathFile) {
        this.pathFile = pathFile;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public void control(int index){
        String foundString = find(index);
       // System.out.println(foundString);
        String resultString = SendToNp(foundString);
        updateTextFile(index, resultString);
    }

    public String find(Integer index){
        String foundString = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile)))){
            while(reader.ready()){
                String currentString = reader.readLine();
                if(currentString.indexOf(index.toString()) == 0){
                    foundString += currentString;
                    return foundString;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foundString;
    }

    public String SendToNp(String foundSting){
        String resultString = "";

        try (FileWriter fileWriter = new FileWriter("src\\main\\resources\\updateTextFile.txt")) {
            fileWriter.write(foundSting);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProcessBuilder pb = new ProcessBuilder("Notepad.exe", "src\\main\\resources\\updateTextFile.txt");
        try {
            pb.start().waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("src\\main\\resources\\updateTextFile.txt")));
            while(reader.ready()){
                resultString += reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultString;
    }

    public void updateTextFile(Integer index, String resultString){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile)));
             FileWriter fileWriter = new FileWriter("src\\main\\resources\\temp.txt")) {
            while (reader.ready()) {
                String currentString = reader.readLine();
                if (currentString.indexOf(index.toString()) == 0) {
                    fileWriter.write(resultString + "\n");
                } else{
                    fileWriter.write(currentString + "\n");
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Files.delete(Paths.get("src\\main\\resources\\TextFileForParse.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File oldFile = new File("src\\main\\resources\\temp.txt");
        File newFile = new File("src\\main\\resources\\TextFileForParse.txt");
        oldFile.renameTo(newFile);

    }

}
