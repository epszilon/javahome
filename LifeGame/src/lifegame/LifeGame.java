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

public class LifeGame
{

    static DisplayType display = DisplayType.Message;

    int SizeX = 1000;
    int SizeY = 1000;

    int activeSizeMinX;
    int activeSizeMaxX;
    int activeSizeMinY;
    int activeSizeMaxY;

    SpaceType[][] Space;

    public LifeGame()
    {
        Space = CreateNewSpace();
        init();
    }

    public LifeGame(String filePath)
    {
        readSpace(filePath);
    }
    
    private void init()
    {
        int centerX = SizeX / 2;
        int centerY = SizeY / 2;

        java.util.Random random = new java.util.Random();

        int initRadius = 25;
//        for (int i = 0; i < (2 * initRadius * 2 * initRadius) / 2; i++)
//        {
//            Space[centerX + random.nextInt(2 * initRadius) - initRadius][centerY + random.nextInt(2 * initRadius) - initRadius] = SpaceType.Live;
//        }

        for (int x = centerX - initRadius; x < centerX + initRadius; x++)
        {
            for (int y = centerY - initRadius; y < centerY + initRadius; y++)
            {
                // Space[centerX + random.nextInt(2 * initRadius) - initRadius][centerY + random.nextInt(2 * initRadius) - initRadius] = SpaceType.Live;
                if ((x + y) % 5 == 0 || (x - y) % 7 == 0 || x + y % 11 == 0 || x - y % 17 == 0 || x + y % 19 == 0 || x - y % 19 == 0)
                {
                    Space[centerX + random.nextInt(2 * initRadius) - initRadius][centerY + random.nextInt(2 * initRadius) - initRadius] = SpaceType.Live;
                }
            }
        }

//        Space[centerX - 1][centerY] = SpaceType.Live;
//        Space[centerX][centerY] = SpaceType.Live;
//        Space[centerX + 1][centerY] = SpaceType.Live;
//
//        Space[centerX - 2][centerY + 1] = SpaceType.Live;
//        Space[centerX - 1][centerY + 1] = SpaceType.Live;
//        Space[centerX][centerY + 1] = SpaceType.Live;
    }

    public int getLivingCount()
    {
        int livingCount = 0;
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
        return livingCount;
    }

    public boolean isEmpty()
    {
        for (int y = 0; y < SizeY; y++)
        {
            for (int x = 0; x < SizeX; x++)
            {
                if (Space[x][y] != SpaceType.Empty)
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isOversized()
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
                    } else if (Space[x][y] == SpaceType.Dying)
                    {
                        System.out.print("\u25CE");
                    } else if (Space[x][y] == SpaceType.Born)
                    {
                        System.out.print("\u25A1");
                    } else if (Space[x][y] == SpaceType.Empty)
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
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (writer != null)
                {
                    // Close the writer regardless of what happens...
                    writer.close();
                }
            } catch (Exception e)
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
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            } catch (Exception e)
            {
            }
        }

    }

    private int getNeighborCount(int posX, int posY)
    {
        int neighborCount = 0;
//        if(posX == 0 || posY == 0 || posX == SizeX -1 || posY == SizeY -1) 
//        {
//            throw new java.lang.IllegalStateException("Elértük a tábla szélét!");
//        }

        for (int y = posY - 1; y <= posY + 1; y++)
        {
            for (int x = posX - 1; x <= posX + 1; x++)
            {
                if (x != posX || y != posY)
                {
                    if (Space[x][y] == SpaceType.Live || Space[x][y] == SpaceType.Dying)
                    {
                        neighborCount++;
                    }
                }
            }
        }
        return neighborCount;
    }

    public void evaluate()
    {
        for (int y = activeSizeMinY; y < activeSizeMaxY; y++)
        {
            for (int x = activeSizeMinX; x < activeSizeMaxX; x++)
            {
                int neighborCount = getNeighborCount(x, y);
                // kihalók megkeresése
                if (Space[x][y] == SpaceType.Live && (neighborCount <= 1 || neighborCount >= 4))
                {
                    Space[x][y] = SpaceType.Dying;
                }
            }
        }
        //DisplaySpace("kihatók megjelölve");

        for (int y = activeSizeMinY; y < activeSizeMaxY; y++)
        {
            for (int x = activeSizeMinX; x < activeSizeMaxX; x++)
            {
                int neighborCount = getNeighborCount(x, y);
                // szüleők megkeresése
                if (Space[x][y] == SpaceType.Empty && neighborCount == 3)
                {
                    Space[x][y] = SpaceType.Born;
                }
            }
        }
        //DisplaySpace("születettek megjelölése");

        for (int y = activeSizeMinY; y < activeSizeMaxY; y++)
        {
            for (int x = activeSizeMinX; x < activeSizeMaxX; x++)
            {
                int neighborCount = getNeighborCount(x, y);
                // kihalók kihalása
                if (Space[x][y] == SpaceType.Dying)
                {
                    Space[x][y] = SpaceType.Empty;
                }
            }
        }
        //DisplaySpace("kihalás");

        for (int y = activeSizeMinY; y < activeSizeMaxY; y++)
        {
            for (int x = activeSizeMinX; x < activeSizeMaxX; x++)
            {
                int neighborCount = getNeighborCount(x, y);
                // kihalók kihalása
                if (Space[x][y] == SpaceType.Born)
                {
                    Space[x][y] = SpaceType.Live;
                }
            }
        }
        //DisplaySpace("születés");

        activeSizeMinX = SizeX;
        activeSizeMinY = SizeY;
        activeSizeMaxX = 0;
        activeSizeMaxY = 0;

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
                    } else if (activeSizeMaxX < x)
                    {
                        activeSizeMaxX = x;
                    }
                    if (activeSizeMinY > y)
                    {
                        activeSizeMinY = y;
                    } else if (activeSizeMaxY < y)
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

        System.out.println("(" + activeSizeMinX + "," + activeSizeMinY + ") (" + activeSizeMaxX + "," + activeSizeMaxY + ")");

//        activeSizeMinX = 1;
//        activeSizeMinY = 1;
//        activeSizeMaxX = SizeX - 1;
//        activeSizeMaxY = SizeY - 1;

    }
}
