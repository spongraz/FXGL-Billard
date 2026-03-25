package at.htl.billard;

import java.util.ArrayList;
import java.util.List;

public class TableLayout {

    private static final double TABLE_WIDTH  = 0.8;
    private static final double TABLE_HEIGHT = 0.7;
    private static final double POCKET_RADIUS = 22;

    private final double tableX, tableY, tableW, tableH;

    private TableLayout(double tableX, double tableY, double tableW, double tableH) {
        this.tableX = tableX;
        this.tableY = tableY;
        this.tableW = tableW;
        this.tableH = tableH;
    }

    public static TableLayout fromAppSize(double appW, double appH) {
        double tableW = appW * TABLE_WIDTH;
        double tableH = appH * TABLE_HEIGHT;
        double tableX = (appW - tableW) / 2.0;
        double tableY = (appH - tableH) / 2.0;
        return new TableLayout(tableX, tableY, tableW, tableH);
    }

    public double getTableX()      { return tableX; }
    public double getTableY()      { return tableY; }
    public double getTableW()      { return tableW; }
    public double getTableH()      { return tableH; }
    public double getPocketRadius(){ return POCKET_RADIUS; }


     // Gibt die Eck-Koordinaten als Polygon-Punkte zurück.
     // Eckpockets: rechtwinkliges Dreieck
     // Mittelpockets: Trapez

    public double[][] getPocketCenters() {
        double x0 = tableX, x1 = tableX + tableW;
        double y0 = tableY, y1 = tableY + tableH;
        double midX = tableX + tableW / 2.0;
        double r = POCKET_RADIUS;

        return new double[][]{
                { x0 + 2 * r, y0 + 2*r },   // oben-links
                { x1,   y0 + 2*r },   // oben-rechts
                { x0 + 2 * r,   y1 },   // unten-links
                { x1,   y1 },   // unten-rechts
                { midX + r, y0 + 2 *r },   // mitte-oben
                { midX + r, y1 },   // mitte-unten
        };
    }
    public List<CushionData> getCushions() {
        List<CushionData> cushions = new ArrayList<>();
        double r   = POCKET_RADIUS;
        double t   = 25;           // Bandendicke
        double stroke = 15;   // 30 / 2
        double d  = r * 1.3 ; //  Abstand der Bandenden von der Ecke
        double mid = r * 0.5; //  Mittelpocket-Öffnung

        double x0 = tableX + stroke,          x1 = tableX + tableW - stroke;
        double y0 = tableY + stroke,          y1 = tableY + tableH - stroke;
        double midX = tableX + tableW / 2.0;

        // ─── Obere Bande links ───
        cushions.add(new CushionData(new double[]{
                x0 + d,          y0,
                midX - mid,      y0,
                midX - mid - t,  y0 + t,
                x0 + d + t,      y0 + t
        }));

// ─── Obere Bande rechts ───
        cushions.add(new CushionData(new double[]{
                midX + mid,      y0,
                x1 - d,          y0,
                x1 - d - t,      y0 + t,
                midX + mid + t,  y0 + t
        }));

// ─── Untere Bande links ───
        cushions.add(new CushionData(new double[]{
                x0 + d,          y1,
                midX - mid,      y1,
                midX - mid - t,  y1 - t,
                x0 + d + t,      y1 - t
        }));

// ─── Untere Bande rechts ───
        cushions.add(new CushionData(new double[]{
                midX + mid,      y1,
                x1 - d,          y1,
                x1 - d - t,      y1 - t,
                midX + mid + t,  y1 - t
        }));

        // ─── Linke Bande ───
        cushions.add(new CushionData(new double[]{
                x0,      y0 + d,
                x0 + t,  y0 + d + t,
                x0 + t,  y1 - d - t,
                x0,      y1 - d
        }));

        // ─── Rechte Bande ───
        cushions.add(new CushionData(new double[]{
                x1,      y0 + d,
                x1 - t,  y0 + d + t,
                x1 - t,  y1 - d - t,
                x1,      y1 - d
        }));

        return cushions;
    }

    public static class CushionData {
        public final double[] points;
        public CushionData(double[] points) { this.points = points; }
    }
}