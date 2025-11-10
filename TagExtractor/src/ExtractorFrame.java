import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.StandardOpenOption.CREATE;

public class ExtractorFrame extends JFrame {

    JPanel mainPnl;

    JPanel topPnl;
    JLabel title;

    JPanel middlePnl;
    JTextArea textArea;
    JScrollPane scrollPane;
    JTextArea nameOfFile;

    JPanel bottomPnl;
    JButton loadFile;
    JButton loadStopFile;
    JButton parseTags;
    JButton toFile;
    JButton quit;


    JFileChooser chooser = new JFileChooser();
    File selectedFile;
    File stopFile;
    String rec = "";

    Map<String, Integer> wordCount;
    Set<String> stopWords;

    public ExtractorFrame(){
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());

        createTopPanel();
        mainPnl.add(topPnl,BorderLayout.NORTH);

        createMiddlePanel();
        mainPnl.add(middlePnl,BorderLayout.CENTER);

        createBottomPanel();
        mainPnl.add(bottomPnl,BorderLayout.SOUTH);

        add(mainPnl);
        setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createTopPanel(){
        topPnl = new JPanel();
        title =  new JLabel("Extractor Frame");
        topPnl.add(title);
    }

    private void createMiddlePanel(){
        middlePnl = new JPanel();
        textArea = new JTextArea(10,30);
        scrollPane = new JScrollPane(textArea);

        nameOfFile = new JTextArea(1,20);

        middlePnl.add(scrollPane,BorderLayout.CENTER);
        middlePnl.add(nameOfFile,BorderLayout.SOUTH);
    }

    private void createBottomPanel(){
        bottomPnl = new JPanel();

        wordCount = new HashMap<>();
        stopWords = new HashSet<>();

        quit = new JButton("Quit");
        quit.addActionListener((ActionEvent ae) -> System.exit(0));

        loadFile = new JButton("Load File");
        loadFile.addActionListener((ActionEvent ae) -> {
            try {

                File workingDirectory = new File(System.getProperty("user.dir"));

                chooser.setCurrentDirectory(workingDirectory);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();

                    InputStream in =
                            new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(in));

                    int line = 0;

                    while (reader.ready()) {
                        rec = reader.readLine();
                        line++;
                    }

                    reader.close();
                    System.out.println("\n\nData file read!");

                    nameOfFile.setText(selectedFile.getName());

                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        loadStopFile = new JButton("Load Stop File");
        loadStopFile.addActionListener((ActionEvent ae) -> {
            try {

                File workingDirectory = new File(System.getProperty("user.dir"));

                chooser.setCurrentDirectory(workingDirectory);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    stopFile = chooser.getSelectedFile();
                    Path file = stopFile.toPath();

                    InputStream in =
                            new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(in));

                    Files.lines(stopFile.toPath()).map(String::toLowerCase).forEach(stopWords::add);

                    reader.close();
                    System.out.println("\n\nData file read!");
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        parseTags = new JButton("Parse Tags");
        parseTags.addActionListener((ActionEvent ae) -> {

            //I was struggling with this part, so I had an AI (ChatGPT) help me with what I needed to do. This code below isn't all my own due to that.
            //If you need me to do anything to change this, or if you need me to provide chat logs or proper credit, can you let me know?

            try {
                //Get words from file
                Files.lines(selectedFile.toPath())
                        .flatMap(line -> Arrays.stream(line.split("\\W+")))
                        .map(String::toLowerCase)
                        .filter(word -> !word.isEmpty())
                        .filter(word -> !stopWords.contains(word))
                        .forEach(word -> wordCount.merge(word, 1, Integer::sum));

                //Update text area with tags
                wordCount.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).forEach(entry -> textArea.append(entry.getKey() + ": " + entry.getValue() + "\n"));

            }catch (IOException e){
                e.printStackTrace();
            }

        });

        toFile = new JButton("Save Tags To File");
        toFile.addActionListener((ActionEvent ae) -> {

            File workingDirectory = new File(System.getProperty("user.dir"));
            Path file = Paths.get(workingDirectory.getPath() + "\\src\\Tags.txt");

            try
            {
                // Typical java pattern of inherited classes
                // we wrap a BufferedWriter around a lower level BufferedOutputStream
                OutputStream out =
                        new BufferedOutputStream(Files.newOutputStream(file, CREATE));
                BufferedWriter writer =
                        new BufferedWriter(new OutputStreamWriter(out));

                // Finally can write the file LOL!

                writer.write(textArea.getText());

                writer.close(); // must close the file to seal it and flush buffer
                System.out.println("Data file written!");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });

        bottomPnl.add(loadFile);
        bottomPnl.add(loadStopFile);
        bottomPnl.add(parseTags);
        bottomPnl.add(toFile);
        bottomPnl.add(quit);

    }
}
