package volvis;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import gui.MIPRendererPanel;
import gui.RaycastRendererPanel;
import gui.TransferFunctionEditor;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javafx.util.Pair;
import javax.media.opengl.GL2;
import util.TFChangeListener;
import util.VectorMath;
import volume.Volume;

/**
 *
 * @author Mart
 */
public class OpacityRenderer extends Renderer implements TFChangeListener {

    private Volume volume = null;
    MIPRendererPanel panel;
    TransferFunction tFunc;
    TransferFunctionEditor tfEditor;
    int count = 0;

    public OpacityRenderer(Visualization vis) {
        panel = new MIPRendererPanel(this, vis);
        panel.setSpeedLabel("0");
    }

    public void setVolume(Volume vol) {
        volume = vol;
        System.out.println(vol.getMaximum() + " " + vol.getMinimum());
        // set up image for storing the resulting rendering
        // the image width and height are equal to the length of the volume diagonal
        int imageSize = (int) Math.floor(Math.sqrt(vol.getDimX() * vol.getDimX() + vol.getDimY() * vol.getDimY()
                + vol.getDimZ() * vol.getDimZ()));
        if (imageSize % 2 != 0) {
            imageSize = imageSize + 1;
        }
        image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
        tFunc = new TransferFunction(volume.getMinimum(), volume.getMaximum());
        tFunc.addTFChangeListener(this);
        tfEditor = new TransferFunctionEditor(tFunc, volume.getHistogram(), true);
        panel.setTransferFunctionEditor(tfEditor);

    }

