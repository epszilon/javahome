package lifegame;

import java.util.List;
import java.util.ArrayList;
import org.joda.time.Instant;
import org.joda.time.Duration;

public class Program
{
    static int MAX_GENERATION_COUNT = 100;
    static int MAX_GAME_COUNT = 1;

    public static void main(String[] args)
    {
        for(int i = 0; i<MAX_GAME_COUNT; i++) 
        {
            PerformLiveGame();
        }
    }
    
    public static void PerformLiveGame()
    {
        try
        {
            Instant oldTime = new Instant();
            List<LifeGame> generations = new ArrayList<>();


            LifeGame lifeGame = new LifeGame(1000, 1000);
            //lifeGame.saveSpace("c:/temp/lifegame.txt");
            //LifeGame lifeGame = new LifeGame("c:/temp/lifegame.txt");
            // LifeGame lifeGame = new LifeGame("c:/temp/lifegame_1000th-694.txt");

            GenerationState generationState = GenerationState.Healthy;
            for (int i = 0; i < MAX_GENERATION_COUNT; i++)
            {
                lifeGame.evaluate();
                if (lifeGame.isOversized() == true)
                {
                    System.out.println("This generation is oversized.");
                    generationState = GenerationState.Oversized;
                } 
                else if (lifeGame.isEmpty() == true)
                {
                    System.out.println("This generation is empty.");
                    generationState = GenerationState.Empty;
                }
                else
                {
                    for (int g = 0; g < generations.size(); g++)
                    {
                        LifeGame lg = generations.get(g);
                        if (lifeGame.equals(lg))
                        {
                            System.out.println("Repeated generation: " + g + ".." + (generations.size()+1));
                            generationState = GenerationState.Repeated;
                            break;
                        }
                    }
                    generations.add(new LifeGame(lifeGame));
                }

                if (generationState != GenerationState.Healthy)
                {
                    break;
                }
            }
            // lifeGame.saveSpace("c:/temp/lifegame_1000th-694.1000th.txt");
            Duration duration = new Duration(oldTime, new Instant());
            System.out.println("generation state: " + generationState + ", duration: " + duration.getMillis() + "ms");
            if (generationState == GenerationState.Healthy || generationState == GenerationState.Repeated)
            {
                for(int g = 0; g<generations.size(); g++)
                {
                    LifeGame clone = (LifeGame) generations.get(g);
                    clone.displaySpace("Generation index:" + clone.GenerationIndex, DisplayType.ReducedTable);
                    
                }
            }
            
        }
        catch (Exception exc)
        {
            System.out.println("Error:\n" + exc.getMessage());
        }
    }

}
