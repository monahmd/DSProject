import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

class Point {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
}

class Zone {

    private int x1;
    private int y1;
    private int x2;
    private int y2;

    public Zone(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX1(){
        return this.x1;
    }
    public int getY1(){
        return this.y1;
    }
    public int getX2(){
        return this.x2;
    }
    public int getY2(){
        return this.y2;
    }

    public boolean InAreaPoint(Point point) {

        return getX2() <= point.getX() && point.getX() <= getX1() && getY2() <= point.getY() && point.getY() <= getY1();
    }
    public Zone areaSegmentation(int sectionIndex) {

        int width = (getX1() - getX2()) / 2;
        int height = (getY1() - getY2()) / 2;

        /*** NE=0, NW=1, SW=2, SE=3 ***/
        switch (sectionIndex) {
            case 0:
                return new Zone(x1, y1, x2 + width, y2 + height);
            case 1:
                return new Zone(x1 - width, y1, x2, y2 + height);
            case 2:
                return new Zone(x1 - width, y1 - height, x2, y2);
            case 3:
                return new Zone(x1, y1 - height, x2 + width, y2);
            default:
                return null;
        }
    }
    public boolean doesOverlap(Zone zone) {
        if (zone.getX2() > this.getX1()) {
            return false;
        }
        if (zone.getX1() < this.getX2()) {
            return false;
        }
        if (zone.getY1() < this.getY2()) {
            return false;
        }
        if (zone.getY2() > this.getY1()) {
            return false;
        }
        return true;
    }
}

class QuadTree {

    //private static final int MAX_POINTS = 4;
    private Zone area;
    private int size = 0;
    private List<Point> points = new ArrayList<>();
    private List<QuadTree> quadTrees = new ArrayList<>();

    public QuadTree(Zone area) {
        this.area = area;
    }

    private void createQuadrants() {
        Zone zone;
        for (int i = 0; i < 4; i++) {
            zone = this.area.areaSegmentation(i);
            quadTrees.add(new QuadTree(zone));
            for (int j = 0;  j < points.size(); j++){
                if (quadTrees.get(i).area.InAreaPoint(points.get(j))){
                    quadTrees.get(i).points.add(points.get(j));
                }
            }
        }
        points = new ArrayList<>();
    }

    private boolean addPointToOneQuadrant(Point point) {
        boolean pointAdded;
        for (int i = 0; i < 4; i++) {
            pointAdded = this.quadTrees.get(i).addPoint(point);
            if (pointAdded)
                return true;
        }
        return false;
    }

    public boolean addPoint(Point point) {

        if (this.area.InAreaPoint(point)){
            if (this.size == 0) {
                this.points.add(point);
                size++;
                return true;
            }
            else {
                if (this.quadTrees.size() == 0) {
                    this.createQuadrants();
                }
                return addPointToOneQuadrant(point);
            }
        }
        return false;
    }
    public boolean searchPoint(Point point){
        if (quadTrees.size() != 0){
            boolean result = false;
            for (int i = 0; i < quadTrees.size(); i++) {
                if (this.area.InAreaPoint(point)) {
                    for (int j = 0; j < points.size(); j++) {
                        if (points.get(j).equals(point)) {
                            return true;
                        }
                    }
                    result |= quadTrees.get(i).searchPoint(point);
                }
            }
            return result;
        }
        else {
            if (this.area.InAreaPoint(point)) {
                for (int j = 0; j < points.size(); j++) {
                    if (points.get(j).equals(point)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }
    public int searchArea(int x1, int y1, int x2, int y2){
        Zone givenZone = new Zone(x1, y1, x2, y2);
        int count = 0;

        for (int j =0; j < points.size(); j++){
            if (givenZone.InAreaPoint(points.get(j))){
                count++;
            }
        }


        for (int i = 0; i < quadTrees.size(); i++){
            if (quadTrees.get(i).area.doesOverlap(givenZone)){
                count += quadTrees.get(i).searchArea(x1, y1, x2, y2);
            }
        }
        return count;
    }
}

public class Quad {

    public static void main(String[] args) {
        Zone mainZone = new Zone(100000, 100000, -100000, -100000);
        QuadTree mainQuad = new QuadTree(mainZone);
        Scanner input = new Scanner(System.in);
        while (input.hasNextLine()){
            String order = input.nextLine();
            String[] parts = order.split(" ");
            switch (parts[0]){
                case "Insert":
                    int addX = Integer.parseInt(parts[1]);
                    int addY = Integer.parseInt(parts[2]);
                    Point addPoint = new Point(addX,addY);
                    mainQuad.addPoint(addPoint);
                    break;
                case "Search":
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    Point searchPoint = new Point(x,y);
                    if (mainQuad.searchPoint(searchPoint)){
                        System.out.println("TRUE");
                    }
                    else {
                        System.out.println("FALSE");
                    }
                    break;
                case "Area":
                    int x1 = Integer.parseInt(parts[1]);
                    int y1 = Integer.parseInt(parts[2]);
                    int x2 = Integer.parseInt(parts[3]);
                    int y2 = Integer.parseInt(parts[4]);
                    System.out.println(mainQuad.searchArea(x2,y2,x1,y1));
                    break;
            }
        }
    }

}
