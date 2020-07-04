import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

class Position{
    int x, y;

    Position()
    {
        this.x=this.y=0;
    }
    public void setPos(int x, int y)
    {
        this.x=x;
        this.y=y;
    }
    public Position getPos()
    {
        return this;
    }
}

class Player
{
    char symbol='@';
    Position pos;

    Player()
    {
        pos=new Position();
    }
    public int getX()
    {
        return pos.x;
    }
    public int getY()
    {
        return pos.y;
    }
    @Override
    public String toString()
    {
        return new StringBuilder().append(symbol).append(" ").append(pos.x).append(" ").append(pos.y).toString();
    }
}

class Vampire{
    char symbol='V';
    Position pos;

    Vampire(int xx, int yy)
    {
        pos=new Position();
        pos.setPos(xx,yy);
    }
    public int getX()
    {
        return pos.x;
    }
    public int getY()
    {
        return pos.y;
    }
    @Override
    public String toString()
    {
        return new StringBuilder().append(this.symbol).append(" ").append(pos.x).append(" ").append(pos.y).toString();
    }
}
////////////////////////////////////////////////////////////////////////////////////
class gameBoard{
    static int l, w;
    gameBoard(int x,int y)
    {
        this.l=y;
        this.w=x;
    }

    public void setBoard(int x, int y)
    {
        this.l=y;
        this.w=x;
    }
    public void DRAW(State st)
    {
        StringBuilder[] level=new StringBuilder[l];
        for(int i=0; i<l; i++)
        {
            level[i]=new StringBuilder();
            for(int j=0; j < w; j++)
            {
                level[i].append(".");
            }
            level[i].append('\n');
        }
        ///////////// PLAYER //////////////////
        level[st.x.getY()].setCharAt(st.x.getX(),st.x.symbol);
        //////////// VAMPIRE /////////////////
        for(Vampire vc:st.v)
            level[vc.getY()].setCharAt(vc.getX(),vc.symbol);

        for(StringBuilder x: level)
        {
            System.out.println(x.toString());
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////
class move{
    static Position GETNEWPOS(String str)
    {
        Position acc=new Position();
        for(char x: str.toCharArray())
        {
            if(x == 'w') acc.y-=1;
            else if(x == 's') acc.y+=1;
            else if(x == 'a') acc.x-=1;
            else if(x == 'd') acc.x+=1;
        }
        return acc;
    }
    static void generateVampMoves(List<Vampire> v)
    {
        int x=0, y=0;
        for(Vampire w:v)
        {
            x= ThreadLocalRandom.current().nextInt(0, gameBoard.w);
            y= ThreadLocalRandom.current().nextInt(0, gameBoard.l);
            w.pos.setPos(x,y);
        }
    }
}

class State{
    Player x;
    List<Vampire> v;
    int steps;
    boolean win;

    State(int n)
    {
        x=new Player();
        steps=n*2+4;
        v=new ArrayList<>(n);
        for (int i = 0; i < n; i++) v.add(new Vampire(0,0));
        move.generateVampMoves(this.v);
        win=false;
    }
    public boolean Update(String moves)
    {
        if(steps > 0)
        {
            --steps;
            Position newPos=move.GETNEWPOS(moves);
            newPos.setPos(x.getX()+newPos.x,x.getY()+newPos.y);
            newPos=CHECK(newPos);
            x.pos.setPos(newPos.x,newPos.y);
            move.generateVampMoves(this.v);
            if(v.size() == 0)
            {
                win=true;
                return false;
            }

            return true;
        }
        return false;
    }
    public Position CHECK(Position p)
    {
        if( p.getPos().x < 0 || p.getPos().x > gameBoard.w || p.getPos().y < 0 || p.getPos().y > gameBoard.l)
            return new Position();
        int x=p.getPos().x, y=p.getPos().y;
        List<Vampire> toRemove=new ArrayList<>();
        for(Vampire vx:v)
        {
            if(vx.getY() == y || vx.getY()-1 == y || vx.getY()+1 == y)
            {
                if(vx.getX() == x || vx.getX()-1 == x || vx.getX()+1 == x)
                    toRemove.add(vx);
            }

        }
        v.removeAll(toRemove);
        return p;
    }
}
////////////////////////////////////////////////////////////////////////////////////
public class Dungeon {
    State est;
    gameBoard gb;

    Dungeon(int n, int x, int y)
    {
        gb=new gameBoard(x,y);
        est=new State(n);
    }

    public void run() throws IOException {
        gb.DRAW(est);
        BufferedReader bf=new BufferedReader(new InputStreamReader(System.in));
        String cmd="";

        while(true)
        {
            System.out.println("INPUT COMMAND: ");
            cmd=bf.readLine();
            System.out.println();
            if(!est.Update(cmd)) break;
            gb.DRAW(est);
            System.out.println("TURNS: "+est.steps);
            System.out.println(est.x);
            for(Vampire x:est.v)System.out.println(x);

        }
        if(est.win) System.out.println("Y O U W O N ! !");
        else System.out.println("Y O U L O S T");
    }
}

class Main{
    public static void main(String[] arg) throws IOException
    {
        Scanner scn=new Scanner(System.in);
        int l, w;
        System.out.println("Input width");
        w=scn.nextInt();
        System.out.println("Input length");
        l=scn.nextInt();

        Dungeon dg=new Dungeon(4,w,l);
        dg.run();

    }
}