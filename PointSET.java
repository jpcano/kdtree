import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.SET;

public class PointSET {
    private SET<Point2D> points;

    // construct an empty set of points     
    public PointSET() { points = new SET<Point2D>(); }

    // is the set empty? 
    public boolean isEmpty() { return points.isEmpty(); }   

    // number of points in the set               
    public int size() { return points.size(); }  

    // add the point to the set (if it is not already in the set)                     
    public void insert(Point2D p) { 
        if (p == null)
            throw new NullPointerException("Expect a point to insert");
        points.add(p); 
    }      

    // does the set contain point p? 
    public boolean contains(Point2D p) { 
        if (p == null)
            throw new NullPointerException("Expect a point to check");
        return points.contains(p); }           

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : points)
            p.draw();
    }                        

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        SET<Point2D> in;

        if (rect == null)
            throw new NullPointerException("Expect a rectangle to check");
        in = new SET<Point2D>();
        for (Point2D p : points)
            if (rect.contains(p))
                in.add(p);

        return in;
    }  

    // a nearest neighbor in the set to point p; null if the set is empty 
    public Point2D nearest(Point2D p) {
        Point2D champion = null;
        double min = Double.POSITIVE_INFINITY;

        if (p == null)
            throw new NullPointerException("Expect a point to check");

        if (points.isEmpty())
            return null;
        
        for (Point2D point : points) {
            if (p.distanceTo(point) < min) {
                min = p.distanceTo(point);
                //champion = new Point2D(point.x(), point.y());
                champion = point;
            }
        }

        return champion;
    }         

    // unit testing of the methods (optional) 
    public static void main(String[] args) {
        PointSET world =  new PointSET();
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius(.01);
        StdDraw.setScale(-2, 4);

        world.insert(new Point2D(0,0));
        world.insert(new Point2D(0,0));
        world.insert(new Point2D(1,0));
        world.insert(new Point2D(2,0));
        world.insert(new Point2D(0,1));
        world.insert(new Point2D(0,2));
        world.draw();

        System.out.println("Empty: " + world.isEmpty());
        System.out.println("Size: " + world.size());

        System.out.println("Contains 0,0: " + world.contains(new Point2D(0, 0)));
        System.out.println("Contains 3,0: " + world.contains(new Point2D(3, 0)));
        System.out.println("Nearest to 1,2: " + world.nearest(new Point2D(1, 2)));
        
        RectHV rect = new RectHV(0.5, -1, 3, 1);
        rect.draw();
        System.out.println("Inside (0.5,-1) (3,1): ");
        for (Point2D p : world.range(rect))
             System.out.println(p);
    }                 
}