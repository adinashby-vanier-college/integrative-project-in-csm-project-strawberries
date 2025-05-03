package edu.vanier.strawberries.Models;

import java.util.*;

/**
 * The Circuit is based on a Graph data structure
 */
public class Circuit {
    boolean closed;
    public ArrayList<LinkedList<Component>> arrayList;
    private boolean diagramView;

    /**
     * Default constructor for the Circuit class.
     * @param diagramView
     */
    public Circuit(boolean diagramView) {
        closed = false;
        arrayList = new ArrayList<>();
        this.diagramView = diagramView;
    }

    /**
     * Adds a component to the current circuit.
     * @param component The component to be added to the calling circuit
     */
    public void addComponent(Component component) {
        LinkedList<Component> currentList = new LinkedList<>();
        currentList.add(component);
        arrayList.add(currentList);
    }

    /**
     * Removes an element and its connections from the circuit.
     * @param component The component to be deleted.
     */
    public void deleteComponent(Component component) {
        LinkedList<Component> toBeDeleted = null;
        for(LinkedList<Component> list : arrayList) {
            if(list.getFirst().equals(component)) toBeDeleted = list;
            else list.remove(component);
        }
        arrayList.remove(toBeDeleted);
    }

    /**
     * Add an edge between two connected nodes within the circuit
     * @param src The index of the first element
     * @param dst The index of the second element
     */
    public void addEdge(int src, int dst) {
        LinkedList<Component> srcList = arrayList.get(src), dstList = arrayList.get(dst);
        Component dest = arrayList.get(dst).getFirst(),
                  source = arrayList.get(src).getFirst();
        if(!srcList.contains(dest)) srcList.add(dest);
        if(!dstList.contains(source)) dstList.add(source);
    }

    /**
     * Remove an edge between two elements of the circuit.
     * @param src The index of the first element
     * @param dst The index of the second element
     * @implNote There is no need to check if the edge exists before calling this method
     */
    public void removeEdge(int src, int dst) {
        LinkedList<Component> srcList = arrayList.get(src), dstList = arrayList.get(dst);
        Component dest = arrayList.get(dst).getFirst(),
                  source = arrayList.get(src).getFirst();
        srcList.remove(dest);
        dstList.remove(source);
    }

    /**
     * Checks if there exists an edge between two elements of the circuit
     * @param src The index of the first object
     * @param dst The index of the second object
     * @return true if there is an edge, false if there is not.
     */
    public boolean checkEdge(int src, int dst) {
        LinkedList<Component> currentList = arrayList.get(src);

        for (Component component : currentList) {
            if (arrayList.get(dst).contains(component)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Static method to initialize and call the recursive method {@link #visit(ArrayList, ArrayList)} to check for cycle in the given Circuit object.
     * @param circuit the circuit which is being checked for cycles.
     */
    public static void checkForCycle(Circuit circuit) {
        boolean temp = false;
        ArrayList<Component> visited = new ArrayList<>();
        ArrayList<Component> beingVisited = new ArrayList<>();

        beingVisited.add(circuit.arrayList.getFirst().getFirst());
        circuit.closed = (circuit.visit(beingVisited, visited) == 1);
    }

    /**
     * Recursive method that iterates through the graph data structure and checks for a cycle.
     * @param beingVisited The ArrayList of nodes that have been visited once but not explored.
     * @param visited The ArrayList of nodes that have been entirely explored.
     * @return '0' if no cycle was detected. '1' if a cycle was detected.
     */
    private int visit(ArrayList<Component> beingVisited, ArrayList<Component> visited) {
        /*
         * When visiting a node:
         * 1. Remove from beingVisited and add to visited
         * 2. Check if children belong in either list
         *      if NO: Add children to beingVisited
         *      if YES: visited = ignore, beingVisited = CYCLE
         * 3. Visit children and repeat
         */

        Component comp;
        try {
            comp = beingVisited.getFirst();
        }
        catch(Exception e) {
            System.out.println("NO CYCLE FOUND.");
            return 0;
        }

        if(comp != null) {
            beingVisited.remove(comp);
            visited.add(comp);

            ArrayList<Component> childrenToVisit = new ArrayList<>();
            for(Component child : arrayList.get(getIndex(comp))) {
                if(child != comp && child != null && !beingVisited.contains(child) && !visited.contains(child)) childrenToVisit.add(child);
                else if (beingVisited.contains(child)) {
                    System.out.println("CYCLE FOUND");
                    return 1;
                }
            }
            beingVisited.addAll(childrenToVisit);
            return visit(beingVisited,visited);
        }
        return -1; // A problem occurred
    }

    /**
     *
     * @return the state of the circuit (true = closed, false = open)
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Remove all elements of the circuit
     */
    public void clear() {
        arrayList.clear();
    }

    /**
     * Transforms the circuit into an ArrayList containing all the elements of the circuit.
     * @return an ArrayList of Component objects
     */
    public ArrayList<Component> toArrayList() {
        ArrayList<Component> al = new ArrayList<>();
        for(LinkedList<Component> currentList:arrayList) {
            al.add(currentList.getFirst());
        }
        return al;
    }

    /**
     * Prints an adjacency list of the elements of the circuit by iterating through the LinkedLists.
     */
    public void print() {
        System.out.println("--------------------- Start of Graph ---------------------");
        for(LinkedList<Component> currentList:arrayList) {
            for(Component component:currentList) {
                System.out.print(component+"("+getIndex(component)+")"+" -> ");
            }
            System.out.println();
        }
        System.out.println("---------------------- End of Graph ----------------------");
    }

    /**
     *
     * @param component the component to look for
     * @return The index of the component in the circuit's {@link #arrayList} property. Returns '-1' if it does not exist.
     */
    public int getIndex(Component component) {
        for(int i=0;i<arrayList.size();i++) {
            if(arrayList.get(i).getFirst()==component) {
                return i;
            }
        }
        return -1; // Component not found
    }


}