package edu.vanier.math;

import edu.vanier.strawberries.*;
import edu.vanier.strawberries.Components.*;

import java.util.*;

public class CircuitMath {

    private final Circuit circuit;
    private boolean[] visited;

    public CircuitMath(Circuit circuit) {
        this.circuit = circuit;
        this.visited = new boolean[circuit.arrayList.size()];
    }

    // Depth-First Search traversal
    private void dfs(int current, List<Component> path) {
        if (visited[current]) return;
        visited[current] = true;

        Component component = circuit.arrayList.get(current).getFirst();
        path.add(component);

        for (int i = 0; i < circuit.arrayList.size(); i++) {
            if (circuit.checkEdge(current, i)) {
                dfs(i, path);
            }
        }
    }

    // Returns a list of components in order of connection
    public List<Component> getTraversalPath() {
        Arrays.fill(visited, false);
        List<Component> path = new ArrayList<>();

        for (int i = 0; i < circuit.arrayList.size(); i++) {
            Component c = circuit.arrayList.get(i).getFirst();
            if (c instanceof Battery || circuit.arrayList.get(i).size() > 1) {
                dfs(i, path);
                break;
            }
        }

        return path;
    }

    // Total resistance (series only for now)
    public double getTotalResistance() {
        List<Component> path = getTraversalPath();
        double resistance = 0.0;
        for (Component c : path) {
            if (c instanceof Resistor r) {
                resistance += r.getResistance();
            }
        }
        return resistance;
    }

    // Total voltage provided by all batteries
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

    // Computes total current using Ohm’s Law: I = V / R
    public double getTotalCurrent() {
        double R = getTotalResistance();
        double V = getTotalVoltage();
        return R == 0 ? 0 : V / R;
    }

    // Assigns voltage and current to each component
    public void assignValuesToComponents() {
        double totalCurrent = getTotalCurrent();

        System.out.println("⚡ Total voltage: " + getTotalVoltage() + " V");
        System.out.println("⚡ Total current: " + totalCurrent + " A");

        for (LinkedList<Component> list : circuit.arrayList) {
            Component c = list.getFirst();

            if (c instanceof Wire wire) {
                wire.setCurrent(totalCurrent);
                wire.setVoltage(0);
                System.out.println(" Wire updated: I=" + wire.getCurrent());
            }

            if (c instanceof Resistor resistor) {
                double R = resistor.getResistance();
                resistor.setCurrent(totalCurrent);
                resistor.setVoltage(totalCurrent * R);
                System.out.println(" Resistor updated: V=" + resistor.getVoltage() + ", I=" + resistor.getCurrent());
            }

            if (c instanceof Battery battery) {
                System.out.println(" Battery detected with potential: " + battery.getPotential() + " V");
            }
        }
    }
}
