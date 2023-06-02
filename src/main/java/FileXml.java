import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileXml {

    private String pathFile;

    public FileXml(String pathFile) {
        this.pathFile = pathFile;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    /**
     * Метод для нахождения эеметна "УчастникУчредитель" и его параметра "КодУчУчр"
     * @param index id для нахождения учередителя
     * @return возвращает участника учеридителя
     */

    public String find(long index) {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        String result = "";
        try {
            XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(new FileInputStream(pathFile));

            int count = 0;
            int countT = 0;
            boolean isLastEventStart = false;
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("УчастникУчредитель")) {
                    long getIndex = Long.parseLong(reader.getAttributeValue(null, "КодУчУчр"));
                    if (getIndex == index) {
                        for (int i = 0; i < countT; i++)
                            result += "    ";
                        result += "<" + reader.getLocalName();
                        for (int i = 0; i < reader.getAttributeCount(); i++)
                            result += String.format(" %s=\"%s\"", reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                        count++;
                        isLastEventStart = true;
                    }
                } else if (event == XMLStreamConstants.START_ELEMENT && count > 0) {
                    if(isLastEventStart)
                        result  += ">\n";
                    for (int i = 0; i < countT; i++)
                        result += "    ";
                    result += "<" + reader.getLocalName();
                    for (int i = 0; i < reader.getAttributeCount(); i++)
                        result += String.format(" %s=\"%s\"", reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                    isLastEventStart = true;
                    count++;
                } else if (event == XMLStreamConstants.END_ELEMENT && count > 0) {
                    count--;
                    if(isLastEventStart) {
                        result += "/>\n";
                        isLastEventStart = false;
                    } else if(count != 0){
                        for (int i = 0; i < countT - 1; i++)
                            result += "    ";
                        result += "</" + reader.getLocalName() + ">\n";
                    } else {
                        for (int i = 0; i < countT - 1; i++)
                            result += "    ";
                        result += "</" + reader.getLocalName() + ">";
                    }

                }
                if(event == XMLStreamConstants.START_ELEMENT)
                    countT ++;
                else if(event == XMLStreamConstants.END_ELEMENT)
                    countT --;
                if(reader.hasNext() == false){
                    if (result.endsWith("\n")) {
                        result = result.substring(0, result.length() - 1);
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Метод для передачи найденой строки в блокнот и сохранения изменений в строке
     * @param parsedString данные об участнике и учеридителе которые необходимо изменить
     * @return измененные данные участника и учередителя
     */

    public String sendToNp(String parsedString) {
        try (FileWriter fileWriter = new FileWriter("src\\main\\resources\\text.txt")) {
            fileWriter.write(parsedString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProcessBuilder pb = new ProcessBuilder("Notepad.exe", "src\\main\\resources\\text.txt");
        try {
            pb.start().waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String resultString = "";
        try (BufferedReader reader = new BufferedReader
                (new InputStreamReader(new FileInputStream("src\\main\\resources\\text.txt"), StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                resultString += reader.readLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (resultString.endsWith("\n")) {
            resultString = resultString.substring(0, resultString.length() - 1);
        }

        return resultString;
    }


    /**
     * Метод для обновления данных в XML файле
     * @param oldString данные которые необходимо заменить
     * @param newString данные на которые происходит замена
     */
    public void updateXML(String oldString, String newString) {

        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader
                    (new InputStreamReader(new FileInputStream("src\\main\\resources\\FileForParse.xml"), "Windows-1251"))){
            while (reader.ready()) {
                stringBuilder.append(reader.readLine() + "\n");
            }
            int startIndex = stringBuilder.indexOf(oldString);
            int lastIndex = startIndex + oldString.length();
            stringBuilder.replace(startIndex, lastIndex, newString);

            String replacedString = stringBuilder.toString().replace("\t", "    ");
            stringBuilder = new StringBuilder(replacedString);

            try (FileWriter writer = new FileWriter("src\\main\\resources\\NewXmlFile.xml",  Charset.forName("windows-1251"))) {
                writer.write(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            Files.delete(Paths.get("src\\main\\resources\\FileForParse.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        File oldFile = new File("src\\main\\resources\\NewXmlFile.xml");
        File newFile = new File("src\\main\\resources\\FileForParse.xml");
        oldFile.renameTo(newFile);
    }
}

