
import java.awt.* ;
import java.awt.event.* ;

import javax.swing.* ;

import java.awt.image.BufferedImage ;
import java.awt.image.PixelGrabber ;
import java.awt.image.DataBufferInt ;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random ;
import java.awt.image.BufferStrategy ;

public class MainStats extends JFrame
implements ActionListener,MouseListener, KeyListener, MouseMotionListener
{
  private Container pane ;
  BufferedImage display;
  DataBufferInt databuffer ;
  int displaypixels[];
  static int w=1920,h=1080;
  BufferStrategy strategy ;

  //Win rate relative to cards drawn
  HashMap<String,int[]> card_draw_wins; // Key = "player position,card name,amount", value is {losses,wins}
  //Win rate relative to cards flipped on your turn
  HashMap<String,int[]> card_flip_wins; // Key = "player position,card name,amount", value is {losses,wins}
  
  int first_wins, first_losses ; // Wins and losses of the player who went first
  int player_wins[], player_losses[] ;// Wins and losses of each player.

  String player0name = "";
  String player1name = "";
  boolean display_samples = false; // whether number of samples is displayed (toggles with 's')
  int top_padding = 45;
  int left_padding = 200;
  int edge_padding = 10;
  int name_step = 20 ; // y step of names on left column
  int min_samples = 500; // Used for coloring bars to indicate satistical significance

  public HashMap<String,LazyImage> card_image;
  public String[] card_names = new String[]{
      ViralMarketing.VIRAL_MARKETING_NAME,
      Capital.CAPITAL_NAME,
      Profit.PROFIT_NAME,
      Sabotage.SABOTAGE_NAME,
      Breakthrough.BREAKTHROUGH_NAME,
      Pivot.PIVOT_NAME,
      Damages.DAMAGES_NAME,
      EpicFail.EPIC_FAIL_NAME,
      Planning.PLANNING_NAME,
      Funnel.FUNNEL_NAME,
      Delay.DELAY_NAME,
      PressRelease.PRESS_RELEASE_NAME,
      Scandal.SCANDAL_NAME,
      PublicityStunt.PUBLICITY_STUNT_NAME,
      Spinoff.SPINOFF_NAME,
      Poach.POACH_NAME,
      Rush.RUSH_NAME,
      Lawyers.LAWYERS_NAME,
      Documentation.DOCUMENTATION_NAME,
      Taxes.TAXES_NAME,
      OpenSource.OPEN_SOURCE_NAME,
      Patent.PATENT_NAME,
      Boom.BOOM_NAME,
      Bust.BUST_NAME,
      Infamy.INFAMY_NAME,
      Underdog.UNDERDOG_NAME,
      Nonprofit.NONPROFIT_NAME,
      FlipMetric.YOUR_MONEY,
      FlipMetric.THEIR_MONEY
  };
  int viewing = 0 ;

  public void init()
  {
    pane = getContentPane();
    pane.addMouseListener(this);
    pane.addMouseMotionListener(this);
    pane.addKeyListener(this);
    pane.requestFocus();
    Timer clock = new Timer(10, this); 
    clock.start();

    card_image = Main.loadCardImages();
    calculateCardWinStats(100000);

  }
  
  public Player[] setUpPlayers(){
    Player[] player = new Player[2];
    player0name = "Default Basic Player";
    player[0] = new BasicPlayer((int)(Math.random()*Integer.MAX_VALUE));
    //((BasicPlayer)player[0]).only_spend_cards = true;
    player1name = "Also Basic Player";
    player[1] = new BasicPlayer((int)(Math.random()*Integer.MAX_VALUE));
    //((BasicPlayer)player[1]).only_spend_cards = true;
    return player;
  }

  // Returns a map from "CardName,amount" -> {losses, wins} for "games" played by AI
  public void calculateCardWinStats(int games){
    card_draw_wins = new HashMap<String, int[]>();
    card_flip_wins = new HashMap<String, int[]>();
    first_wins = 0 ;
    first_losses = 0;
    player_wins = new int[2];
    player_losses = new int[2];
    long total_run_time = 0 ;
    for(int q=0;q<games;q++){
      Player[] player = setUpPlayers();
      int seed = (int)(Math.random()*Integer.MAX_VALUE);
      System.out.println("Game " + q + " seed:" + seed);
      DrawMetric draws = new DrawMetric();
      FlipMetric flips = new FlipMetric();
      Deck main_deck = Deck.getCompetitiveMainDeck(2);
      Deck[] player_decks = new Deck[]{Deck.getCompetitivePlayerDeck(), Deck.getCompetitivePlayerDeck()};
      Game game = new Game(player, main_deck, player_decks, seed);
      int first_player = game.getTurn();
      game.enforce_hidden_information = false;
      game.attachMetric(draws);
      game.attachMetric(flips);
      long start_time = System.currentTimeMillis();
      game.run();
      total_run_time += System.currentTimeMillis() - start_time;
      int winner = Game.NOONE;
      double win_score = 0 ;
      for(int k=0;k<player.length;k++){
        double score = player[k].finalScore();
        if(score > win_score){
          win_score = score;
          winner = k;
        }
      }
      if(winner != Game.NOONE){ // Ignore ties
        int loser = 1-winner;
        player_wins[winner]++;
        player_losses[loser]++;
        if(winner == first_player){
          first_wins++;
        }else{
          first_losses++;
        }
        
        
        // Winner draws
        HashSet<String> had_card = new HashSet<String>();

        HashMap<String, Integer> win_map = draws.draws.get(winner);
        Iterator<String> wi = win_map.keySet().iterator();
        while(wi.hasNext()){
          String card = wi.next();
          had_card.add(card);
          String key = winner + "," + card + "," + win_map.get(card);
          if(card_draw_wins.containsKey(key)){
            card_draw_wins.get(key)[1]++;
          }else{
            card_draw_wins.put(key, new int[]{0,1});
          }
        }
        // Add every card the winner got none of
        for(int k = 0; k < card_names.length;k++){
          if(!had_card.contains(card_names[k])){
            String key = winner + "," + card_names[k] +",0";
            if(card_draw_wins.containsKey(key)){
              card_draw_wins.get(key)[1]++;
            }else{
              card_draw_wins.put(key, new int[]{0,1});
            }
          }
        }

        // Loser Draws
        HashMap<String, Integer> lose_map = draws.draws.get(loser);
        Iterator<String> li = lose_map.keySet().iterator();
        had_card = new HashSet<String>(); // Referesh for loser
        while(li.hasNext()){
          String card = li.next();
          had_card.add(card);
          String key = loser +"," + card + "," + lose_map.get(card);
          if(card_draw_wins.containsKey(key)){
            card_draw_wins.get(key)[0]++;
          }else{
            card_draw_wins.put(key, new int[]{1,0});
          }
        }
        // Add every card the loser got none of
        for(int k = 0; k < card_names.length;k++){
          if(!had_card.contains(card_names[k])){
            String key = loser + "," + card_names[k] +",0";
            if(card_draw_wins.containsKey(key)){
              card_draw_wins.get(key)[0]++;
            }else{
              card_draw_wins.put(key, new int[]{1,0});
            }
          }
        }
        
        // Winner Flips
        had_card = new HashSet<String>();

        win_map = flips.flips.get(winner);
        wi = win_map.keySet().iterator();
        while(wi.hasNext()){
          String card = wi.next();
          had_card.add(card);
          String key = winner + "," + card + "," + win_map.get(card);
          if(card_flip_wins.containsKey(key)){
            card_flip_wins.get(key)[1]++;
          }else{
            card_flip_wins.put(key, new int[]{0,1});
          }
        }
        // Add every card the winner got none of
        for(int k = 0; k < card_names.length;k++){
          if(!had_card.contains(card_names[k])){
            String key = winner + "," + card_names[k] +",0";
            if(card_flip_wins.containsKey(key)){
              card_flip_wins.get(key)[1]++;
            }else{
              card_flip_wins.put(key, new int[]{0,1});
            }
          }
        }
        
        // Loser Flips
        lose_map = flips.flips.get(loser);
        li = lose_map.keySet().iterator();
        had_card = new HashSet<String>(); // Refresh for loser
        while(li.hasNext()){
          String card = li.next();
          had_card.add(card);
          String key = loser +"," + card + "," + lose_map.get(card);
          if(card_flip_wins.containsKey(key)){
            card_flip_wins.get(key)[0]++;
          }else{
            card_flip_wins.put(key, new int[]{1,0});
          }
        }
        // Add every card the loser got none of
        for(int k = 0; k < card_names.length;k++){
          if(!had_card.contains(card_names[k])){
            String key = loser + "," + card_names[k] +",0";
            if(card_flip_wins.containsKey(key)){
              card_flip_wins.get(key)[0]++;
            }else{
              card_flip_wins.put(key, new int[]{1,0});
            }
          }
        }

      }
    }

    for(int k=0;k<player_wins.length;k++){
      System.out.println("Player " + k + " Wins:" + player_wins[k] +"  Losses:" + player_losses[k]);
    }
    System.out.println("First player wins:" + first_wins + " First player losses:" + first_losses +" ->  " +  (first_wins*10000l/(first_wins + first_losses)/100.0) +"%");
    System.out.println("Total runtime: " + (int)(total_run_time/1000) +"s  Average run time: " + ((int)(total_run_time / (double)games*100)/100.0) +"ms");
    
  }

  


  public void paint(Graphics g)
  {


    if(display==null){
      createBufferStrategy(2);
      strategy = getBufferStrategy();
      display = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
      databuffer = (DataBufferInt) display.getRaster().getDataBuffer();
      displaypixels = databuffer.getData();
    }

    Graphics g2 = strategy.getDrawGraphics();
    paint(displaypixels);
    g2.drawImage(display,0,0,this);
    paint2(g2);
    strategy.show();






  }

  //this is where pixel specific stuff gets drawn first
  public void paint(int displaypixels[]){}


  // java graphics stuff is drawn over pixel items
  public void paint2(Graphics g){

    for(int k=0;k<card_names.length;k++){
      if(k == viewing){
        g.setColor(Color.yellow);
      }else{
        g.setColor(Color.white);
      }
      g.drawString(card_names[k], edge_padding, top_padding + name_step*k);
    }

    g.setColor(Color.white);
    g.drawString(card_names[viewing], left_padding+  (w-left_padding-edge_padding)/2 - card_names[viewing].length() * 5,top_padding);
    g.drawString(player0name, left_padding+  (w-left_padding-edge_padding)/4 - player0name.length() * 5,top_padding);
    g.drawString(player1name, left_padding+  (w-left_padding-edge_padding)*3/4 - player1name.length() * 5,top_padding);

    String draw_card_name = card_names[viewing];
    if(draw_card_name == FlipMetric.YOUR_MONEY){
      draw_card_name = Money.GENERAL_MONEY_NAME;
    }
    
    g.drawString("Draws", left_padding - 40 , top_padding + (h-top_padding - edge_padding)/4);
    drawWinLossGraph(g, card_draw_wins, 0, draw_card_name, min_samples, left_padding, top_padding, w/2-left_padding/2-edge_padding, (h-top_padding)/2 - edge_padding*2);
    drawWinLossGraph(g, card_draw_wins, 1, draw_card_name, min_samples, w/2 +left_padding/2, top_padding, w/2-left_padding/2-edge_padding, (h-top_padding)/2 - edge_padding*2);
    
    g.drawString("Flips", left_padding - 40 , top_padding + (h-top_padding - edge_padding)*3/4);
    drawWinLossGraph(g, card_flip_wins, 0, card_names[viewing], min_samples, left_padding, (h-edge_padding)/2, w/2-left_padding/2-edge_padding, (h-top_padding)/2 - edge_padding*2);
    drawWinLossGraph(g, card_flip_wins, 1, card_names[viewing], min_samples, w/2 +left_padding/2, (h-edge_padding)/2, w/2-left_padding/2-edge_padding, (h-top_padding)/2 - edge_padding*2);
  }



  public void drawWinLossGraph(Graphics g, HashMap<String, int[]> stats, int player, String card_name, int min_samples, int x, int y, int width, int height){
    
    if(card_draw_wins !=null){
      int min_amount = 99999;
      int max_amount = -99999;
      HashMap<Integer, int[]> win_chance = new HashMap<Integer, int[]>();

      Iterator<String> i = stats.keySet().iterator();
      while(i.hasNext()){
        String key = i.next();
        String skey[] = key.split(",");
        int key_player = Integer.parseInt(skey[0]);
        if(player == key_player){
          String name = skey[1];
          if(name.equals(card_name)){
            int amount = Integer.parseInt(skey[2]);
            int[] lose_win = stats.get(key);
            if(amount < min_amount){
              min_amount = amount;
            }
            if(amount > max_amount){
              max_amount = amount;
            }
            win_chance.put(amount, lose_win);
          }
        }
      }


      if(max_amount > min_amount){
        Iterator<Integer> ai = win_chance.keySet().iterator();
        while(ai.hasNext()){
          int amount = ai.next().intValue();
          int lw[] = win_chance.get(amount) ;
          int samples = lw[0] + lw[1];
          double p = lw[1] / (double)samples;
          int bx = x+((amount - min_amount) * width )/ (1+max_amount-min_amount);
          if(samples > min_samples*4){
            g.setColor(Color.green);
          }else if(samples > min_samples){
            g.setColor(Color.yellow);
          }else{
            g.setColor(Color.red);
          }
          g.fillRect(bx,(int)( y + 15 +  (1-p) * (height-30)), width / (1+max_amount-min_amount)+1, (int)(p * (height-30)));
          g.setColor(Color.white);
          g.drawString(""+amount, bx+ width / (2*(1+max_amount-min_amount))-5, y+height);
          if(display_samples){
            g.drawString("(" + samples +")", bx+ width / (2*(1+max_amount-min_amount))-5, y+30);
          }
        }
      }
    }
    g.setColor(Color.white);

    g.drawRect(x,y+15,width,height-30);
    g.drawLine(x, y + height/2, x+width, y + height/2);
    g.drawString("50%", x+width/2 - 10, y + height/2);

  }


  public void actionPerformed(ActionEvent e )//used for the timer
  {
    repaint();

  }


  public void keyPressed(KeyEvent e)
  {
    int t = e.getKeyCode() ;


    if(t == KeyEvent.VK_ENTER){

    }
    if(t == KeyEvent.VK_LEFT){

    }
    if(t == KeyEvent.VK_RIGHT){

    } 
    if(t == KeyEvent.VK_UP){
      viewing--;
      if(viewing<0){
        viewing+=card_names.length;
      }
    }

    if(t == KeyEvent.VK_DOWN){  
      viewing++;
      if(viewing >= card_names.length){
        viewing-=card_names.length;
      }
    }


  }
  public void keyTyped(KeyEvent e)
  {
    char t = e.getKeyChar() ;

    if(t == 's'){
      display_samples = !display_samples;
    }

  }
  public void keyReleased(KeyEvent e)
  {

    int t = e.getKeyCode() ;


    if(t == KeyEvent.VK_ENTER){

    }

    if( t== KeyEvent.VK_UP){
    }
    if( t== KeyEvent.VK_DOWN){
    }
    if( t== KeyEvent.VK_LEFT){
    }
    if( t== KeyEvent.VK_RIGHT){
    }


  }





  public void mousePressed(MouseEvent e)
  {
    int b = e.getButton() ;
    int mx = e.getX(), my = e.getY();
    if(mx < left_padding){
     int select = my/name_step;
     if(select >= 0 && select < card_names.length){
       viewing = select;
     }
    }
    
    pane.requestFocus();
  }
  public void mouseClicked(MouseEvent e)
  {
  }

  public void mouseReleased(MouseEvent e)
  {

  }

  public void mouseEntered(MouseEvent e)
  {
  }

  public void mouseExited(MouseEvent e)
  {
  }


  public void mouseMoved(MouseEvent e)
  {
  }

  public void mouseDragged(MouseEvent e)
  {
  }

  public static void main(String[] args)
  {
    MainStats window = new MainStats();
    window.init() ;
    window.addWindowListener(new WindowAdapter()
    { public void windowClosing(WindowEvent e) { System.exit(0); }});

    window.setSize(w, h);
    window.setVisible(true);
  }



}

