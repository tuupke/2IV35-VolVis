package infovisproject;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import prefuse.Visualization;
import prefuse.action.layout.AxisLayout;
import prefuse.data.Table;
import prefuse.data.io.DelimitedTextTableReader;

/**
 * Creates an interactive scatter plot for a CSV file that contains multivariate
 * data.
 */
public class InfoVisProject {

    static JFrame frame;

    public static void main(String[] argv) {
        // Load multivariate data as a table from a CSV file.
        String data = "../set3_data/wine/wine_white.csv";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("../"));

        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                data = fileChooser.getSelectedFile().getAbsolutePath();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(-1);
            }

            Table input = null;
            try {
                input = new DelimitedTextTableReader(",").readTable(data);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }

            final Table table = input;

            table.addColumn("sulfur dioxide", "[free sulfur dioxide] / [total sulfur dioxide]");

//        for (int i = 0; i < table.getRowCount(); i++) {
//            Tuple t = table.getTuple(i);
//            if(t.get("quality").equals(9)){
//                System.out.println(t.get("sulfur dioxide"));
//            }
//        }





            frame = new JFrame("InfoVisProject");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



            int spacing = 10;
            final JToolBar toolbar = new JToolBar();
            toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
            toolbar.add(Box.createHorizontalStrut(spacing));
            final JComboBox cb = new JComboBox(new String[]{"Scatter plot", "Interval Tree", "Median Tree"});
            cb.setSelectedItem("Scatter plot");
            cb.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(toolbar, BorderLayout.NORTH);
                    if (((String) cb.getSelectedItem()).equals("Interval Tree")) {
                        IntTree(table);
                    } else if (((String) cb.getSelectedItem()).equals("Median Tree")) {
                        MedT(table);
                    } else {
                        scat(table);
                    }
                    frame.pack();
                }
            });

            toolbar.add(cb);

            toolbar.add(Box.createHorizontalStrut(2 * spacing));
            final JButton but = new JButton("Print");
            but.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        BufferedImage image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics2D graphics2D = image.createGraphics();
                        frame.paint(graphics2D);
                        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = new Date();
                        ImageIO.write(image, "png", new File("../" + dateFormat.format(date) + "plot.png"));
                        setClipboardContents( dateFormat.format(date) + "plot.png");
                    } catch (Exception exception) {
                        //code
                    }
                }
            });
            toolbar.add(but);

            frame.getContentPane().add(toolbar, BorderLayout.NORTH);

            frame.pack();
            frame.setVisible(true);


        }

    }

    static public void setClipboardContents(String aString) {
        StringSelection selection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    static void scat(Table table) {
        // Encode three fields from the table as a scatter plot.
        String xField = "quality";
        String yField = "sulfur dioxide";
        ScatterPlot scatterPlot = new ScatterPlot(table, xField, yField);
        JToolBar toolbar = getEncodingToolbar(scatterPlot, xField, yField);
        frame.getContentPane().add(toolbar, BorderLayout.SOUTH);
        frame.getContentPane().add(scatterPlot, BorderLayout.CENTER);
    }

    static void MedT(Table table) {
        MedTreePlot tr = new MedTreePlot(table);
        frame.getContentPane().add(tr, BorderLayout.CENTER);
    }

    static void IntTree(Table table) {
        int ans = -1;
        try {
            ans = Integer.parseInt(JOptionPane.showInputDialog(frame, "How many intervals do you want?"));
        } catch (Exception e) {
            return;
        }
        if (ans < 1) {
            return;
        }
        TreePlot tr = new TreePlot(table, ans);
        frame.getContentPane().add(tr, BorderLayout.CENTER);
    }

    /**
     * Create top tool bar that allows the mapping of attributes, to marker
     * coordinates and shape.
     */
    private static JToolBar getEncodingToolbar(
            final ScatterPlot sp,
            final String xfield,
            final String yfield) {
        int spacing = 10;

        // create list of column names
        final Table t = (Table) sp.getVisualization().getSourceData(ScatterPlot.GROUP);
        String[] colnames = new String[t.getColumnCount()];

        for (int i = 0; i < colnames.length; ++i) {

            colnames[i] = t.getColumnName(i);

        }

        // create toolbar that allows visual mappings to be changed
        JToolBar toolbar = new JToolBar();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
        toolbar.add(Box.createHorizontalStrut(spacing));

        final JComboBox xcb = new JComboBox(colnames);
        xcb.setSelectedItem(xfield);
        xcb.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Visualization vis = sp.getVisualization();
                AxisLayout xaxis = (AxisLayout) vis.getAction("x");
                xaxis.setDataField((String) xcb.getSelectedItem());
                vis.run("draw");
            }
        });
        toolbar.add(new JLabel("X: "));
        toolbar.add(xcb);
        toolbar.add(Box.createHorizontalStrut(2 * spacing));

        final JComboBox ycb = new JComboBox(colnames);
        ycb.setSelectedItem(yfield);
        ycb.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Visualization vis = sp.getVisualization();
                AxisLayout yaxis = (AxisLayout) vis.getAction("y");
                yaxis.setDataField((String) ycb.getSelectedItem());
                vis.run("draw");
            }
        });
        toolbar.add(new JLabel("Y: "));
        toolbar.add(ycb);



        toolbar.add(Box.createHorizontalStrut(spacing));
        toolbar.add(Box.createHorizontalGlue());

        return toolbar;
    }
}
