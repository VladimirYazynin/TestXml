public class Solution {

    public static void main(String[] args) {

        //Задание 2 вариант
        FileXml fileXml = new FileXml("src\\main\\resources\\FileForParse.xml");
        String parsedString = fileXml.find(22);
        String updatedString = fileXml.sendToNp(parsedString);
        fileXml.updateXML(parsedString, updatedString);

        //Задание 1 вариант
//        FileText fileText = new FileText("src\\main\\resources\\TextFileForParse.txt");
//        fileText.control(1000000008);
    }

}
