package lifegame;

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

public class LifeGame
{

    int SizeX = 20;
    int SizeY = 10;
    SpaceType[][] Space;

    public LifeGame()
    {
        Space = CreateNewSpace();
        int centerX = SizeX / 2;
        int centerY = SizeY / 2;
        Space[centerX - 1][centerY] = SpaceType.Live;
        Space[centerX][centerY] = SpaceType.Live;
        Space[centerX + 1][centerY] = SpaceType.Live;

        Space[centerX - 2][centerY + 1] = SpaceType.Live;
        Space[centerX - 1][centerY + 1] = SpaceType.Live;
        Space[centerX][centerY + 1] = SpaceType.Live;

        // Space[centerX][centerY + 1] = SpaceType.Live;
        //Space[centerX+1][centerY+1] = SpaceType.Live;       
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

    private void DisplaySpace(String message)
    {
        System.out.println(message);
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

    private int GetNeighborCount(int posX, int posY)
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

    public void Evaluate()
    {
        for (int y = 1; y < SizeY - 1; y++)
        {
            for (int x = 1; x < SizeX - 1; x++)
            {
                int neighborCount = GetNeighborCount(x, y);
                // kihalók megkeresése
                if (Space[x][y] == SpaceType.Live && (neighborCount <= 1 || neighborCount >= 4))
                {
                    Space[x][y] = SpaceType.Dying;
                }
            }
        }
        //DisplaySpace("kihatók megjelölve");

        for (int y = 1; y < SizeY - 1; y++)
        {
            for (int x = 1; x < SizeX - 1; x++)
            {
                int neighborCount = GetNeighborCount(x, y);
                // szüleők megkeresése
                if (Space[x][y] == SpaceType.Empty && neighborCount == 3)
                {
                    Space[x][y] = SpaceType.Born;
                }
            }
        }
        //DisplaySpace("születettek megjelölése");

        for (int y = 1; y < SizeY - 1; y++)
        {
            for (int x = 1; x < SizeX - 1; x++)
            {
                int neighborCount = GetNeighborCount(x, y);
                // kihalók kihalása
                if (Space[x][y] == SpaceType.Dying)
                {
                    Space[x][y] = SpaceType.Empty;
                }
            }
        }
        //DisplaySpace("kihalás");

        for (int y = 1; y < SizeY - 1; y++)
        {
            for (int x = 1; x < SizeX - 1; x++)
            {
                int neighborCount = GetNeighborCount(x, y);
                // kihalók kihalása
                if (Space[x][y] == SpaceType.Born)
                {
                    Space[x][y] = SpaceType.Live;
                }
            }
        }
        //DisplaySpace("születés");

    }

    public static void main(String[] args)
    {
        try
        {
            LifeGame lifeGame = new LifeGame();
            lifeGame.DisplaySpace("generation " + 0);

            for (int i = 0; i < 100; i++)
            {
                lifeGame.Evaluate();
                lifeGame.DisplaySpace("generation " + (i + 1));
                if (lifeGame.isViable() == false)
                {
                    System.out.println("This generation isn't viable.");
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception exc) {
            System.out.println("Error:\n" +  exc.getMessage());
        }
    }
}
