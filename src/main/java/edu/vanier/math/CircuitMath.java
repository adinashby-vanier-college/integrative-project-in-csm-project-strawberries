package edu.vanier.math;

import edu.vanier.strawberries.Models.*;
import java.util.*;
import javafx.scene.control.Alert;

public class CircuitMath {

    private final Circuit circuit;
    private boolean[] visited;

    public CircuitMath(Circuit circuit) {
        this.circuit = circuit;
        this.visited = new boolean[circuit.arrayList.size()];
    }

    
    private void search(int current, List<Component> path) {
        if (visited[current]) return;
        visited[current] = true;

        Component component = circuit.arrayList.get(current).getFirst();
        path.add(component);

        for (int i = 0; i < circuit.arrayList.size(); i++) {
            if (circuit.checkEdge(current, i)) {
                search(i, path);
            }
        }
    }

    
    public List<Component> getTraversalPath() {
        Arrays.fill(visited, false);
        List<Component> path = new ArrayList<>();

        for (int i = 0; i < circuit.arrayList.size(); i++) {
            Component c = circuit.arrayList.get(i).getFirst();
            if (c instanceof Battery || circuit.arrayList.get(i).size() > 1) {
                search(i, path);
                break;
            }
        }
        return path;
    }

    
    public double getTotalResistance() {
        List<Component> path = getTraversalPath();
        double totalResistance = 0.0;

        for (Component c : path) {
            if (c instanceof Resistor r) {
                totalResistance += r.getResistance();
            }
        }

        return totalResistance;
    }

   
    public double getTotalVoltage() {
        List<Component> path = getTraversalPath();
        double voltage = 0.0;

        for (Component c : path) {
            if (c instanceof Battery b) {
                voltage += b.getPotential();
            }
        }
        return voltage;
    }

    // Calculates total current using Ohm's Law: I = V / R
    public double getTotalCurrent() {
        double R = getTotalResistance();
        double V = getTotalVoltage();
        //System.out.println("Voltage = " + V + ", Resistance = " + R + ", Current = " + V/R );

       return  R == 0 ? 0 : V / R;  //for testing purposese you can remove this after
    }

    public double getVoltageAcrossResistor(Resistor resistor) {
        double current = getTotalCurrent();
        return current * resistor.getResistance();
    }

    public static void setVoltageAcross(Component component) {
        if(component.getResistance() > 0 && component.getCurrent() > 0) {
            component.setVoltage(component.getResistance()*component.getCurrent());
        }
    }
 
    public void assignValuesToComponents() {
        double totalCurrent = getTotalCurrent();
        double totalVoltage = getTotalVoltage();

     /*   if (totalCurrent == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Current Flowing");
            alert.setHeaderText(null);
            alert.setContentText("Total current is zero. Please check the circuit connections!");
            alert.showAndWait();
        }
*/
        System.out.println("Total voltage: " + totalVoltage + " V");
        System.out.println("Total current: " + totalCurrent + " A");

        for (LinkedList<Component> list : circuit.arrayList) {
            Component c = list.getFirst();

            if (c instanceof Wire wire) {
                wire.setCurrent(totalCurrent);
                wire.setVoltage(0); // wires assumed ideal
            }

            if (c instanceof Resistor resistor) {
                if (!resistor.isConnected(circuit)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Unconnected Resistor");
                    alert.setHeaderText(null);
                    alert.setContentText("A resistor is not connected to any components.");
                    alert.showAndWait();
                } else {
                    resistor.setCurrent(totalCurrent);
                    resistor.setVoltage(getVoltageAcrossResistor(resistor));
                    System.out.println("Resistor updated: V=" + resistor.getVoltage() + "V, I=" + resistor.getCurrent() + "A");
                }
            }

            if (c instanceof Battery battery) {
                System.out.println("Battery detected with potential: " + battery.getPotential() + " V");
            }
        }
    }
}
