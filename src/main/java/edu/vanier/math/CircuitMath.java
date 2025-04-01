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

    public List<Component> getTraversalPath() {
        Arrays.fill(visited, false);
        List<Component> path = new ArrayList<>();
        dfs(0, path); 
        return path;
    }

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

    public double getTotalCurrent() {
        double R = getTotalResistance();
        double V = getTotalVoltage();
        return R == 0 ? 0 : V / R;
    }
}
