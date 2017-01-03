import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.SET;
// import edu.princeton.cs.algs4.Queue;

public class KdTree {
    private static final boolean O_VERTICAL    = true;
    private static final boolean O_HORIZONTAL  = false;
    private Node root;
    private int size;

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        public Node(Point2D p, RectHV r) { 
            this.p = p;
            this.rect = new RectHV(r.xmin(), r.ymin(), r.xmax(), r.ymax());
        }
    }

    public KdTree() { }

    public boolean isEmpty() { return size() == 0; }   
            
    public int size() { return size; }  

    /* 
    private int size(Node x) {
        if (x == null) return 0;
        else return 1 + size(x.lb) + size(x.rt);
    }
    */      
    public void insert(Point2D p) { 
        if (p == null)
            throw new NullPointerException("Expect a point to insert");
        root = insert(root, p, O_VERTICAL, new RectHV(0, 0, 1, 1));
        size++;
    }      

    private Node insert(Node x, Point2D p, boolean orientation, RectHV rect) {
        if (x == null) return new Node(p, rect);
        int cmp = compare(p, x.p, orientation);
        if (cmp < 0) 
            x.lb  = insert(x.lb, p, !orientation, createRect(rect, x.p, orientation, true));
        else if (cmp > 0) 
            x.rt = insert(x.rt, p, !orientation, createRect(rect, x.p, orientation, false));
        return x;
    }

    private RectHV createRect(RectHV r, Point2D p, boolean orientation, boolean lb) {
        if (orientation == O_VERTICAL) {
            if (lb) return new RectHV(r.xmin(), r.ymin(), p.x(), r.ymax());
            else    return new RectHV(p.x(), r.ymin(), r.xmax(), r.ymax());
        } else {
            if (lb) return new RectHV(r.xmin(), r.ymin(), r.xmax(), p.y());
            else    return new RectHV(r.xmin(), p.y(), r.xmax(), r.ymax());
        }
    }

    private int compare(Point2D a, Point2D b, boolean orientation) {
        if (a.equals(b))
            return 0;
        if (orientation == O_VERTICAL) { 
            if (a.x() < b.x()) return -1;
        } else {
            if (a.y() < b.y()) return -1;
        }
        return +1;
    }
    
    public boolean contains(Point2D p) { 
        if (p == null) throw new NullPointerException("Expect a point to check");
        return contains(root, p, O_VERTICAL); 
    }           

    private boolean contains(Node x, Point2D p, boolean orientation) {
        if (x == null) return false;
        int cmp = compare(p, x.p, orientation);
        if (cmp < 0) return contains(x.lb, p, !orientation);
        else if (cmp > 0) return contains(x.rt, p, !orientation);
        else return true;
    }

    public void draw() {
        StdDraw.clear();
        draw(root, new Point2D(0,0), O_VERTICAL, new RectHV(0, 0, 1, 1));
        // StdDraw.setPenRadius(0.004);
        // StdDraw.setPenColor(StdDraw.BLACK);
        // StdDraw.rectangle(0.5, 0.5, 0.5, 0.5);
    }

    private void draw(Node x, Point2D p, boolean orientation, RectHV r) {
        if (x == null) return;
        drawPointLine(x, p, orientation, r);
        if (x.lb != null) draw(x.lb, x.p, !orientation, x.rect);
        if (x.rt != null) draw(x.rt, x.p, !orientation, x.rect);
    }

    private void drawPointLine(Node x, Point2D p, boolean orientation, RectHV r) {
        StdDraw.setPenRadius(0.015);
        StdDraw.setPenColor(StdDraw.BLACK);
        x.p.draw();
        StdDraw.setPenRadius(0.004);
        if (orientation == O_HORIZONTAL) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(p.x(), r.ymin(), p.x(), r.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(r.xmin(), p.y(), r.xmax(), p.y());
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        SET<Point2D> in;

        if (rect == null)
            throw new NullPointerException("Expect a rectangle to check");
        in = new SET<Point2D>();
        range(root, rect, in);

        return in;
    }

    private void range(Node x, RectHV r, SET<Point2D> s) {
        if (x == null) return;
        if (r.contains(x.p)) s.add(x.p);
        if (x.lb != null && r.intersects(x.lb.rect)) range(x.lb, r, s);
        if (x.rt != null && r.intersects(x.rt.rect)) range(x.rt, r, s);
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new NullPointerException("Expect a point to check");
        if (isEmpty()) return null;
        return nearest(root, p, null, Double.POSITIVE_INFINITY);
    }

    private Point2D nearest(Node x, Point2D p, Point2D champ, double min) {
        if (x == null) return champ;
        if (p.distanceSquaredTo(x.p) < min) {
            min = p.distanceSquaredTo(x.p);
            champ = x.p;
        }
        if (x.lb != null && (x.lb.rect.contains(p) || x.lb.rect.distanceSquaredTo(p) < min)) {
            champ = nearest(x.lb, p, champ, min);
            min = p.distanceSquaredTo(champ);
        }
        if (x.rt != null && (x.rt.rect.contains(p) || x.rt.rect.distanceSquaredTo(p) < min)) 
            champ = nearest(x.rt, p, champ, min);
        return champ;
    }

    // unit testing of the methods (optional) 
    public static void main(String[] args) {
        KdTree world =  new KdTree();

        world.insert(new Point2D(0.4 ,0.3));
        world.insert(new Point2D(0.2, 0.3));
        world.insert(new Point2D(0.1, 0.5));
        world.draw();

        System.out.println("Empty: " + world.isEmpty());
        System.out.println("Size: " + world.size());

        System.out.println("Contains 0,0: " + world.contains(new Point2D(0, 0)));
        System.out.println("Contains 0.4,0.3: " + world.contains(new Point2D(0.4, 0.3)));
        /* System.out.println("Nearest to 1,2: " + world.nearest(new Point2D(1, 2)));
        
        RectHV rect = new RectHV(0.5, -1, 3, 1);
        rect.draw();
        System.out.println("Inside (0.5,-1) (3,1): ");
        for (Point2D p : world.range(rect))
             System.out.println(p);*/
    }                 
}