// https://en.wikipedia.org/wiki/Equirectangular_projection#Reverse



package rotate3dshapes;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import static com.jogamp.opengl.GL.*;  // GL constants
import static com.jogamp.opengl.GL2.*; // GL2 constants
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import com.jogamp.opengl.GLAnimatorControl;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * JOGL 2.0 Example 2: Rotating 3D Shapes (GLCanvas)
 */
@SuppressWarnings("serial")
public class Rotate3DShapes extends GLCanvas
        implements GLEventListener, KeyListener,  MouseListener, MouseMotionListener
{
    // Define constants for the top-level container

    private static String TITLE = "Rotating 3D Shapes (GLCanvas)";  // window's title
    private static final int FPS = 60; // animator's target frames per second
    private float xrot, yrot, zrot;
    private static String home = System.getProperty("user.home");
    
    private java.util.Random random;
    
    private float mouseTestMove = 0.0f;
    
    private String[] textureImages = new String[]
    {
        home + "\\Documents\\NetBeansProjects\\javahome\\WorldMap.jpg",
    };

    private int[] textures = new int[textureImages.length];

    /**
     * The entry main() method to setup the top-level container and animator
     */
    public static void main(String[] args)
    {
        // Run the GUI codes in the event-dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                // Create the OpenGL rendering canvas
                GLCanvas canvas = new Rotate3DShapes();
                // canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

                // Create a animator that drives canvas' display() at the specified FPS.
                final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

                // Create the top-level container
                final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
                frame.getContentPane().add(canvas);
                frame.addWindowListener(new WindowAdapter()
                {
                    @Override
                    public void windowClosing(WindowEvent e)
                    {
                        // Use a dedicate thread to run the stop() to ensure that the
                        // animator stops before program exits.
                        new Thread()
                        {
                            @Override
                            public void run()
                            {
                                if (animator.isStarted())
                                {
                                    animator.stop();
                                }
                                System.exit(0);
                            }
                        }.start();
                    }
                });
                //frame.setTitle(TITLE);
                //frame.pack();

                frame.setUndecorated(true);     // no decoration such as title bar
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);  // full screen mode

                frame.setVisible(true);
                animator.start(); // start the animation loop
            }
        });
    }

    // Setup OpenGL Graphics Renderer
    private GLU glu;  // for the GL Utility
    private float diffOpening = 0.0f;

    private float currentOpening = 0.0f;
    private float speedOpening = 0.000f;   // opening for cube

    /**
     * Constructor to setup the GUI for this Component
     */
    public Rotate3DShapes()
    {
        random = new java.util.Random();
        this.addGLEventListener(this);
        this.addKeyListener(this); // for Handling KeyEvents
        this.addMouseListener(this); // for Handling KeyEvents
        this.addMouseMotionListener(this); // for Handling KeyEvents
        this.setFocusable(true);
        this.requestFocus();
    }

    // ------ Implement methods declared in GLEventListener ------
    /**
     * Called back immediately after the OpenGL context is initialized. Can be used to perform one-time initialization. Run only once.
     */
    @Override
    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
        glu = new GLU();                         // get GL Utilities
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
        gl.glClearDepth(1.0f);      // set clear depth value to farthest

        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting

        gl.glEnable(GL2.GL_TEXTURE_2D);
        try
        {

            for (int i = 0; i < textureImages.length; i++)
            {
                File im = new File(textureImages[i]);
                Texture t = TextureIO.newTexture(im, true);
                textures[i] = t.getTextureObject(gl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Call-back handler for window re-size event. Also called when the drawable is first set to visible.
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

        if (height == 0)
        {
            height = 1;   // prevent divide by zero
        }
        float aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        glu.gluPerspective(45.0, aspect, 0.1, 100.0); // fovy, aspect, zNear, zFar

        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity(); // reset
    }

    /**
     * Called back by the animator to perform rendering.
     */
    @Override
    public void display(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers

        // ----- Render the Color Cube -----
        gl.glLoadIdentity();                // reset the current model-view matrix
        gl.glTranslatef(0.0f, 0.0f, -5.0f - mouseTestMove); // translate right and into the screen
        // gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and z-axes

        gl.glRotatef(30f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(-23.27f, 0.0f, 0.0f, 1.0f);
        gl.glRotatef(xrot, 0.0f, 1.0f, 0.0f);
        
        
//        gl.glRotatef(xrot, 1.0f, 1.0f, 1.0f);
//        gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
//        gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);

        int SIZE = 120;
        
        int latitudeCount = SIZE; // széleségi körök száma
        int meridianCount = 2*SIZE; // hosszúsági körök száma
        
        double tX = 1d;
        double tY = 1d;
        
        double lDiff = 1d/latitudeCount;
        double mDiff = 1d/meridianCount;
        
        for (int l = 0; l < latitudeCount; l++) {
            for (int m = 0; m < meridianCount; m++)
            {
                
                double alpha1 = l * 1 * Math.PI / latitudeCount - Math.PI/2;
                double alpha2 = (l+1) * 1  * Math.PI / latitudeCount - Math.PI/2;
                
                double beta1 = m * 2 * Math.PI / meridianCount;
                double beta2 = (m+1) * 2 * Math.PI / meridianCount;
                
                gl.glBegin(GL_QUADS); // of the color cube
                // gl.glColor3d(random.nextInt(256)/256f, random.nextInt(256)/256f, random.nextInt(256)/256f); // green
                // gl.glColor3d(0.5, (1.0d * l)/latitudeCount, (1.0d * m)/meridianCount); // green
                
//                gl.glEnable (GL_BLEND); 
//                gl.glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//                gl.glColor4d(1.0d, 1.0d, 1.0d, 0.9d);

                gl.glTexCoord2d(m*mDiff*tX, l*lDiff*tY);
                gl.glVertex3d(Math.sin(beta1) * Math.cos(alpha1), Math.sin(alpha1), Math.cos(beta1) * Math.cos(alpha1));

                gl.glTexCoord2d((m+1)*mDiff*tX, l*lDiff*tY);
                gl.glVertex3d(Math.sin(beta2) * Math.cos(alpha1), Math.sin(alpha1), Math.cos(beta2) * Math.cos(alpha1));

                gl.glTexCoord2d((m+1)*mDiff*tX, (l+1)*lDiff*tY);
                gl.glVertex3d(Math.sin(beta2) * Math.cos(alpha2), Math.sin(alpha2), Math.cos(beta2) * Math.cos(alpha2));

                gl.glTexCoord2d(m*mDiff*tX, (l+1)*lDiff*tY);
                gl.glVertex3d(Math.sin(beta1) * Math.cos(alpha2), Math.sin(alpha2), Math.cos(beta1) * Math.cos(alpha2));
                gl.glEnd(); // of the color cube                
                
               
//                gl.glBegin(GL_QUADS); // of the color cube
//                // gl.glColor3d(random.nextInt(256)/256f, random.nextInt(256)/256f, random.nextInt(256)/256f); // green
//                // gl.glColor3d(0.5, (1.0d * l)/latitudeCount, (1.0d * m)/meridianCount); // green
//                gl.glTexCoord2f(0.0f, 0.0f);
//                gl.glVertex3d(Math.sin(beta1) * Math.cos(alpha1), Math.sin(alpha1), Math.cos(beta1) * Math.cos(alpha1));
//
//                gl.glTexCoord2f(1.0f, 0.0f);
//                gl.glVertex3d(Math.sin(beta2) * Math.cos(alpha1), Math.sin(alpha1), Math.cos(beta2) * Math.cos(alpha1));
//
//                gl.glTexCoord2f(1.0f, 1.0f);
//                gl.glVertex3d(Math.sin(beta2) * Math.cos(alpha2), Math.sin(alpha2), Math.cos(beta2) * Math.cos(alpha2));
//
//                gl.glTexCoord2f(0.0f, 1.0f);
//                gl.glVertex3d(Math.sin(beta1) * Math.cos(alpha2), Math.sin(alpha2), Math.cos(beta1) * Math.cos(alpha2));
//                gl.glEnd(); // of the color cube
                
            }
        }

        // gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0]);
        // Update the rotational angle after each refresh.
        currentOpening += speedOpening;

        //change the speeds here
        xrot += 0.42f;
        yrot += 0.31f;
        zrot += 0.1f;

    }

    /**
     * Called back before the OpenGL context is destroyed. Release resource such as buffers.
     */
    @Override
    public void dispose(GLAutoDrawable drawable)
    {
    }

    // ------ Implement methods declared in KeyListener ------
    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        switch (keyCode)
        {
            case KeyEvent.VK_ESCAPE: // quit
                // Use a dedicate thread to run the stop() to ensure that the
                // animator stops before program exits.
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        GLAnimatorControl animator = getAnimator();
                        if (animator.isStarted())
                        {
                            animator.stop();
                        }
                        System.exit(0);
                    }
                }.start();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }
    
// Mouse events

    // Do-nothing methods, but required nonetheless
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override    
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            mouseTestMove += 0.3f;
        } else if(e.getButton() == MouseEvent.BUTTON3) {
            mouseTestMove -= 0.3f;
        }
        
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
}
