
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;
    boolean zsoundDone;
    boolean bgSoundDone;

//variables for rocket.
    Image rocketImage;
    public int rocketXPos;
    public int rocketYPos;
    int numStars = 10;
    int starXPos[]=new int [numStars];
    int starYPos[]=new int [numStars];
    int rocketXSpeed;
    int rocketYSpeed;
    boolean rocketRight;
    int whichStar;
//    int numMissles = 30;
//    int missleXPos[] = new int[numMissles];
//    int missleYPos[] = new int[numMissles];
//    boolean missleVis[] = new boolean[numMissles];
//    int missleIndex;
    boolean starVis[] = new boolean[numStars];
//    boolean missleRight;
    
    Missle missles[] = new Missle[Missle.numMissles];
    
    
    
    

    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) 
                {
                    rocketYSpeed++;
                } 
                else if (e.VK_DOWN == e.getKeyCode()) 
                {
                    rocketYSpeed--;
                }
                else if (e.VK_LEFT == e.getKeyCode()) 
                {
                    rocketXSpeed++;
                } 
                else if (e.VK_RIGHT == e.getKeyCode()) 
                {
                    rocketXSpeed--;
                }
                else if (e.VK_INSERT == e.getKeyCode()) 
                {
                    zsound = new sound("ouch.wav");                    
                }
                else if (e.VK_SPACE == e.getKeyCode()) 
                {
                    for(int index = 0;index < missles.length;index++)
                    {
                        missles[index].active = true;
                    }
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }
        
        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
        
        g.setColor(Color.yellow);
        for(int index = 0; index < numStars; index++)
        {
            if(starVis[index])
            {
                drawCircle(getX(starXPos[index]),getYNormal(starYPos[index]),0.0,1.0,1.0);
            }
        }
        
        g.setColor(Color.green);
        for(int index = 0; index < Missle.numMissles; index++)
        {
            if(missles[index].active)
            {
//                drawMissle(getX(missleXPos[index]),getYNormal(missleYPos[index]),0.0,1.0,1.0);
            }
        }
        
        double rocketXScale = 1.0;
        if(rocketRight)
        {
            rocketXScale = 1.0;
        }
        else
        {
            rocketXScale = -1.0;
        }
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,rocketXScale,1.0 );

        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
    ////////////////////////////////////////////////////////////////////////////
    public void drawMissle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.fillRect(-5,-2,10,4);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        rocketXPos = getWidth2()/2;
        
        rocketYPos = getHeight2()/2;
        for(int index = 0; index < numStars; index++)
        {
            starXPos[index] = getX((int) (Math.random()*getWidth2()));
            starYPos[index] = getYNormal((int) (Math.random()*getHeight2()));
            starVis[index] = true;
        }
        rocketXSpeed = 0;
        rocketYSpeed = 0;
        rocketRight = true;
        zsoundDone = false;
        whichStar = -1;
        Missle.current = 0;
        
        missles = new Missle[Missle.numMissles];
        for(int index = 0; index < Missle.numMissles; index++)
        {
            missles[index] = new Missle();
        }
//        missleRight = true;
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            reset();
        }
        zsoundDone = false;
        boolean hit = false;
        for(int index = 0; index < numStars; index++)
        {
            if(starXPos[index] -20 < rocketXPos &&
               starXPos[index] +20 > rocketXPos &&
               starYPos[index] -20 < rocketYPos &&
               starYPos[index] +20 > rocketYPos)
            {

                hit = true;
                if(whichStar != index)
                {
                    zsound = new sound("ouch.wav");
                    zsoundDone = true; 
                }
               
               whichStar = index;
            }
        }
        if(!hit)
            {
                whichStar = -1;
            }
        if(rocketXSpeed < 0)
        {
            rocketRight = true;
        }
        else if(rocketXSpeed > 0)
        {
            rocketRight = false;
        }
        
        for(int index = 0; index < numStars; index++)
        {
            starXPos[index] += rocketXSpeed;
        }
        rocketYPos += rocketYSpeed;
        
        if(rocketYPos >= getHeight2())
        {
            rocketYSpeed = 0;
            rocketYPos = getHeight2();
        }
        
        if(rocketYPos <= 0)
        {
            rocketYSpeed = 0;
            rocketYPos = 0;
        }
        for(int index = 0; index < numStars; index++)
        {
            if(starXPos[index] <= -10)
            {
                starXPos[index] = getWidth2() + 10;
                starYPos[index] = getYNormal((int) (Math.random()*getHeight2()));
            }
            else if(starXPos[index] >= getWidth2() + 10)
            {
                starXPos[index] = -10;
                starYPos[index] = getYNormal((int) (Math.random()*getHeight2()));
            }
        }
        
//         for(int index = 0; index < numStars; index++)
//        {
//            for(int index2 = 0; index2 < numMissles; index2++)
//            {
//                if(starXPos[index] -20 < missleXPos[index2] &&
//                   starXPos[index] +20 > missleXPos[index2] &&
//                   starYPos[index] -20 < missleYPos[index2] &&
//                   starYPos[index] +20 > missleYPos[index2])
//                {
//                    missleVis[index2] = false;
//                    starVis[index] = false;
//                }
//                
//            }
//        }
         

         
//         for(int index = 0; index < numMissles; index++)
//         {
//             if(missleVis[index])
//             {
//                 if(missleRight)
//                 {
//                     missleXPos[index] += 10;
//                 }
//                 else
//                 {
//                 missleXPos[index] -= 10;
//                 }
//             }
//         }
         
         

    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}

class Missle
{
    public static int current;
    public static final int numMissles = 30;
    public int xpos;
    public int ypos;
    public boolean right;
    public boolean active;
    Missle()
    {
        active = false;
    }
}