    @Override
    public void changed() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).changed();
        }
    }

    public MIPRendererPanel getPanel() {
        return panel;
    }

    short[] getVoxels(double[] coord, double[] vector) {

        double xZeroAt, yZeroAt, zZeroAt;
        double xMaxAt, yMaxAt, zMaxAt;

        if (vector[0] >= 0) {
            xZeroAt = -coord[0] / vector[0];
            xMaxAt = (volume.getDimX() - coord[0]) / vector[0];
        } else {
            xZeroAt = (volume.getDimX() - coord[0]) / vector[0];
            xMaxAt = -coord[0] / vector[0];
        }

        if (vector[1] >= 0) {
            yZeroAt = -coord[1] / vector[1];
            yMaxAt = (volume.getDimY() - coord[1]) / vector[1];
        } else {
            yZeroAt = (volume.getDimY() - coord[1]) / vector[1];
            yMaxAt = -coord[1] / vector[1];
        }

        if (vector[2] >= 0) {
            zZeroAt = -coord[2] / vector[2];
            zMaxAt = (volume.getDimZ() - coord[2]) / vector[2];
        } else {
            zZeroAt = (volume.getDimZ() - coord[2]) / vector[2];
            zMaxAt = -coord[2] / vector[2];
        }

        xZeroAt = vector[0] == 0 ? -Double.MAX_VALUE : xZeroAt;
        yZeroAt = vector[1] == 0 ? -Double.MAX_VALUE : yZeroAt;
        zZeroAt = vector[2] == 0 ? -Double.MAX_VALUE : zZeroAt;

        xMaxAt = vector[0] == 0 ? Double.MAX_VALUE : xMaxAt;
        yMaxAt = vector[1] == 0 ? Double.MAX_VALUE : yMaxAt;
        zMaxAt = vector[2] == 0 ? Double.MAX_VALUE : zMaxAt;

        double start = Math.max(Math.max(xZeroAt, yZeroAt), zZeroAt);
        double end = Math.min(Math.min(xMaxAt, yMaxAt), zMaxAt);
        int slices = Integer.parseInt(panel.Samples.getValue().toString());
        double length = end - start;
        double diff = (length / (slices - 1));

        short[] fuckDezeShit = new short[slices];
        for (double i = 0; i < slices; i++) {
            int x = (int) Math.round((double) coord[0] + (double) vector[0] * ((double) start + (double) diff * i));
            int y = (int) Math.round((double) coord[1] + (double) vector[1] * ((double) start + (double) diff * i));
            int z = (int) Math.round((double) coord[2] + (double) vector[2] * ((double) start + (double) diff * i));
            fuckDezeShit[(int) i] = ((x >= 0) && (x < volume.getDimX()) && (y >= 0) && (y < volume.getDimY())
                    && (z >= 0) && (z < volume.getDimZ())) ? volume.getVoxel(x, y, z) : 0;
        }

        return fuckDezeShit;
    }

    double[] getRayColor(double[] coord, double[] vector) {

        double xZeroAt, yZeroAt, zZeroAt;
        double xMaxAt, yMaxAt, zMaxAt;

        if (vector[0] >= 0) {
            xZeroAt = -coord[0] / vector[0];
            xMaxAt = (volume.getDimX() - coord[0]) / vector[0];
        } else {
            xZeroAt = (volume.getDimX() - coord[0]) / vector[0];
            xMaxAt = -coord[0] / vector[0];
        }

        if (vector[1] >= 0) {
            yZeroAt = -coord[1] / vector[1];
            yMaxAt = (volume.getDimY() - coord[1]) / vector[1];
        } else {
            yZeroAt = (volume.getDimY() - coord[1]) / vector[1];
            yMaxAt = -coord[1] / vector[1];
        }

        if (vector[2] >= 0) {
            zZeroAt = -coord[2] / vector[2];
            zMaxAt = (volume.getDimZ() - coord[2]) / vector[2];
        } else {
            zZeroAt = (volume.getDimZ() - coord[2]) / vector[2];
            zMaxAt = -coord[2] / vector[2];
        }

        xZeroAt = vector[0] == 0 ? -Double.MAX_VALUE : xZeroAt;
        yZeroAt = vector[1] == 0 ? -Double.MAX_VALUE : yZeroAt;
        zZeroAt = vector[2] == 0 ? -Double.MAX_VALUE : zZeroAt;

        xMaxAt = vector[0] == 0 ? Double.MAX_VALUE : xMaxAt;
        yMaxAt = vector[1] == 0 ? Double.MAX_VALUE : yMaxAt;
        zMaxAt = vector[2] == 0 ? Double.MAX_VALUE : zMaxAt;

        double start = Math.max(Math.max(xZeroAt, yZeroAt), zZeroAt);
        double end = Math.min(Math.min(xMaxAt, yMaxAt), zMaxAt);
        int slices = Integer.parseInt(panel.Samples.getValue().toString());
        double length = end - start;
        double diff = (length / (slices - 1));
        double c_red, c_green, c_blue;
        c_red = c_green = c_blue = 0;

        ArrayList<TransferFunction.ControlPoint> controlPoints = tFunc.getControlPoints();
        Region[] regions = new Region[(controlPoints.size() - 1) / 2];
//        if(++count % 10000 == 0){
//            System.out.println(controlPoints.size());
//            count = 0;
//        
//        }
        int at = 0;
        if (regions.length > 1) {
            for (int i = 1; i < controlPoints.size(); i += 2) {
                regions[at++] = new Region(controlPoints.get(i).value, controlPoints.get(i).color.a);
            }
        }

//        for (double i = slices - 1; i >= 0; i--) {
        for (double i = 0; i < slices; i++) {
            int x = (int) Math.round((double) coord[0] + (double) vector[0] * ((double) start + (double) diff * i));
//            int xp = (int) Math.round((double) coord[0] + (double) vector[0] * ((double) start + (double) diff * (i-1)));
//            int xn = (int) Math.round((double) coord[0] + (double) vector[0] * ((double) start + (double) diff * (i+1)));
            int y = (int) Math.round((double) coord[1] + (double) vector[1] * ((double) start + (double) diff * i));
//            int yp = (int) Math.round((double) coord[1] + (double) vector[1] * ((double) start + (double) diff * (i-1)));
//            int yn = (int) Math.round((double) coord[1] + (double) vector[1] * ((double) start + (double) diff * (i+1)));
            int z = (int) Math.round((double) coord[2] + (double) vector[2] * ((double) start + (double) diff * i));
//            int zp = (int) Math.round((double) coord[2] + (double) vector[2] * ((double) start + (double) diff * (i-1)));
//            int zn = (int) Math.round((double) coord[2] + (double) vector[2] * ((double) start + (double) diff * (i+1)));

            short fxi = getVoxel(x, y, z);

            TFColor voxelColor = tFunc.getColor(fxi);

            double[] dfxi = new double[]{
                0.5 * (getVoxel(x - 1, y, z) - getVoxel(x + 1, y, z)),
                0.5 * (getVoxel(x, y - 1, z) - getVoxel(x, y + 1, z)),
                0.5 * (getVoxel(x, y, z - 1) - getVoxel(x, y, z + 1))
            };

            double dfxil = VectorMath.length(dfxi);

            double a = 0;
            if (regions.length >= 2) {
                for (int v = 0; v < regions.length - 1; v++) {
                    if (!(regions[v].v <= fxi && fxi <= regions[v + 1].v)) {
                        continue;
                    }
                    double an = regions[v].a;
                    double anp1 = regions[v + 1].a;

                    a += anp1 * (fxi - regions[v].v);
                    a += an * (regions[v + 1].v - fxi);
                    a /= (regions[v + 1].v - regions[v].v);

                    a *= dfxil / tFunc.num;
//                a *= voxelColor.a;

                    break;
                }
            }
            if (a == 0) {
                continue;
            }

            double ai = 1 - a;

            //c_alpha = ai * c_alpha + (voxelColor.a <= 1.0 ? voxelColor.a : 1) * a;
            c_red = ai * c_red + (voxelColor.r <= 1.0 ? voxelColor.r : 1) * a;
            c_green = ai * c_green + (voxelColor.g <= 1.0 ? voxelColor.g : 1) * a;
            c_blue = ai * c_blue + (voxelColor.b <= 1.0 ? voxelColor.b : 1) * a;

        }
        return new double[]{c_red, c_green, c_blue};

    }

    short getVoxel(int x, int y, int z) {
        if ((x >= 0) && (x < volume.getDimX()) && (y >= 0) && (y < volume.getDimY())
                && (z >= 0) && (z < volume.getDimZ())) {
            return volume.getVoxel(x, y, z);
        } else {
            return 0;
        }
    }

    // get a voxel from the volume data by nearest neighbor interpolation
    short getVoxel(double[] coord) {
        // Lijkt me dat hier iets moet gebeuren om alle voxels te selecteren ofzo
        int x = (int) Math.round(coord[0]);
        int y = (int) Math.round(coord[1]);
        int z = (int) Math.round(coord[2]);

        if ((x >= 0) && (x < volume.getDimX()) && (y >= 0) && (y < volume.getDimY())
                && (z >= 0) && (z < volume.getDimZ())) {
            return volume.getVoxel(x, y, z);
        } else {
            return 0;
        }
    }

    void slicer(double[] viewMatrix) {

        // clear image
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                image.setRGB(i, j, 0);
            }
        }

        // vector uVec and vVec define a plane through the origin, 
        // perpendicular to the view vector viewVec
        double[] viewVec = new double[3];
        double[] uVec = new double[3];
        double[] vVec = new double[3];
        VectorMath.setVector(viewVec, viewMatrix[2], viewMatrix[6], viewMatrix[10]);
        VectorMath.setVector(uVec, viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        VectorMath.setVector(vVec, viewMatrix[1], viewMatrix[5], viewMatrix[9]);

        // image is square
        int imageCenter = image.getWidth() / 2;
        
        double[] pixelCoord = new double[3];
        double[] volumeCenter = new double[3];
        VectorMath.setVector(volumeCenter, volume.getDimX() / 2, volume.getDimY() / 2, volume.getDimZ() / 2);

        // sample on a plane through the origin of the volume data
//        double max = volume.getMaximum();
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                pixelCoord[0] = uVec[0] * (i - imageCenter) + vVec[0] * (j - imageCenter)
                        + volumeCenter[0];
                pixelCoord[1] = uVec[1] * (i - imageCenter) + vVec[1] * (j - imageCenter)
                        + volumeCenter[1];
                pixelCoord[2] = uVec[2] * (i - imageCenter) + vVec[2] * (j - imageCenter)
                        + volumeCenter[2];

//                int val = getVoxel(pixelCoord);
                short[] blub = getVoxels(pixelCoord, viewVec);
                double c_red, c_green, c_blue, c_mult, c_alpha;
                c_red = c_green = c_blue = c_alpha = 0;
                c_mult = 1;
                for (int q = blub.length - 1; q >= 0; q--) {
//                    System.out.println(blub[q]);
//                    if(blub[q]==1 || blub[q]){
//                        continue;
//                    }
//                    System.out.println(blub[q]);
                    TFColor voxelColor = tFunc.getColor(blub[q]);

                    double curMult = (voxelColor.a <= 1.0 ? voxelColor.a : 1);
                    c_red *= (1-curMult);
                    c_green *= (1-curMult);
                    c_blue *= (1-curMult);
                    c_red += curMult * (voxelColor.r <= 1.0 ? voxelColor.r : 1);
                    c_green += curMult * (voxelColor.g <= 1.0 ? voxelColor.g : 1);
                    c_blue += curMult * (voxelColor.b <= 1.0 ? voxelColor.b : 1);
                    c_alpha += curMult * (voxelColor.a <= 1.0 ? voxelColor.a : 1);
                    //c_mult = (1 - (voxelColor.a <= 1.0 ? voxelColor.a : 1));
                }
                int red = (int) Math.round(c_red * 255);
                int blue = (int) Math.round(c_blue * 255);
                int green = (int) Math.round(c_green * 255);
                int alpha = (int) Math.round(c_alpha * 255);
                
                // (c_alpha << 24) | 
                int pixelColor = (254 << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(i, j, pixelColor);
            }
        }

    }

    private void drawBoundingBox(GL2 gl) {
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor4d(1.0, 1.0, 1.0, 1.0);
        gl.glLineWidth(1.5f);
        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_LOOP);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glVertex3d(-volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, volume.getDimZ() / 2.0);
        gl.glVertex3d(volume.getDimX() / 2.0, -volume.getDimY() / 2.0, -volume.getDimZ() / 2.0);
        gl.glEnd();

        gl.glDisable(GL2.GL_LINE_SMOOTH);
        gl.glDisable(GL2.GL_BLEND);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopAttrib();

    }

    @Override
    public void visualize(GL2 gl) {

        if (volume == null) {
            return;
        }

        drawBoundingBox(gl);

        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, viewMatrix, 0);

        long startTime = System.currentTimeMillis();
        slicer(viewMatrix);
        long endTime = System.currentTimeMillis();
        double runningTime = (endTime - startTime);
        panel.setSpeedLabel(Double.toString(runningTime));

        Texture texture = AWTTextureIO.newTexture(gl.getGLProfile(), image, false);

        gl.glPushAttrib(GL2.GL_LIGHTING_BIT);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // draw rendered image as a billboard texture
        texture.enable(gl);
        texture.bind(gl);
        double halfWidth = image.getWidth() / 2.0;
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(-halfWidth, -halfWidth, 0.0);
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(-halfWidth, halfWidth, 0.0);
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(halfWidth, halfWidth, 0.0);
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(halfWidth, -halfWidth, 0.0);
        gl.glEnd();
        texture.disable(gl);
        texture.destroy(gl);
        gl.glPopMatrix();

        gl.glPopAttrib();

        if (gl.glGetError() > 0) {
            System.out.println("some OpenGL error: " + gl.glGetError());
        }

    }
    private BufferedImage image;
    private double[] viewMatrix = new double[4 * 4];

    private static class Region {

        public double v;
        public double a;

        public Region(double v, double a) {
            this.v = v;
            this.a = a;
        }
    }
}
