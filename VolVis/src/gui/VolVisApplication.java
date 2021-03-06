/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import volume.Volume;
import volvis.CompRenderer;
import volvis.MIPRenderer;
import volvis.MIPRendererhi;
import volvis.OpacityRenderer;
import volvis.RaycastRenderer;
import volvis.Renderer;
import volvis.TFRenderer;
import volvis.Visualization;

/**
 *
 * @author michel
 */
public class VolVisApplication extends javax.swing.JFrame {

    Visualization visualization;
    Volume volume;
    RaycastRenderer raycastRenderer;
    MIPRenderer MIPRenderer;
    MIPRendererhi MIPRendererhi;
    TFRenderer TFRenderer;
    CompRenderer compRenderer;
    OpacityRenderer opRenderer;

    /**
     * Creates new form VolVisApplication
     */
    public VolVisApplication() {
        initComponents();
        this.setTitle("Volume visualization");

        // Create a new visualization for the OpenGL panel
        GLJPanel glPanel = new GLJPanel();
        renderPanel.setLayout(new BorderLayout());
        renderPanel.add(glPanel, BorderLayout.CENTER);
        visualization = new Visualization(glPanel);
        glPanel.addGLEventListener(visualization);

        raycastRenderer = new RaycastRenderer();
        visualization.addRenderer(raycastRenderer);
        raycastRenderer.addTFChangeListener(visualization);
        tabbedPanel.addTab("Raycaster", raycastRenderer.getPanel());

        MIPRenderer = new MIPRenderer(visualization);
        visualization.addRenderer(MIPRenderer);
        MIPRenderer.addTFChangeListener(visualization);
        tabbedPanel.addTab("MIP", MIPRenderer.getPanel());

        MIPRendererhi = new MIPRendererhi(visualization);
        visualization.addRenderer(MIPRendererhi);
        MIPRendererhi.addTFChangeListener(visualization);
        tabbedPanel.addTab("MIPhi", MIPRendererhi.getPanel());

        /*TFRenderer = new TFRenderer(visualization);
        visualization.addRenderer(TFRenderer);
        TFRenderer.addTFChangeListener(visualization);
        tabbedPanel.addTab("transfer", TFRenderer.getPanel());*/

        compRenderer = new CompRenderer(visualization);
        visualization.addRenderer(compRenderer);
        compRenderer.addTFChangeListener(visualization);
        tabbedPanel.addTab("Composition", compRenderer.getPanel());

        opRenderer = new OpacityRenderer(visualization);
        visualization.addRenderer(opRenderer);
        opRenderer.addTFChangeListener(visualization);
        tabbedPanel.addTab("Opacity Weighting", opRenderer.getPanel());

        tabbedPanel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                if (tabbedPanel.getSelectedIndex() == 0) {
                    return;
                }
                for (Renderer r : visualization.getRenderers()) {
                    r.setVisible(false);
                }
                visualization.getRenderers().get(tabbedPanel.getSelectedIndex() - 1).setVisible(true);
                visualization.update();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        tabbedPanel = new javax.swing.JTabbedPane();
        loadVolume = new javax.swing.JPanel();
        loadButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        infoTextPane = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        renderPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        splitPane.setDividerLocation(600);

        tabbedPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabbedPanelStateChanged(evt);
            }
        });

        loadButton.setText("Load volume");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });

        infoTextPane.setEditable(false);
        jScrollPane1.setViewportView(infoTextPane);

        jButton1.setText("Print image");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout loadVolumeLayout = new javax.swing.GroupLayout(loadVolume);
        loadVolume.setLayout(loadVolumeLayout);
        loadVolumeLayout.setHorizontalGroup(
            loadVolumeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loadVolumeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loadVolumeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                    .addGroup(loadVolumeLayout.createSequentialGroup()
                        .addComponent(loadButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)))
                .addContainerGap())
        );
        loadVolumeLayout.setVerticalGroup(
            loadVolumeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loadVolumeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(loadVolumeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadButton)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPanel.addTab("Load", loadVolume);

        splitPane.setRightComponent(tabbedPanel);

        javax.swing.GroupLayout renderPanelLayout = new javax.swing.GroupLayout(renderPanel);
        renderPanel.setLayout(renderPanelLayout);
        renderPanelLayout.setHorizontalGroup(
            renderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 599, Short.MAX_VALUE)
        );
        renderPanelLayout.setVerticalGroup(
            renderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 522, Short.MAX_VALUE)
        );

        splitPane.setLeftComponent(renderPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 988, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("../set2_data"));
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isFile()) {
                    if (f.getName().toLowerCase().endsWith(".fld")) {
                        return true;
                    }
                }
                if (f.isDirectory()) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "AVS files";
            }
        });
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            volume = new Volume(file);

            String infoText = new String("Volume data info:\n");
            infoText = infoText.concat(file.getName() + "\n");
            infoText = infoText.concat("dimensions:\t\t" + volume.getDimX() + " x " + volume.getDimY() + " x " + volume.getDimZ() + "\n");
            infoText = infoText.concat("voxel value range:\t" + volume.getMinimum() + " - " + volume.getMaximum());
            infoTextPane.setText(infoText);
            raycastRenderer.setVolume(volume);
            MIPRenderer.setVolume(volume);
            MIPRendererhi.setVolume(volume);
            //TFRenderer.setVolume(volume);
            opRenderer.setVolume(volume);
            compRenderer.setVolume(volume);
            visualization.getRenderers().get(0).setVisible(true);
            visualization.update();

        }
    }//GEN-LAST:event_loadButtonActionPerformed

    private void tabbedPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabbedPanelStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_tabbedPanelStateChanged

    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
                BufferedImage image = new BufferedImage( renderPanel.getWidth(),  renderPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        renderPanel.printAll(g);
        image.flush();
        
        try {
            ImageIO.write(image, "png", new File("image.png"));
        } catch (IOException ex) {
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VolVisApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VolVisApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VolVisApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VolVisApplication.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new VolVisApplication().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane infoTextPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadButton;
    private javax.swing.JPanel loadVolume;
    private javax.swing.JPanel renderPanel;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPanel;
    // End of variables declaration//GEN-END:variables
}
