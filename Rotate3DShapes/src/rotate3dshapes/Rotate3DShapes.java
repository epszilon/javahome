package rotate3dshapes;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import static com.jogamp.opengl.GL.*;  // GL constants
import static com.jogamp.opengl.GL2.*; // GL2 constants
import com.jogamp.opengl.GLAnimatorControl;
 
/**
 * JOGL 2.0 Example 2: Rotating 3D Shapes (GLCanvas)
 */
@SuppressWarnings("serial")
public class Rotate3DShapes extends GLCanvas
         implements GLEventListener, KeyListener {
   // Define constants for the top-level container
   private static String TITLE = "Rotating 3D Shapes (GLCanvas)";  // window's title
   private static final int FPS = 60; // animator's target frames per second
 
   /** The entry main() method to setup the top-level container and animator */
   public static void main(String[] args) {
      // Run the GUI codes in the event-dispatching thread for thread safety
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            // Create the OpenGL rendering canvas
            GLCanvas canvas = new Rotate3DShapes();
            // canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
 
            // Create a animator that drives canvas' display() at the specified FPS.
            final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
 
            // Create the top-level container
            final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
            frame.getContentPane().add(canvas);
            frame.addWindowListener(new WindowAdapter() {
               @Override
               public void windowClosing(WindowEvent e) {
                  // Use a dedicate thread to run the stop() to ensure that the
                  // animator stops before program exits.
                  new Thread() {
                     @Override
                     public void run() {
                        if (animator.isStarted()) animator.stop();
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
   private float anglePyramid = 0;    // rotational angle in degree for pyramid
   private float angleCube = 0;       // rotational angle in degree for cube
   private float diffOpening = 0.0f;
  
   private float speedPyramid = 2.0f; // rotational speed for pyramid
   private float speedCube = -1.5f;   // rotational speed for cube
   private float currentOpening = 0.0f;
   private float speedOpening = 0.000f;   // opening for cube
   
   
   /** Constructor to setup the GUI for this Component */
   public Rotate3DShapes() {
      this.addGLEventListener(this);
      this.addKeyListener(this); // for Handling KeyEvents
      this.setFocusable(true);
      this.requestFocus();
   }
 
   // ------ Implement methods declared in GLEventListener ------
 
   /**
    * Called back immediately after the OpenGL context is initialized. Can be used
    * to perform one-time initialization. Run only once.
    */
   @Override
   public void init(GLAutoDrawable drawable) {
      GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
      glu = new GLU();                         // get GL Utilities
      gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
      gl.glClearDepth(1.0f);      // set clear depth value to farthest
      
      gl.glEnable(GL_DEPTH_TEST); // enables depth testing
      gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
      gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
      gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
      
      
//        gl.glEnable(GL_BLEND);
//        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//         gl.glEnable(GL_LINE_SMOOTH);      
//      gl.glEnable(GL_POLYGON_SMOOTH);
//      gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

      
        
      
   }
 
   /**
    * Call-back handler for window re-size event. Also called when the drawable is
    * first set to visible.
    */
   @Override
   public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context

      
      
      if (height == 0) height = 1;   // prevent divide by zero
      float aspect = (float)width / height;
 
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
   public void display(GLAutoDrawable drawable) {
      GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
      gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
 
      // ----- Render the Pyramid -----
      gl.glLoadIdentity();                 // reset the model-view matrix
      gl.glTranslatef(-1.6f, 0.0f, -6.0f); // translate left and into the screen
      gl.glRotatef(anglePyramid, -0.2f, 1.0f, 0.0f); // rotate about the y-axis
 
      gl.glBegin(GL_TRIANGLES); // of the pyramid
 
      
      float diff = (float)Math.abs(Math.sin(currentOpening));
      
      // Font-face triangle
      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
      gl.glVertex3f(0.0f, 1.0f , 0.0f);
      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
      gl.glVertex3f(-1.0f, -1.0f, 1.0f);
      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
      gl.glVertex3f(1.0f, -1.0f, 1.0f);
 
      // Right-face triangle
      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
      gl.glVertex3f(0.0f, 1.0f, 0.0f);
      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
      gl.glVertex3f(1.0f, -1.0f, 1.0f);
      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
      gl.glVertex3f(1.0f, -1.0f, -1.0f);
 
      // Back-face triangle
      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
      gl.glVertex3f(0.0f, 1.0f, 0.0f);
      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
      gl.glVertex3f(1.0f, -1.0f, -1.0f);
      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
      gl.glVertex3f(-1.0f, -1.0f, -1.0f);
 
      // Left-face triangle
      gl.glColor3f(1.0f, 0.0f, 0.0f); // Red
      gl.glVertex3f(0.0f, 1.0f, 0.0f);
      gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue
      gl.glVertex3f(-1.0f, -1.0f, -1.0f);
      gl.glColor3f(0.0f, 1.0f, 0.0f); // Green
      gl.glVertex3f(-1.0f, -1.0f, 1.0f);
 
      gl.glEnd(); // of the pyramid
 
      // ----- Render the Color Cube -----
      gl.glLoadIdentity();                // reset the current model-view matrix
      gl.glTranslatef(1.6f, 0.0f, -7.0f); // translate right and into the screen
      gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and z-axes
 
      gl.glBegin(GL_QUADS); // of the color cube
 
      // Top-face
      gl.glColor3f(0.0f, 1.0f, 0.0f); // green
      gl.glVertex3f(1.0f, 1.0f + diff, -1.0f);
      gl.glVertex3f(-1.0f, 1.0f + diff, -1.0f);
      gl.glVertex3f(-1.0f, 1.0f + diff, 1.0f);
      gl.glVertex3f(1.0f, 1.0f + diff, 1.0f);
 
      // Bottom-face
      gl.glColor3f(1.0f, 0.5f, 0.0f); // orange
      gl.glVertex3f(1.0f, -1.0f - diff, 1.0f);
      gl.glVertex3f(-1.0f, -1.0f - diff, 1.0f);
      gl.glVertex3f(-1.0f, -1.0f - diff, -1.0f);
      gl.glVertex3f(1.0f, -1.0f - diff, -1.0f);
 
      // Front-face
      gl.glColor3f(1.0f, 0.0f, 0.0f); // red
      gl.glVertex3f(1.0f, 1.0f, 1.0f + diff);
      gl.glVertex3f(-1.0f, 1.0f, 1.0f + diff);
      gl.glVertex3f(-1.0f, -1.0f, 1.0f + diff);
      gl.glVertex3f(1.0f, -1.0f, 1.0f + diff);
 
      // Back-face
      gl.glColor3f(1.0f, 1.0f, 0.0f); // yellow
      gl.glVertex3f(1.0f, -1.0f, -1.0f - diff);
      gl.glVertex3f(-1.0f, -1.0f, -1.0f - diff);
      gl.glVertex3f(-1.0f, 1.0f, -1.0f - diff);
      gl.glVertex3f(1.0f, 1.0f, -1.0f - diff);
 
      // Left-face
      gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
      gl.glVertex3f(-1.0f - diff, 1.0f, 1.0f);
      gl.glVertex3f(-1.0f - diff, 1.0f, -1.0f);
      gl.glVertex3f(-1.0f - diff, -1.0f, -1.0f);
      gl.glVertex3f(-1.0f - diff, -1.0f, 1.0f);
 
      // Right-face
      gl.glColor3f(1.0f, 0.0f, 1.0f); // magenta
      gl.glVertex3f(1.0f + diff, 1.0f, -1.0f);
      gl.glVertex3f(1.0f + diff, 1.0f, 1.0f);
      gl.glVertex3f(1.0f + diff, -1.0f, 1.0f);
      gl.glVertex3f(1.0f + diff, -1.0f, -1.0f);
 
      gl.glEnd(); // of the color cube
 
      // Update the rotational angle after each refresh.
      anglePyramid += speedPyramid;
      angleCube += speedCube;
      currentOpening += speedOpening;
   }
 
   /**
    * Called back before the OpenGL context is destroyed. Release resource such as buffers.
    */
   @Override
   public void dispose(GLAutoDrawable drawable) { }

  // ------ Implement methods declared in KeyListener ------
 
   @Override
   public void keyTyped(KeyEvent e) {}
 
   @Override
   public void keyPressed(KeyEvent e) {
      int keyCode = e.getKeyCode();
      switch (keyCode) {
         case KeyEvent.VK_ESCAPE: // quit
            // Use a dedicate thread to run the stop() to ensure that the
            // animator stops before program exits.
            new Thread() {
               @Override
               public void run() {
                  GLAnimatorControl animator = getAnimator();
                  if (animator.isStarted()) animator.stop();
                  System.exit(0);
               }
            }.start();
            break;
      }
   }
 
   @Override
   public void keyReleased(KeyEvent e) {}


}