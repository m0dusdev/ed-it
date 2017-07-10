import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.plaf.basic.BasicBorders;


/**
 * A class that deals with file Io
 *
 */

public final class Io {

    private static String syntaxFromFile = "PLAIN";
    private static String toSave;


    static void compileJava() throws Exception {
        String toCompile = MainScreen.currentR.getText();
        System.out.println(toCompile);


        //PrintWriter out = new PrintWriter("src/Frame.java");
        // out.write(toCompile);
        // out.flush();
        // out.close();

        //System.out.print("flushed\n\n");
        String command = "cd src";
        String command2 = "javac Frame.java";
        String command3 = "java Frame";
        runProcess(command);
        runProcess(command2);
        runProcess(command3);

    }

    private static void printLines(String name, InputStream ins) throws Exception {
        String line;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(name + " " + line);
        }
    }

    private static void runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        printLines(command + " stdout:", pro.getInputStream());
        printLines(command + " stderr:", pro.getErrorStream());
        pro.waitFor();
        System.out.println(command + " exitValue() " + pro.exitValue());
    }




    /**
     * Save - saves gets text from current Pane - saves text to file.
     */

    static void saveAs() {

        toSave = MainScreen.currentR.getText();
        System.out.print(toSave);

        String path = MainScreen.tabbedPane.getTitleAt(MainScreen.current).
                replace("-", "");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(path));
        if (fileChooser.showSaveDialog(MainScreen.tabbedPane) == JFileChooser.APPROVE_OPTION) {

            try {
                File file = fileChooser.getSelectedFile();

                // create file
                PrintWriter out = new PrintWriter(file);

                // write string to file
                out.write(toSave);
                out.flush();
                out.close();
            } catch (FileNotFoundException f){
                f.printStackTrace();
            }

            // update hashmap to set just saved tab to true
            MainScreen.saveMap.put(MainScreen.tabbedPane.getTitleAt(MainScreen.current), true);

        }
    }

    /**
     * Open File, pass to a new Note object
     * @throws IOException - invalid file type or other file error
     */
    static void open() throws IOException{

        JFileChooser fileChooser;


        fileChooser = new JFileChooser("c:");
        fileChooser.setDialogType(1);
        fileChooser.setBorder(new BasicBorders.SplitPaneBorder(Uicolor.GREY, Uicolor.BOLD_GREY));


        if (fileChooser.showOpenDialog(MainScreen.tabbedPane) == JFileChooser.APPROVE_OPTION) {

            // get selected file location  - add to String type list - strip "[]" - add to text area
            String path = fileChooser.getSelectedFile().getPath();

            File tempFile = fileChooser.getSelectedFile();

            System.out.println(path);


            List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());


            StringBuilder buffer = new StringBuilder();
            for (String Final : lines) {
                buffer.append(Final);
                buffer.append("\n");
            }


            String fileText = buffer.toString();

            System.err.print(buffer);

            if (path.contains(".py")) {
                syntaxFromFile = "SYNTAX_STYLE_PYTHON";

            } else if (path.contains(".java")) {
                syntaxFromFile = "SYNTAX_STYLE_JAVA";

            } else if (path.contains(".html")) {
                syntaxFromFile = "SYNTAX_STYLE_HTML";

            } else if (path.contains(".css")) {
                syntaxFromFile = "SYNTAX_STYLE_CSS";

            } else if (path.contains(".txt")) {
                syntaxFromFile = "SYNTAX_STYLE_PLAIN";

            } else if (path.contains(".c")) {
                syntaxFromFile = "SYNTAX_STYLE_C";
            }

            MainScreen.tabbedPane.add(path, new Note(fileText, syntaxFromFile));

            MainScreen.tabbedPane.setSelectedIndex(MainScreen.current + 1);
            MainScreen.justAddedTab = true;


        }
    }

    // used to get user clipboard string content and add it to a new tab
    static void cliboardToTab() throws IOException, UnsupportedFlavorException {
        String temp = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        MainScreen.newTab("FromClipboard", "PLAIN", temp);
    }
}

