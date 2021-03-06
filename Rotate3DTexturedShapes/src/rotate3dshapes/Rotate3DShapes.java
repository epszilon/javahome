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
         implements GLEventListener, KeyListener {
   // Define constants for the top-level container
   private static String TITLE = "Rotating 3D Shapes (GLCanvas)";  // window's title
   private static final int FPS = 60; // animator's target frames per second
   private float xrot,yrot,zrot;
   private static String home = System.getProperty("user.home");
   private String[] textureImages = new String[] 
    { 
       
       
       
        home + "\\Documents\\NetBeansProjects\\javahome\\IMG_0475.JPG",
        home + "\\Documents\\NetBeansProjects\\javahome\\IMG_0652.JPG",
        home + "\\Documents\\NetBeansProjects\\javahome\\IMG_2719.JPG",
        home + "\\Documents\\NetBeansProjects\\javahome\\IMG_3544.JPG",
        home + "\\Documents\\NetBeansProjects\\javahome\\IMG_6354.JPG",
        home + "\\Documents\\NetBeansProjects\\javahome\\IMG_7060.JPG",

    };
   
   private int[] textures = new int[textureImages.length];   
 
   
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
   private float diffOpening = 0.0f;
  
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

      gl.glEnable(GL2.GL_TEXTURE_2D);
      try{
	
        for(int i = 0; i<textureImages.length; i++)  
        {
            File im = new File(textureImages[i]);
            Texture t = TextureIO.newTexture(im, true);
            textures[i]= t.getTextureObject(gl);
        }
      
//        File im = new File("c:\\Users\\Gyuri\\Documents\\NetBeansProjects\\javahome\\IMG_0652.JPG");
//        Texture t = TextureIO.newTexture(im, true);
//        texture = t.getTextureObject(gl);
         
          
      }catch(IOException e){
         e.printStackTrace();
      }      
        
      
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

      float diff = (float)Math.abs(Math.sin(currentOpening));

 
      // ----- Render the Color Cube -----
      gl.glLoadIdentity();                // reset the current model-view matrix
      gl.glTranslatef(0.0f, 0.0f, -15.0f); // translate right and into the screen
      // gl.glRotatef(angleCube, 1.0f, 1.0f, 1.0f); // rotate about the x, y and z-axes
 
      gl.glRotatef(xrot, 1.0f, 1.0f, 1.0f);
      gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
      gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);      
      

      gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0]);      
      gl.glBegin(GL_QUADS); // of the color cube
      // Top-face
      //gl.glColor3f(0.0f, 1.0f, 0.0f); // green
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f, 1.0f + diff, -1.0f);
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, 1.0f + diff, -1.0f);
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f + diff, 1.0f);
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f, 1.0f + diff, 1.0f);
      gl.glEnd();

      // Bottom-face
      gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[1]);      
      gl.glBegin(GL_QUADS); // of the color cube
      //gl.glColor3f(1.0f, 0.5f, 0.0f); // orange
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f, -1.0f - diff, 1.0f);
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f - diff, 1.0f);
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, -1.0f - diff, -1.0f);
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f, -1.0f - diff, -1.0f);
      gl.glEnd();
 
      // Front-face
      gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[2]);      
      gl.glBegin(GL_QUADS); // of the color cube
      //gl.glColor3f(1.0f, 0.0f, 0.0f); // red
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f, 1.0f, 1.0f + diff);
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, 1.0f + diff);
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, 1.0f + diff);
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f, -1.0f, 1.0f + diff);
      gl.glEnd();
 
      // Back-face
      gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[3]);      
      gl.glBegin(GL_QUADS); // of the color cube
      //gl.glColor3f(1.0f, 1.0f, 0.0f); // yellow
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f, -1.0f, -1.0f - diff);
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, -1.0f, -1.0f - diff);
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f, 1.0f, -1.0f - diff);
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f, 1.0f, -1.0f - diff);
      gl.glEnd(); // of the color cube
 
      // Left-face
      gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[4]);      
      gl.glBegin(GL_QUADS); // of the color cube
      //gl.glColor3f(0.0f, 0.0f, 1.0f); // blue
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f - diff, 1.0f, 1.0f);
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f - diff, 1.0f, -1.0f);
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1.0f - diff, -1.0f, -1.0f);
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1.0f - diff, -1.0f, 1.0f);
      gl.glEnd(); // of the color cube
 
      // Right-face
      gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[5]);      
      gl.glBegin(GL_QUADS); // of the color cube
      //gl.glColor3f(1.0f, 0.0f, 1.0f); // magenta
      gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1.0f + diff, 1.0f, -1.0f);
      gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1.0f + diff, 1.0f, 1.0f);
      gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1.0f + diff, -1.0f, 1.0f);
      gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1.0f + diff, -1.0f, -1.0f);
      gl.glEnd(); // of the color cube
 
      // Update the rotational angle after each refresh.
      currentOpening += speedOpening;

      //change the speeds here
      xrot += 0.31f;
      yrot += 0.13f;
      zrot += 0.27f;   
   
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