package edu.vanier.strawberries.Models;

import javafx.geometry.Point2D;

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
        component.updateEnd();
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

    public void connectEntireCircuit() {
        for(LinkedList<Component> list : arrayList) {
            attemptConnection(list.getFirst(),null);
        }
    }

    /**
     * Verify and confirm the connection/disconnection of circuit components given the positions of their nodes.
     * @param toCheck The component to connect
     * @param node The node whose position to check
     * @implSpec The initial call must have <b>null</b> as the node parameter.
     */
    public void attemptConnection(Component toCheck, Node node) {
        if(node==null) node = toCheck.begin;
        int srcIndex = getIndex(toCheck);
        Point2D checkPoint = node.getPosition();
        ArrayList<Integer> connectedComponents = new ArrayList<>(), disconnectedComponents = new ArrayList<>();

        // CHECK FOR CONNECTION
        for (LinkedList<Component> currentList : arrayList) {
            for (Component connectedComponent : currentList) {
                if(connectedComponent != toCheck) {
                    int dstIndex = getIndex(connectedComponent);

                    Point2D componentBegin = connectedComponent.begin.getPosition();
                    Point2D componentEnd = connectedComponent.end.getPosition();

                    if ((componentBegin.distance(checkPoint) <= 1) || (componentEnd.distance(checkPoint) <= 1)) {
                        if(!connectedComponents.contains(dstIndex)) connectedComponents.add(dstIndex);
                        if(!node.isConnected()) node.setConnected(true);
                    }
                }
            }
        }
        for (int compIndex : connectedComponents) addEdge(srcIndex, compIndex);

        // CHECK FOR DISCONNECTION
        Point2D checkBegin = toCheck.begin.getPosition(),
                checkEnd = toCheck.end.getPosition();
        for(Component connected : arrayList.get(srcIndex)) {
            if(connected != toCheck) {
                Point2D compBegin = connected.begin.getPosition(),
                        compEnd = connected.end.getPosition();
                if ((checkBegin.distance(compBegin) > 1 && checkBegin.distance(compEnd) > 1) && (checkEnd.distance(compBegin) > 1 && checkEnd.distance(compEnd) > 1)) {
                    if(getIndex(connected) != -1) removeEdge(srcIndex, getIndex(connected));
                }
            }
        }

        if(node == toCheck.begin) attemptConnection(toCheck, toCheck.end);
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

        System.out.println("added an edge between components "+src+" and "+dst);
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

        try {
            beingVisited.add(circuit.arrayList.getFirst().getFirst());
            circuit.closed = (circuit.visit(beingVisited, visited) == 1);
        }
        catch (NoSuchElementException e) {
            System.out.println("Circuit is empty... : "+circuit.arrayList);
        }
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
            return 0; // No cycle found
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