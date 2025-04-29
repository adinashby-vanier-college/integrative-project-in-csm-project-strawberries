package edu.vanier.strawberries.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The Circuit is based on a Graph data structure
 */
public class Circuit {
    boolean closed;
    public ArrayList<LinkedList<Component>> arrayList;
    private boolean diagramView;

    public Circuit(boolean diagramView) {
        closed = false;
        arrayList = new ArrayList<>();
        this.diagramView = diagramView;
    }

    public void addComponent(Component component) {
        LinkedList<Component> currentList = new LinkedList<>();
        currentList.add(component);
        arrayList.add(currentList);
    }

    public void deleteComponent(Component component) {
        LinkedList<Component> toBeDeleted = null;
        for(LinkedList<Component> list : arrayList) {
            if(list.getFirst().equals(component)) toBeDeleted = list;
            else list.remove(component);
        }
        arrayList.remove(toBeDeleted);
    }

    public void addEdge(int src, int dst) {
        LinkedList<Component> srcList = arrayList.get(src), dstList = arrayList.get(dst);
        Component dest = arrayList.get(dst).getFirst(),
                  source = arrayList.get(src).getFirst();
        if(!srcList.contains(dest)) srcList.add(dest);
        if(!dstList.contains(source)) dstList.add(source);
    }

    public void removeEdge(int src, int dst) {
        LinkedList<Component> srcList = arrayList.get(src), dstList = arrayList.get(dst);
        Component dest = arrayList.get(dst).getFirst(),
                  source = arrayList.get(src).getFirst();
        srcList.remove(dest);
        dstList.remove(source);
    }

    public boolean checkEdge(int src, int dst) {
        LinkedList<Component> currentList = arrayList.get(src);

        for (Component component : currentList) {
            if (arrayList.get(dst).contains(component)) {
                return true;
            }
        }
        return false;
    }

    public void checkForCycle() {
        boolean temp = false;
        ArrayList<Component> visited = new ArrayList<>();
        ArrayList<Component> beingVisited = new ArrayList<>();

        beingVisited.add(arrayList.getFirst().getFirst());
//        System.out.println("before checking: "+arrayList.getFirst().getFirst());
        if(visit(beingVisited, visited)==1) closed = true;
        else closed = false;
    }

    private int visit(ArrayList<Component> beingVisited, ArrayList<Component> visited) {
        /**
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
//            System.out.println("component to visit: "+comp);
        }
        catch(Exception e) {
//            System.out.println("empty array");
//            System.out.println("array 1: "+beingVisited + "\tarray 2: "+visited);
            System.out.println("NO CYCLE FOUND.");
            System.out.println("-------------------------------------------------------");
            return 0;
        }

        if(comp != null) {
            beingVisited.remove(comp);
            visited.add(comp);

            ArrayList<Component> childrenToVisit = new ArrayList<>();
            for(Component child : arrayList.get(getIndex(comp))) {
                if(child != comp && child != null && !beingVisited.contains(child) && !visited.contains(child)) childrenToVisit.add(child);
                else if (beingVisited.contains(child)) {
//                    System.out.println("Child already being visited: " + child);
                    System.out.println("CYCLE FOUND !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    return 1;
                }
            }
            beingVisited.addAll(childrenToVisit);
//            System.out.println("beingVisited after adding children: "+ beingVisited);
            return visit(beingVisited,visited);
        }
        return -1; // A problem occurred
    }

    public boolean isClosed() {
        return closed;
    }

    public void clear() {
        arrayList.clear();
    }

    public ArrayList<Component> toArrayList() {
        ArrayList<Component> al = new ArrayList<>();
        for(LinkedList<Component> currentList:arrayList) {
            al.addAll(currentList);
        }
        return al;
    }

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

    public int getIndex(Component component) {
        for(int i=0;i<arrayList.size();i++) {
            if(arrayList.get(i).getFirst()==component) {
                return i;
            }
        }
        return -1; // Not found
    }

    public void setDiagramView(boolean diagramView) {
        this.diagramView = diagramView;
        for(LinkedList<Component> compList : arrayList) compList.getFirst().switchDisplay(diagramView);
    }

}