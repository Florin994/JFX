import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class App extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage window) {
        NumberAxis xAxis = new NumberAxis(1968, 2008, 4);
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.format("%.0f", object); // Format without commas, no decimal
            }
            @Override
            public Number fromString(String string) {
                return Double.parseDouble(string);
            }
        });
        xAxis.setLabel("Year");
        yAxis.setLabel("Relative support (%)");
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Relative support of the parties");
        Map<String, Map<Integer, Double>> values
                = readPartyData("partiesdata.tsv");
        values.keySet().forEach(party -> {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(party);
            values.get(party).forEach((year, support) -> series.getData().add(new XYChart.Data(year, support)));
            lineChart.getData().add(series);
        });
        Scene scene = new Scene(lineChart, 800, 600);
        window.setScene(scene);
        window.show();
    }

    private Map<String, Map<Integer, Double>> readPartyData(String fileName) {
        Map<String, Map<Integer, Double>> values = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/" + fileName)))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] pieces = line.split("\t");
                String party = pieces[0];
                Map<Integer, Double> partyData = new HashMap<>();
                int year = 1968;
                for (int i = 1; i < pieces.length; i++) {
                    if (!pieces[i].equals("-")) {
                        try {
                            partyData.put(year, Double.valueOf(pieces[i]));
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number format for year " + year + ": " + pieces[i]);
                        }
                    }
                    year += 4;
                }
                values.put(party, partyData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("File not found: " + fileName);
        }
        return values;
    }
}