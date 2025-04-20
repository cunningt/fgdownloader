///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.6.3
//DEPS commons-io:commons-io:2.13.0
//DEPS org.apache.commons:commons-csv:1.10.0
//DEPS com.opencsv:opencsv:5.8

import picocli.CommandLine;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.io.FileUtils;

@CommandLine.Command
public class steamerseparate {

    String steamerFile = "data/steamerhitting.csv";

    String steamerPitchingFile = "data/steamerpitching.csv";

    String minorsFile = "data/minors.csv";

    String minorsPitchingFile = "data/minorspitching.csv";

    private SeparationService separationService = new SeparationService();

    public void separate() {
        try {
            separationService.separate(steamerFile, minorsFile);
            separationService.pitchingSeparate(steamerPitchingFile, minorsPitchingFile);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main (String...args) {
        steamerseparate ss = new steamerseparate();
        ss.separate();
    }
}

class SeparationService {
    private File steamerFile;
    private File minorsFile;
    private File pitchingSteamerFile;
    private File pitchingMinorsFile;

    HashMap<String, String> minorsPlayers = new HashMap<String, String>();
    HashMap<String, String> pitchingMinorsPlayers = new HashMap<String, String>();


    public void readInFiles() throws IOException {
        List<String> steamerContents = FileUtils.readLines(steamerFile, "UTF-8");
        List<String> minorsContents = FileUtils.readLines(minorsFile, "UTF-8");

        for (String minorsPlayer : minorsContents) {
            String[] fields = minorsPlayer.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            String levels = fields[2];
            String id = fields[fields.length-1];

            levels = levels.replaceAll("\"", "");
            if (levels.contains(",")) {
                String[] templevels = levels.split(",");
                levels = templevels[templevels.length-1];
            }

            minorsPlayers.put(id, levels);
        }

        boolean header = true;
        File minors = new File("steamerminors.csv");
        if (minors.exists()) {
            minors.delete();
        }
        for (String steamerPlayer : steamerContents) {
            if (header) {
                FileUtils.writeStringToFile(minors, steamerPlayer + "\n", true);
                header = false;
            } else {

                String[] fields = steamerPlayer.split(",");
                String id = fields[fields.length-2];

                if (id.startsWith("\"sa")) {
                    FileUtils.writeStringToFile(minors, steamerPlayer + "\n", true);
                }
            }
        }

        File aFile = new File("a.csv");
        aFile.delete();
        File aplusFile = new File("a+.csv");
        aplusFile.delete();
        File aaFile = new File("aa.csv");
        aaFile.delete();
        File aaaFile = new File("aaa.csv");
        aaaFile.delete();
        header = true;
        for (String steamerPlayer : steamerContents) {
            if (header) {
                FileUtils.writeStringToFile(aFile, steamerPlayer + "\n", true);
                FileUtils.writeStringToFile(aplusFile, steamerPlayer + "\n", true);
                FileUtils.writeStringToFile(aaFile, steamerPlayer + "\n", true);
                FileUtils.writeStringToFile(aaaFile, steamerPlayer + "\n", true);
                header = false;
            } else {

                String[] fields = steamerPlayer.split(",");
                String id = fields[fields.length-2];
                String levels = minorsPlayers.get(id);

                if (id.startsWith("\"sa")) {
                    String level = minorsPlayers.get(id);
                    if (level != null) {
                        switch(level) {
                            case "A" :
                                FileUtils.writeStringToFile(aFile, steamerPlayer + "\n", true);
                                break;
                            case "A+" :
                                FileUtils.writeStringToFile(aplusFile, steamerPlayer + "\n", true);
                                break;
                            case "AA" :
                                FileUtils.writeStringToFile(aaFile, steamerPlayer + "\n", true);
                                break;
                            case "AAA" :
                                FileUtils.writeStringToFile(aaaFile, steamerPlayer + "\n", true);
                                break;
                        }
                    }
                }
            }
        }
    }
   public void pitchingReadInFiles() throws IOException {
        List<String> pitchingSteamerContents = FileUtils.readLines(pitchingSteamerFile, "UTF-8");
        List<String> pitchingMinorsContents = FileUtils.readLines(pitchingMinorsFile, "UTF-8");

        System.out.println("pitchingMinorsFile " + pitchingMinorsFile.getAbsolutePath());
        System.out.println("pitchingSteamerFile " + pitchingSteamerFile.getAbsolutePath());

        for (String minorsPlayer : pitchingMinorsContents) {
            String[] fields = minorsPlayer.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            String levels = fields[2];
            String id = fields[fields.length-1];

            levels = levels.replaceAll("\"", "");
            if (levels.contains(",")) {
                String[] templevels = levels.split(",");
                levels = templevels[templevels.length-2];
            }

            pitchingMinorsPlayers.put(id, levels);
        }

        boolean header = true;
        File minors = new File("steamerpitchingminors.csv");
        if (minors.exists()) {
            minors.delete();
        }
        for (String steamerPlayer : pitchingSteamerContents) {
            if (header) {
                FileUtils.writeStringToFile(minors, steamerPlayer + "\n", true);
                header = false;
            } else {

                String[] fields = steamerPlayer.split(",");
                String id = fields[fields.length-1];

                if (id.startsWith("\"sa")) {
                    FileUtils.writeStringToFile(minors, steamerPlayer + "\n", true);
                }
            }
        }

        File aFile = new File("aPitching.csv");
        aFile.delete();
        File aplusFile = new File("a+Pitching.csv");
        aplusFile.delete();
        File aaFile = new File("aaPitching.csv");
        aaFile.delete();
        File aaaFile = new File("aaaPitching.csv");
        aaaFile.delete();
        header = true;
        for (String steamerPlayer : pitchingSteamerContents) {
            if (header) {
                FileUtils.writeStringToFile(aFile, steamerPlayer + "\n");
                FileUtils.writeStringToFile(aplusFile, steamerPlayer + "\n");
                FileUtils.writeStringToFile(aaFile, steamerPlayer + "\n");
                FileUtils.writeStringToFile(aaaFile, steamerPlayer + "\n");
                header = false;
            } else {

                String[] fields = steamerPlayer.split(",");
                String id = fields[fields.length-2];


                if (id.startsWith("\"sa")) {
                    String level = pitchingMinorsPlayers.get(id);

                    //System.out.println("id " + id + " fields " + Arrays.toString(fields) + " level " + level);

                    if (level != null) {
                        switch(level) {
                            case "A" : 
                                FileUtils.writeStringToFile(aFile, steamerPlayer + "\n", true);
                                break;
                            case "A+" :
                                FileUtils.writeStringToFile(aplusFile, steamerPlayer + "\n", true);
                                break;
                            case "AA" :
                                FileUtils.writeStringToFile(aaFile, steamerPlayer + "\n", true);
                                break;
                            case "AAA" :
                                FileUtils.writeStringToFile(aaaFile, steamerPlayer + "\n", true);
                                break;
                        }
                    }
                }
            }
        }
    }

    public static void generateHtml(String csvPath, String htmlPath, String title) {
        try (
            CSVReader reader = new CSVReader(Files.newBufferedReader(Paths.get(csvPath)));
            PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(htmlPath)))
        ) {
            List<String[]> lines = reader.readAll();
            StringBuilder html = new StringBuilder();
    
            // HTML Head
            html.append("<!DOCTYPE html>\n");
            html.append("<html lang=\"en\">\n");
            html.append("<head>\n");
            html.append("  <meta charset=\"UTF-8\">\n");
            html.append("  <title>").append(title).append("</title>\n");
            html.append("  <link rel=\"stylesheet\" href=\"https://cdn.datatables.net/1.13.4/css/jquery.dataTables.min.css\">\n");
            html.append("  <script src=\"https://code.jquery.com/jquery-3.7.0.min.js\"></script>\n");
            html.append("  <script src=\"https://cdn.datatables.net/1.13.4/js/jquery.dataTables.min.js\"></script>\n");
            html.append("  <style>\n");
            html.append("    body {\n");
            html.append("      font-family: 'Segoe UI', sans-serif;\n");
            html.append("      padding: 40px;\n");
            html.append("      background-color: #f4f4f4;\n");
            html.append("    }\n");
            html.append("    h2 {\n");
            html.append("      text-align: center;\n");
            html.append("      margin-bottom: 30px;\n");
            html.append("    }\n");
            html.append("    table.dataTable {\n");
            html.append("      width: 90%;\n");
            html.append("      margin: auto;\n");
            html.append("      border-collapse: collapse;\n");
            html.append("    }\n");
            html.append("  </style>\n");
            html.append("</head>\n");
            html.append("<body>\n");
            html.append("  <h2>").append(title).append("</h2>\n");
            html.append("  <table id=\"csvTable\" class=\"display\">\n");
            html.append("    <thead>\n");
    
            // Header row
            String[] header = lines.get(0);
            html.append("      <tr>\n");
            for (String cell : header) {
                html.append("        <th>").append(escapeHtml(cell)).append("</th>\n");
            }
            html.append("      </tr>\n");
            html.append("    </thead>\n");
            html.append("    <tbody>\n");
    
            // Data rows
            for (int i = 1; i < lines.size(); i++) {
                html.append("      <tr>\n");
                for (String cell : lines.get(i)) {
                    html.append("        <td>").append(escapeHtml(cell)).append("</td>\n");
                }
                html.append("      </tr>\n");
            }
    
            html.append("    </tbody>\n");
            html.append("  </table>\n");
    
            // Script for DataTables
            html.append("  <script>\n");
            html.append("    $(document).ready(function() {\n");
            html.append("      $('#csvTable').DataTable({\n");
            html.append("        \"pageLength\": 100,\n");
            html.append("        \"responsive\": true\n");
            html.append("      });\n");
            html.append("    });\n");
            html.append("  </script>\n");
    
            html.append("</body>\n");
            html.append("</html>\n");
    
            writer.write(html.toString());
            System.out.println("✅ HTML file created: " + htmlPath);
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String escapeHtml(String text) {
        return text == null ? "" : text.replace("&", "&amp;")
                                       .replace("<", "&lt;")
                                       .replace(">", "&gt;")
                                       .replace("\"", "&quot;");
    }


    public void writeAbbreviatedCSV() throws FileNotFoundException, IOException {
        String[] levels = {"a.csv", "a+.csv", "aa.csv", "aaa.csv"};
        for (String level : levels) {
            Reader reader = new InputStreamReader(new BOMInputStream(new FileInputStream(level)), "UTF-8");

            String csvFile = "steamerabbreviated/" + level;
            String htmlFile = csvFile.replace(".csv", ".html");

            CSVPrinter printer = new CSVPrinter(new FileWriter("steamerabbreviated/" + level), CSVFormat.EXCEL);
            printer.printRecord("Name", "Team", "ISO", "BB%", "K%", "AVG", "OBP", "SLG", "OPS", "wRC+");
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            int i = 1;
            for (CSVRecord record : records) {
                String name = record.get("Name");
                String team = record.get("Team");
                String iso = record.get("ISO");
                String bb = record.get("BB%");
                String k = record.get("K%");
                String avg = record.get("AVG");
                String obp = record.get("OBP");
                String slg = record.get("SLG");
                String ops = record.get("OPS");
                String wrc = record.get("wRC+");
                printer.printRecord(name, team, iso, bb, k, avg, obp, slg, ops, wrc);
                i++;
            }
            printer.close(true);

            generateHtml(csvFile, htmlFile, htmlFile );

        }
    }

    public void writePitchingAbbreviatedCSV() throws FileNotFoundException, IOException {
        String[] levels = {"aPitching.csv", "a+Pitching.csv", "aaPitching.csv", "aaaPitching.csv"};
        for (String level : levels) {
            Reader reader = new InputStreamReader(new BOMInputStream(new FileInputStream(level)), "UTF-8");

            String csvFile = "steamerabbreviated/" + level;
            String htmlFile = csvFile.replace(".csv", ".html");

            CSVPrinter printer = new CSVPrinter(new FileWriter("steamerabbreviated/" + level), CSVFormat.EXCEL);
            printer.printRecord("Name", "Team", "IP", "K/9", "BB/9", "K-BB%", "WHIP", "ERA", "FIP");
            //printer.printRecord("Name", "Team", "IP", "WHIP", "ERA", "FIP");

            CSVParser parser = CSVParser.parse(reader, CSVFormat.RFC4180.withFirstRecordAsHeader());
            int i = 1;

            //System.out.println(parser.getHeaderMap().keySet());

            for (CSVRecord record : parser.getRecords()) {
                String name = record.get("Name");
                String team = record.get("Team");
                String ip = record.get("IP");
                String bb = record.get("BB/9"); // BB%
                String k = record.get("K/9"); // K%
                String whip = record.get("WHIP");
                String era = record.get("ERA");
                String fip = record.get("FIP");

                double kbb = (Double.valueOf(k) * 20/7.5) - (Double.valueOf(bb) * 20/7.5);

                printer.printRecord(name, team, ip, k, bb, kbb, whip, era, fip);
                i++;
            }
            printer.close(true);

            generateHtml(csvFile, htmlFile, htmlFile );

        }
    }

    void separate(String steamerFileName, String minorsFileName) throws FileNotFoundException, IOException {

        steamerFile = new File(steamerFileName);
        minorsFile = new File(minorsFileName);

        if (!steamerFile.exists()) {
            throw new FileNotFoundException("Could not find the steamer csv file at " + steamerFileName);
        }

        if (!minorsFile.exists()) {
            throw new FileNotFoundException("Could not find the minors csv file at " + minorsFileName);
        }

        readInFiles();
        writeAbbreviatedCSV();
    }

    void pitchingSeparate(String steamerFileName, String minorsFileName) throws FileNotFoundException, IOException {

        pitchingSteamerFile = new File(steamerFileName);
        pitchingMinorsFile = new File(minorsFileName);

        if (!pitchingSteamerFile.exists()) {
            throw new FileNotFoundException("Could not find the steamer csv file at " + steamerFileName);
        }

        if (!pitchingMinorsFile.exists()) {
            throw new FileNotFoundException("Could not find the minors csv file at " + minorsFileName);
        }

        pitchingReadInFiles();
        try {
        writePitchingAbbreviatedCSV();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
