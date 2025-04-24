package edu.vanier.strawberries.Models;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The Circuit is based on a Graph data structure
 */
public class Circuit {
    boolean closed;
    public ArrayList<LinkedList<Component>> arrayList;

    public Circuit() {
        closed = false;
        arrayList = new ArrayList<>();
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
        LinkedList<Component> currentList = arrayList.get(src);
        Component dest = arrayList.get(dst).getFirst();
        currentList.add(dest);
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
        //TODO : Verify connections!!!!! ** Not printing correctly :(
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

}