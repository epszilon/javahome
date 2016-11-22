package lifegame;

import java.util.List;
import java.util.ArrayList;
import org.joda.time.Instant;
import org.joda.time.Duration;


public class Program
{

    public static void main(String[] args)
    {
        try
        {
            Instant oldTime = new Instant();
            
            
            List<LifeGame> generations = new ArrayList<>();
                    
            
            LifeGame lifeGame = new LifeGame(300, 300);
            //lifeGame.saveSpace("c:/temp/lifegame.txt");
            // LifeGame lifeGame = new LifeGame("c:/temp/lifegame.txt");
            // LifeGame lifeGame = new LifeGame("c:/temp/lifegame_1000th-694.txt");
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
                
//                // System.out.println("generations size:" + generations.size());
                System.out.println("current lifeGame:" + lifeGame);
                
                
                boolean cycle = false;
                for(int j = 0; j<generations.size(); j++){
                    LifeGame lg = generations.get(j);
                    if(lifeGame.equals(lg))
                    {
                        System.out.println("Repeated generation :" + j);
                        cycle = true;
                        break;
                    }
                }
                
                generations.add((LifeGame)lifeGame.clone());
                
                
                
                if(cycle == true){
                    break;
                }
                
                
                // Thread.sleep(1000);
            }
            lifeGame.saveSpace("c:/temp/lifegame_1000th-694.1000th.txt");
            
            Duration duration = new Duration(oldTime, new Instant());        
            System.out.println("duration: " + duration.getMillis() + "ms");
            
        } catch (Exception exc)
        {
            System.out.println("Error:\n" + exc.getMessage());
        }
    }    
    
}
