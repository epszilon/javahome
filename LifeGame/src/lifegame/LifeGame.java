package lifegame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

enum SpaceType
{
    /**
     * Empty position.
     */
    Empty,
    Live,
    Dying,// Dead,
    Born
}

enum DisplayType
{
    No,
    Message,
    Table,
    ReducedTable,
    Full
}

enum GenerationState 
{
    Empty,
    Healthy,
    Oversized,
    Repeated,
} 

public class LifeGame // implements Cloneable
{
    int GenerationIndex = 0;
    
    int SizeX;
    int SizeY;

    int activeSizeMinX;
    int activeSizeMaxX;
    int activeSizeMinY;
    int activeSizeMaxY;
    public int livingCount = -1;

    SpaceType[][] Space;


    public LifeGame(LifeGame lifeGame) 
    {
        this.SizeX = lifeGame.SizeX;
        this.SizeY = lifeGame.SizeY;
        // this.Space = CreateNewSpace();
        this.Space = new SpaceType[SizeX][SizeY];

        for (int y = 0; y < SizeY; y++)
        {
            for (int x = 0; x < SizeX; x++)
            {
                this.Space [x][y] = lifeGame.Space[x][y];
            }
        }
       
        
        // System.arraycopy(lifeGame.Space, 0, this.Space, 0, lifeGame.Space.length);
        // System.arraycopy(lifeGame.Space, 0, this.Space, 0, lifeGame.Space.length);
        
        
        this.GenerationIndex = lifeGame.GenerationIndex;
        this.activeSizeMinX = lifeGame.activeSizeMinX;
        this.activeSizeMinY = lifeGame.activeSizeMinY;
        this.activeSizeMaxX = lifeGame.activeSizeMaxX;
        this.activeSizeMaxY = lifeGame.activeSizeMaxY;
        this.livingCount = lifeGame.livingCount;
        
        //displaySpace("from the copy constructor", DisplayType.ReducedTable);
        
    }
    
    
    
    public LifeGame(int sizeX, int sizeY)
    {
        this.SizeX = sizeX;
        this.SizeY = sizeY;
        this.GenerationIndex = 0;
        Space = CreateNewSpace();
        // init(SizeX / 40, 2 * (SizeX / 40) * (SizeX / 40));
        init(3, 5);
        calculateActualSize();
    }

    public LifeGame(String filePath)
    {
        readSpace(filePath);
        calculateActualSize();
        this.GenerationIndex = 0;
    }

//    public Object clone()
//    {
//        LifeGame clone = new LifeGame(this);
//        displaySpace("from the clone()", DisplayType.ReducedTable);
//        return clone;
//    }

    private void init(int initWidth, int count)
    {
        int centerX = SizeX / 2;
        int centerY = SizeY / 2;

        java.util.Random random = new java.util.Random();

//        Space[centerX-1][centerY] = SpaceType.Live;
//        Space[centerX][centerY] = SpaceType.Live;
//        Space[centerX+1][centerY] = SpaceType.Live;
//
//        Space[centerX-1][centerY+6] = SpaceType.Live;
//        Space[centerX][centerY+6] = SpaceType.Live;
//        Space[centerX+1][centerY+6] = SpaceType.Live;
//
//
//        Space[centerX+3][centerY-1+3] = SpaceType.Live;
//        Space[centerX+3][centerY+3] = SpaceType.Live;
//        Space[centerX+3][centerY+1+3] = SpaceType.Live;
//
//        Space[centerX-3][centerY-1+3] = SpaceType.Live;
//        Space[centerX-3][centerY+3] = SpaceType.Live;
//        Space[centerX-3][centerY+1+3] = SpaceType.Live;



//        Space[centerX-1][centerY+0] = SpaceType.Live;
//        Space[centerX-1][centerY+1] = SpaceType.Live;
//        Space[centerX+1][centerY+0] = SpaceType.Live;
//        Space[centerX+1][centerY+1] = SpaceType.Live;
//        Space[centerX+0][centerY-1] = SpaceType.Live;
//        Space[centerX+0][centerY+2] = SpaceType.Live;




        
        for (int i = 0; i < count; i++)
        {
            Space[centerX + random.nextInt(initWidth) - initWidth/2][centerY + random.nextInt(initWidth) - initWidth/2] = SpaceType.Live;
        }
        
        activeSizeMinX = 0;
        activeSizeMinY = 0;
        activeSizeMaxX = SizeX - 1;
        activeSizeMaxY = SizeY - 1;
        
    }

    public int getLivingCount()
    {
        if (livingCount == -1)
        {
            livingCount = 0;
            for (int y = 0; y < SizeY; y++)
            {
                for (int x = 0; x < SizeX; x++)
                {
                    if (Space[x][y] == SpaceType.Live)
                    {
                        livingCount++;
                    }
                }
            }
        }
        return livingCount;
    }

