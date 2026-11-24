package com.aerocast.ui.util;

import java.util.List;
import java.util.Locale;

/**
 * High-performance pure-Java cubic Catmull-Rom to Bézier spline interpolator
 * for silky-smooth meteorological trend curve rendering in SVG.
 */
public final class SplineCurveGenerator {

    private SplineCurveGenerator() {}

    public record Point(double x, double y) {}

    /**
     * Converts a discrete set of 2D data points into a smooth cubic Bézier SVG path.
     */
    public static String generateSmoothPath(List<Point> points) {
        if (points == null || points.isEmpty()) {
            return "";
        }
        if (points.size() == 1) {
            return String.format(Locale.US, "M %.2f,%.2f", points.get(0).x(), points.get(0).y());
        }

        StringBuilder path = new StringBuilder();
        path.append(String.format(Locale.US, "M %.2f,%.2f ", points.get(0).x(), points.get(0).y()));

        int n = points.size();
        for (int i = 0; i < n - 1; i++) {
            Point p0 = (i > 0) ? points.get(i - 1) : points.get(i);
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            Point p3 = (i + 2 < n) ? points.get(i + 2) : p2;

            // Catmull-Rom to Cubic Bézier control points
            double cp1x = p1.x() + (p2.x() - p0.x()) / 6.0;
            double cp1y = p1.y() + (p2.y() - p0.y()) / 6.0;
            double cp2x = p2.x() - (p3.x() - p1.x()) / 6.0;
            double cp2y = p2.y() - (p3.y() - p1.y()) / 6.0;

            path.append(String.format(Locale.US, "C %.2f,%.2f %.2f,%.2f %.2f,%.2f ",
                    cp1x, cp1y, cp2x, cp2y, p2.x(), p2.y()));
        }

        return path.toString();
    }
}
