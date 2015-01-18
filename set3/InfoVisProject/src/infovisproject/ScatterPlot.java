package infovisproject;

import javax.swing.BorderFactory;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.DataShapeAction;
import prefuse.action.layout.AxisLayout;
import prefuse.controls.ToolTipControl;
import prefuse.data.Table;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.VisiblePredicate;

/**
 * A simple visualization that can show three attributes 
 * of data items at the same time, in the form of markers
 * on a scatter plot:
 * - Numeric attribute to the marker X-coordinate;
 * - Numeric attribute to the marker Y-coordinate;
 * - Nominal attribute to the marker shape.
 *
 * This code is derived from the Prefuse demo files,
 * which are authored by Jeffrey Heer.
 */
public class ScatterPlot extends Display {

    public static final String GROUP = "data";
    // Renders markers as attribute-derived shapes
    // with a base size of 10 pixels.
    private ShapeRenderer shapeRenderer = new ShapeRenderer(10);

    /**
     * 
     */
    public ScatterPlot(
            Table table,
            String xField,
            String yField) {
        super(new Visualization());

        // Setup the visualized data.
        m_vis.addTable(GROUP, table);

        DefaultRendererFactory rf = new DefaultRendererFactory(shapeRenderer);
        m_vis.setRendererFactory(rf);

        // Create actions to process the visual data.
        AxisLayout xAxis = new AxisLayout(
                GROUP,
                xField,
                Constants.X_AXIS,
                VisiblePredicate.TRUE);
        m_vis.putAction("x", xAxis);

        AxisLayout yAxis = new AxisLayout(
                GROUP,
                yField,
                Constants.Y_AXIS,
                VisiblePredicate.TRUE);
        m_vis.putAction("y", yAxis);

//        ColorAction color = new ColorAction(
//                GROUP,
//                VisualItem.STROKECOLOR,
//                ColorLib.rgb(100, 100, 255));
        int[] palette = new int[]{
            ColorLib.rgb(255, 100, 100), ColorLib.rgb(100, 255, 100)
        };
        ColorAction color = new ColorAction(
                GROUP,
                VisualItem.STROKECOLOR,
                ColorLib.rgb(100, 100, 255));
        m_vis.putAction("color", color);

//        DataShapeAction shape = new DataShapeAction(GROUP, xField);
//        m_vis.putAction("shape", shape);

        ActionList draw = new ActionList();
        draw.add(xAxis);
        draw.add(yAxis);
        if (xField != null) {
//            draw.add(shape);
        }
        draw.add(color);
        draw.add(new RepaintAction());
        m_vis.putAction("draw", draw);

        // Set up a display and UI components to show the visualization.
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setSize(700, 450);

        // Show x- and y-coordinates as a marker tooltip.
        ToolTipControl tooltip = new ToolTipControl(
                new String[]{xField, yField});
        addControlListener(tooltip);

        // Run visualization.
        setHighQuality(true);
        m_vis.run("draw");
    }
}