    public boolean isEmpty()
    {
        return getLivingCount() == 0;
//        for (int y = activeSizeMinY; y < activeSizeMaxY; y++)
//        {
//            for (int x = activeSizeMinX; x < activeSizeMaxX; x++)
//            {
//                if (Space[x][y] != SpaceType.Empty)
//                {
//                    return false;
//                }
//            }
//        }
//        return true;
    }

    public boolean isOversized()
    {
        if (activeSizeMinX > 1 && activeSizeMinY > 1 && activeSizeMaxX < SizeX - 1 && activeSizeMaxY < SizeY - 1)
        {
            return false;
        }
        else
        {
            for (int y = 0; y < SizeY; y++)
            {
                if (Space[0][y] != SpaceType.Empty || Space[SizeX - 1][y] != SpaceType.Empty)
                {
                    return true;
                }
            }

            for (int x = 0; x < SizeX; x++)
            {
                if (Space[x][0] != SpaceType.Empty || Space[x][SizeY - 1] != SpaceType.Empty)
                {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isViable()
    {
        return isEmpty() == false && isOversized() == false;
    }

    private SpaceType[][] CreateNewSpace()
    {
        SpaceType[][] space = new SpaceType[SizeX][SizeY];

        for (int y = 0; y < SizeY; y++)
        {
            for (int x = 0; x < SizeX; x++)
            {
                space[x][y] = SpaceType.Empty;
            }
        }

        activeSizeMinX = 1;
        activeSizeMinY = 1;
        activeSizeMaxX = SizeX - 2;
        activeSizeMaxY = SizeY - 2;

        return space;
    }

    public void displaySpace(String message, DisplayType display)
    {
        if (message != null && message.length() > 0 && display != DisplayType.No)
        {
            System.out.println(message);
        }
        
        
        if (display == DisplayType.Full || display == DisplayType.ReducedTable)
        {
            int startX = 0;
            int startY = 0;
            int endX = SizeX;
            int endY = SizeY;
            if(display ==  DisplayType.ReducedTable) 
            {
                startX = activeSizeMinX;
                startY = activeSizeMinY;
                endX = activeSizeMaxX;
                endY = activeSizeMaxY;
            }
            
            for (int y = startY; y < endY; y++)
            {
                for (int x = startX; x < endX; x++)
                {
                    switch(Space[x][y])
                    {
                        case Live:
                        System.out.print("\u25CF");
                        break;
                        case Dying:
                        System.out.print("\u25CE");
                        break;
                        case Born:
                        System.out.print("\u25A1");
                        break;
                        case Empty:
                        System.out.print("\u25CB");
                        break;
                        default:
                        System.out.print("?");
                        break;
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public void saveSpace(String filePath)
    {
        BufferedWriter writer = null;
        try
        {
            //create a temporary file
            writer = new BufferedWriter(new FileWriter(new File(filePath)));
            writer.write(SizeX + "," + SizeY);
            writer.newLine();

            for (int y = 0; y < SizeY; y++)
            {
                for (int x = 0; x < SizeX; x++)
                {
                    switch (Space[x][y])
                    {
                        case Empty:
                            writer.write("O");
                            break;
                        case Live:
                            writer.write("X");
                            break;
                        default:
                            writer.write(" ");
                    }
                }
                writer.newLine();
            }
            System.out.println();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    // Close the writer regardless of what happens...
                    writer.close();
                }
            }
            catch (Exception e)
            {
            }
        }

    }

    private void readSpace(String filePath)
    {
        BufferedReader reader = null;
        try
        {
            //create a temporary file
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            String[] dimensions = line.split(",");
            this.SizeX = Integer.parseInt(dimensions[0]);
            this.SizeY = Integer.parseInt(dimensions[1]);
            this.Space = new SpaceType[SizeX][SizeY];

            for (int y = 0; y < SizeY; y++)
            {
                line = reader.readLine();
                for (int x = 0; x < SizeX; x++)
                {
                    char c = line.charAt(x);
                    switch (c)
                    {
                        case 'O':
                            Space[x][y] = SpaceType.Empty;
                            break;
                        case 'X':
                            Space[x][y] = SpaceType.Live;
                            break;
                        default:
                            throw new Exception("Unexpected char '" + c + "' found in the input file '" + filePath + "'!");

                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (Exception e)
            {
            }
        }

        activeSizeMinX = 1;
        activeSizeMinY = 1;
        activeSizeMaxX = SizeX - 2;
        activeSizeMaxY = SizeY - 2;
    }

    private int getNeighborCount(int posX, int posY)
    {
        int neighborCount = 0;

        for (int y = posY - 1; y <= posY + 1; y++)
        {
            for (int x = posX - 1; x <= posX + 1; x++)
            {
                if (Space[x][y] == SpaceType.Live || Space[x][y] == SpaceType.Dying)
                {
                    neighborCount++;
                }
            }
        }

        if (Space[posX][posY] == SpaceType.Live || Space[posX][posY] == SpaceType.Dying)
        {
            neighborCount--;
        }

        return neighborCount;
    }

    public void evaluate()
    {
        livingCount = 0;
        for (int y = activeSizeMinY; y < activeSizeMaxY; y++)
        {
            for (int x = activeSizeMinX; x < activeSizeMaxX; x++)
            {
                int neighborCount = getNeighborCount(x, y);
                SpaceType current = Space[x][y];
                if (current == SpaceType.Live)
                {
                    if (neighborCount <= 1 || neighborCount >= 4)
                    {
                        Space[x][y] = SpaceType.Dying;
                    }
                    else
                    {
                        livingCount++;
                    }
                }
                else if (current == SpaceType.Empty && neighborCount == 3)
                {
                    Space[x][y] = SpaceType.Born;
                    livingCount++;
                }
            }
        }
        //DisplaySpace("kihatók megjelölve");
        //DisplaySpace("születettek megjelölése");
        for (int y = activeSizeMinY; y < activeSizeMaxY; y++)
        {
            for (int x = activeSizeMinX; x < activeSizeMaxX; x++)
            {
                // int neighborCount = getNeighborCount(x, y);
                // kihalók kihalása
                SpaceType current = Space[x][y];
                if (current == SpaceType.Dying)
                {
                    Space[x][y] = SpaceType.Empty;
                }

                if (current == SpaceType.Born)
                {
                    Space[x][y] = SpaceType.Live;
                }

            }
        }
        calculateActualSize();
        this.GenerationIndex++;
    }

    public final void calculateActualSize()
    {
        activeSizeMinX = Math.max(1, activeSizeMinX - 1);
        activeSizeMinY = Math.max(1, activeSizeMinY - 1);
        activeSizeMaxX = Math.min(SizeX - 1, activeSizeMaxX + 1);
        activeSizeMaxY = Math.min(SizeY - 1, activeSizeMaxY + 1);

        int newSizeMinX = SizeX - 1;
        int newSizeMinY = SizeY - 1;
        int newSizeMaxX = 1;
        int newSizeMaxY = 1;
        
        // calculate the new active area
        for (int y = activeSizeMinY; y < activeSizeMaxY - 1; y++)
        {
            for (int x = activeSizeMinX; x < activeSizeMaxX - 1; x++)
            {
                if (Space[x][y] == SpaceType.Live)
                {
                    if (newSizeMinX > x)
                    {
                        newSizeMinX = x;
                    }
                    else if (newSizeMaxX < x)
                    {
                        newSizeMaxX = x;
                    }
                    if (newSizeMinY > y)
                    {
                        newSizeMinY = y;
                    }
                    else if (newSizeMaxY < y)
                    {
                        newSizeMaxY = y;
                    }
                }
            }
        }

        activeSizeMinX = Math.max(1, newSizeMinX - 1);
        activeSizeMinY = Math.max(1, newSizeMinY - 1);
        activeSizeMaxX = Math.min(SizeX - 1, newSizeMaxX + 2);
        activeSizeMaxY = Math.min(SizeY - 1, newSizeMaxY + 2);
        // System.out.println("calculate (" + activeSizeMinX + "," + activeSizeMinY + ") (" + activeSizeMaxX + "," + activeSizeMaxY + ")");
    }

    @Override
    public boolean equals(Object obj)
    {
        LifeGame lifeGame = (LifeGame) obj;
        if (this.livingCount != lifeGame.livingCount
                || this.activeSizeMinX != lifeGame.activeSizeMinX || this.activeSizeMinY != lifeGame.activeSizeMinY
                || this.activeSizeMaxX != lifeGame.activeSizeMaxX || this.activeSizeMaxY != lifeGame.activeSizeMaxY)
        {
            //System.out.println("not equals 1" + (this.livingCount != lifeGame.livingCount));
            return false;
        }

        for (int y = this.activeSizeMinY; y < this.activeSizeMaxY; y++)
        {
            for (int x = this.activeSizeMinX; x < this.activeSizeMaxX; x++)
            {
                if (this.Space[x][y] != lifeGame.Space[x][y])
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override 
    public String toString() 
    {
        return "generation:" + this.GenerationIndex + " living:" + this.livingCount + " active area:((" + this.activeSizeMinX + " ," +  this.activeSizeMinY + "),(" + this.activeSizeMaxX + "," + this.activeSizeMaxY + "))";
    }

}
