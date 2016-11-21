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
    Full
}

public class LifeGame implements Cloneable
{

    static DisplayType display = DisplayType.No;

    int SizeX;
    int SizeY;

    int activeSizeMinX;
    int activeSizeMaxX;
    int activeSizeMinY;
    int activeSizeMaxY;
    public int livingCount = -1;

    SpaceType[][] Space;

    public LifeGame(int sizeX, int sizeY)
    {
        this.SizeX = sizeX;
        this.SizeY = sizeY;
        Space = CreateNewSpace();
        // init(SizeX / 40, 2 * (SizeX / 40) * (SizeX / 40));
        init(4, 6);
        calculateActualSize();
    }

    public LifeGame(String filePath)
    {
        readSpace(filePath);
        calculateActualSize();
    }

    public Object clone()
    {
        LifeGame clone = new LifeGame(SizeX, SizeY);
        clone.Space = new SpaceType[SizeX][SizeY];
        System.arraycopy(Space, 0, clone.Space, 0, Space.length);
        clone.activeSizeMinX = activeSizeMinX;
        clone.activeSizeMaxX = activeSizeMaxX;
        clone.activeSizeMaxX = activeSizeMaxX;
        clone.activeSizeMaxY = activeSizeMaxY;
        clone.livingCount = livingCount;
        return clone;
    }

    private void init(int initRadius, int count)
    {
        int centerX = SizeX / 2;
        int centerY = SizeY / 2;

        java.util.Random random = new java.util.Random();

        for (int i = 0; i < count; i++)
        {
            Space[centerX + random.nextInt(2 * initRadius) - initRadius][centerY + random.nextInt(2 * initRadius) - initRadius] = SpaceType.Live;
        }
    }

//    private void init(int initRadius, int count)
//    {
//        int centerX = SizeX / 2;
//        int centerY = SizeY / 2;
//
//        java.util.Random random = new java.util.Random();
//
//        for (int i = 0; i < (2 * initRadius * 2 * initRadius) / 2; i++)
//        {
//            Space[centerX + random.nextInt(2 * initRadius) - initRadius][centerY + random.nextInt(2 * initRadius) - initRadius] = SpaceType.Live;
//        }
//    }
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

        activeSizeMinX = SizeX - 1;
        activeSizeMinY = SizeY - 1;
        activeSizeMaxX = 0;
        activeSizeMaxY = 0;

        return space;
    }

    public void displaySpace(String message)
    {
        if (display == DisplayType.Message || display == DisplayType.Full)
        {
            System.out.println(message);
        }

        if (display == DisplayType.Full)
        {

            for (int y = 0; y < SizeY; y++)
            {
                for (int x = 0; x < SizeX; x++)
                {
                    if (Space[x][y] == SpaceType.Live)
                    {
                        System.out.print("\u25CF");
                    }
                    else if (Space[x][y] == SpaceType.Dying)
                    {
                        System.out.print("\u25CE");
                    }
                    else if (Space[x][y] == SpaceType.Born)
                    {
                        System.out.print("\u25A1");
                    }
                    else if (Space[x][y] == SpaceType.Empty)
                    {
                        System.out.print("\u25CB");
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

        activeSizeMinX = SizeX - 1;
        activeSizeMinY = SizeY - 1;
        activeSizeMaxX = 0;
        activeSizeMaxY = 0;
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
    }

    public final void calculateActualSize()
    {
        activeSizeMinX = Math.max(1, activeSizeMinX - 1);
        activeSizeMinY = Math.max(1, activeSizeMinY - 1);
        activeSizeMaxX = Math.min(SizeX - 1, activeSizeMaxX + 1);
        activeSizeMaxY = Math.min(SizeY - 1, activeSizeMaxY + 1);

        // calculate the new active area
        for (int y = 1; y < SizeY - 1; y++)
        {
            for (int x = 1; x < SizeX - 1; x++)
            {
                if (Space[x][y] == SpaceType.Live)
                {
                    if (activeSizeMinX > x)
                    {
                        activeSizeMinX = x;
                    }
                    else if (activeSizeMaxX < x)
                    {
                        activeSizeMaxX = x;
                    }
                    if (activeSizeMinY > y)
                    {
                        activeSizeMinY = y;
                    }
                    else if (activeSizeMaxY < y)
                    {
                        activeSizeMaxY = y;
                    }
                }
            }
        }

        activeSizeMinX = Math.max(1, activeSizeMinX - 1);
        activeSizeMinY = Math.max(1, activeSizeMinY - 1);
        activeSizeMaxX = Math.min(SizeX - 1, activeSizeMaxX + 2);
        activeSizeMaxY = Math.min(SizeY - 1, activeSizeMaxY + 2);
        //System.out.println("(" + activeSizeMinX + "," + activeSizeMinY + ") (" + activeSizeMaxX + "," + activeSizeMaxY + ")");
    }

//    @Override
//    public int hashCode() 
//    {
//    }
    @Override
    public boolean equals(Object obj)
    {
        LifeGame lifeGame = (LifeGame) obj;
        if (this.livingCount != lifeGame.livingCount
                || this.activeSizeMinX != lifeGame.activeSizeMinX || this.activeSizeMinY != lifeGame.activeSizeMinY
                || this.activeSizeMaxX != lifeGame.activeSizeMaxX || this.activeSizeMaxY != lifeGame.activeSizeMaxY)
        {
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

}
