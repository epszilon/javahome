package lifegame;

import org.joda.time.Instant;
import org.joda.time.Duration;


public class Program
{

    public static void main(String[] args)
    {
        try
        {
            Instant oldTime = new Instant();
            
            
            LifeGame lifeGame = new LifeGame(100, 100);
            //lifeGame.saveSpace("c:/temp/lifegame.txt");
            // LifeGame lifeGame = new LifeGame("c:/temp/lifegame.txt");
            // lifeGame.displaySpace("generation " + 0);

            for (int i = 0; i < 1000; i++)
            {
                lifeGame.evaluate();
                lifeGame.displaySpace("generation " + (i + 1));
                System.out.println("generation:" + (i + 1) + " living:" + lifeGame.getLivingCount());
                // if (lifeGame.isViable() == false)
                if (lifeGame.isOversized() == true)
                {
                    System.out.println("This generation is oversized.");
                    break;
                }
                if (lifeGame.isEmpty() == true)
                {
                    System.out.println("This generation is empty.");
                    break;
                }
                // Thread.sleep(1000);
            }
            
            Duration duration = new Duration(oldTime, new Instant());        
            System.out.println("duration: " + duration.getMillis() + "ms");
            
        } catch (Exception exc)
        {
            System.out.println("Error:\n" + exc.getMessage());
        }
    }    
    
}
