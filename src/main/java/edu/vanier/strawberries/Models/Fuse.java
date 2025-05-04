//package edu.vanier.strawberries.Models;
//
//import javafx.scene.control.TextField;
//import javafx.scene.image.Image;
//import javafx.scene.layout.Pane;
//
//import java.net.URL;
//import java.util.Objects;
//
//public class Fuse extends Component {
//    private boolean blown;
//    private double maxCurrent;
//
//    public Fuse(Node begin, Node end, double maxCurrent, boolean diagramView) {
//        super(begin, end, diagramView);
//        this.maxCurrent = maxCurrent;
//        this.current = 0;
//        this.blown = false;
//
//        try {
//            URL imgUrl = getClass().getResource("/images/fuse_diagram.png");
//            DIAGRAM_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
//        } catch (Exception e) {
//            System.out.println("Could not load fuse diagram image");
//        }
//
//        try {
//            URL imgUrl = getClass().getResource("/images/fuse_real.png");
//            IMAGE_DISPLAY = new Image(Objects.requireNonNull(imgUrl).toExternalForm());
//        } catch (Exception e) {
//            System.out.println("Could not load fuse real image");
//        }
//
//        display = diagramView ? DIAGRAM_DISPLAY : IMAGE_DISPLAY;
//    }
//
//    public boolean isBlown() {
//        return blown;
//    }
//
//    public void setMaxCurrent(double maxCurrent) {
//        this.maxCurrent = maxCurrent;
//    }
//
//    public double getMaxCurrent() {
//        return maxCurrent;
//    }
//
//    
//    public void updateState(double totalCurrent) {
//        if (totalCurrent > maxCurrent && !blown) {
//            blown = true;
//            System.out.println("Fuse has blown! Max = " + maxCurrent + "A, Current = " + totalCurrent + "A");
//           
//        }
//    }
//
//   
//    public void handleEdit(Pane parentPane) {
//        TextField inputField = new TextField(String.valueOf(maxCurrent));
//        inputField.setPrefWidth(60);
//        inputField.setStyle("-fx-font-size: 10px; -fx-background-color: white; -fx-border-color: black;");
//
//        if (parentPane == null) {
//            System.out.println("No parent pane found for fuse!");
//            return;
//        }
//
//        double midX = (begin.getX() + end.getX()) / 2;
//        double midY = (begin.getY() + end.getY()) / 2;
//
//        inputField.setLayoutX(midX + 10);
//        inputField.setLayoutY(midY - 10);
//        parentPane.getChildren().add(inputField);
//        inputField.requestFocus();
//
//        inputField.setOnAction(_ -> updateMaxCurrentFromField(inputField, parentPane));
//        inputField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
//            if (!isNowFocused) {
//                updateMaxCurrentFromField(inputField, parentPane);
//            }
//        });
//    }
//
//    private void updateMaxCurrentFromField(TextField inputField, Pane parentPane) {
//        try {
//            double newMax = Double.parseDouble(inputField.getText());
//            if (newMax >= 0 && newMax <= 1000) {
//                this.maxCurrent = newMax;
//                System.out.println("Max current for fuse updated to: " + maxCurrent + " A");
//            } else {
//                System.out.println("Max current must be between 0–1000 A");
//            }
//        } catch (NumberFormatException ex) {
//            System.out.println("Invalid max current input.");
//        }
//
//        parentPane.getChildren().remove(inputField);
//    }
//}
