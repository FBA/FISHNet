/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SppNavService;

/**
 *
 * @author root
 */
public class Tree {

    private String name;
    private int startPoint;

    public Tree(){
        setName("unknown");
        setStartPoint(-1);
    }
    public Tree(String name, int startPoint){
        setName(name);
        setStartPoint(startPoint);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint = startPoint;
    }
}
