package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;


/**
 * @author ateam120
 *
 */
public class SocialNetwork implements SocialNetworkADT {

    private Graph graph;
    private List<String> tempStore;
    private Queue<Person> queue;

    // constructor
    public SocialNetwork() {

        graph = new Graph();
        recordOperations = new ArrayList<String>();

    }
//record every operation made by user
    private ArrayList<String> recordOperations;

    @Override
    public boolean addFriends(String person1, String person2) {

        Person Person1 = new Person(person1);
        Person Person2 = new Person(person2);

        graph.addEdge(Person1, Person2);
        recordOperations.add("a" + person1 + person2);

        return true;
    }


    @Override
    public boolean removeFriends(String person1, String person2) {

        Person Person1 = new Person(person1);
        Person Person2 = new Person(person2);

        if (!graph.getAdjacentVerticesOf(Person1).contains(Person2)) {
            return false;
        }
        graph.removeEdge(Person1, Person2);
        recordOperations.add("r" + person1 + person2);
        return true;
    }

    @Override
    public boolean addUser(String person) {

        Person Person = new Person(person);

        graph.addVertex(Person);
        recordOperations.add("a " + person);
        return true;
    }

    @Override
    public boolean removeUser(String person) {

        if (!graph.getAllVertices().contains(person)) {
            return false;
        }
        Person Person = new Person(person);

        graph.removeVertex(Person);
        recordOperations.add("r " + person);
        return true;
    }

    @Override
    public Set<Person> getFriends(String person) {

        Person Person = new Person(person);

        List<Person> friendsList = graph.getAdjacentVerticesOf(Person);
        Set<Person> friendSet = new HashSet<Person>();

        for (Person friend : friendsList) {
            friendSet.add(friend);
        }
        return friendSet;
    }

    @Override
    public Set<Person> getMutualFriends(String person1, String person2) {

        Person Person1 = new Person(person1);
        Person Person2 = new Person(person2);

        List<Person> friend1 = graph.getAdjacentVerticesOf(Person1);
        List<Person> friend2 = graph.getAdjacentVerticesOf(Person2);

        Set<Person> mFriend = new HashSet<Person>();

        for (int i = 0; i < friend1.size(); i++) {
            for (int j = 0; j < friend2.size(); j++) {

                if (friend1.get(i).equals(friend2.get(j))) {
                    mFriend.add(friend1.get(i));
                }
            }
        }
        return mFriend;
    }

    @Override
    public List<String> getShortestPath(String person1, String person2) {

        Person Person1 = new Person(person1);
        Person Person2 = new Person(person2);

        try {
            tempStore = findPathAlgorithm(Person1, Person2);
        } catch (Exception e) {
            
            e.printStackTrace();
        }
        return tempStore;
    }

    
    /**
     * the Dijkstra's Algorithm to find shortest path 
     * @param person1
     * @param person2
     * @return shortest path
     * @throws Exception 
     */
    public List<String> findPathAlgorithm(Person person1, Person person2) throws Exception {

        if(person1 == null||person2 == null) {
            throw new NullPointerException("no input,please input person name");
        }
        else {
        
        tempStore = new ArrayList<String>(); // initializes list to store shortest path
        Person node = person1; // stores person1 to start
        person1.totalWeight = 0; // sets start total weight to 0

        queue = new LinkedList<Person>(); // initializes queue
        queue.add(person1); // adds person1 to queue

        // checks that person2 exists in graph, otherwise throws exception
        List<Person> vertexList = graph.getVertexList();
        if (!vertexList.contains(person2))
            throw new Exception(person2 + " not exist");

        while (!queue.isEmpty()) {
            node = queue.remove(); // remove first item from queue
            node.visited = true; // item has now been visited

            // if the person2 has been reached break from loop
            if (node.getName().equals(person2.getName())) {
                break;
                }
            dhelper(node); // call helper
        }

        node = person2;

        // copies predecessors of node into list
        while (node.predecessor != null) {
            tempStore.add(node.getName());
            node = node.predecessor;
        }
        tempStore.add(person1.getName()); // adds person1 to store
        Collections.reverse(tempStore); // reverses list so it appears starting at person1, ending at person2

        // if store doesn't contain person2 throw exception
        if (!tempStore.contains(person2.getName()))
            throw new Exception("Can not find path.");
        return tempStore;
        }
    }

    /**
     * Helper method for dijkstra's algorithm
     * 
     * @param person1
     */
    private void dhelper(Person person1) {

        Person node = null;
        int edgeDistance = -1;
        int newDistance = -1;

        // iterates through list of friends for given person
        for (int i = 0; i < person1.getListOfUsersFriends().size(); i++) {
            node = person1.getListOfUsersFriends().get(i);

            // if person has not been visited yet, check if total weight can be reduced
            if (!node.visited) {
                edgeDistance = node.weight;
                newDistance = person1.totalWeight + edgeDistance;

                // if weight can be reduced do it and add person to queue
                if (newDistance < node.totalWeight) {
                    node.totalWeight = newDistance;
                    node.predecessor = person1;
                    queue.add(node);
                }
            }
        }
    }

    @Override
    public Set<Graph> getConnectedComponents() {
        return null;
        // TODO Auto-generated method stub

    }

    @Override
    public boolean setCentral(String person) {

        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void loadFromFile(File file) {
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] args = line.split(" ");
                if (args.length == 2) {
                    switch (args[0]) {
                        case "a":
                            addUser(args[1]);
                        case "r":
                            removeUser(args[1]);
                        case "s":
                            setCentral(args[1]);
                    }
                } else if (args.length == 3) {
                    switch (args[0]) {
                        case "a":
                            addFriends(args[1], args[2]);
                        case "r":
                            removeFriends(args[1], args[2]);
                    }
                } else {
                    // TODO exception
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void saveToFile(File file) throws IOException {
        try {
            FileWriter fileWriter = new FileWriter(file);
            for (int i = 0; i < recordOperations.size(); i++) {
                String log = recordOperations.get(i);
                fileWriter.write(log);
                fileWriter.write("\n");
            }
        }

        catch (Exception e) {
            System.out.println("Caution: IOException!");
        }

    }

}
