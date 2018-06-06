
import java.awt.* ;
import java.awt.event.* ;

import javax.swing.* ;

import java.awt.image.BufferedImage ;
import java.awt.image.PixelGrabber ;
import java.awt.image.DataBufferInt ;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random ;
import java.awt.image.BufferStrategy ;

public class Main extends JFrame
implements ActionListener,MouseListener, KeyListener, MouseMotionListener
{
  private Container pane ;
  BufferedImage display;
  DataBufferInt databuffer ;
  int displaypixels[];
  static int w=1920,h=1080;
  BufferStrategy strategy ;

  public static final String CARD_FOLDER = "./cards/";
  public static HashMap<String,LazyImage> card_image;
  public static final String BACK = "back", BACK_IMAGE= "Flutterback.jpg";
  public static final String BLANK = "blank", BLANK_IMAGE= "Blank.jpg";

  public String state_string = "";
  public String last_string = "";


  Game game;
  Player[] player;
  HumanPlayer human ;

  ArrayList<CardDisplay> card_displays = new ArrayList<CardDisplay>();
  int trash_view = 0 ;

  int log_pointer = 0 ;

  boolean card_zoom = false;
  CardDisplay mouse_over_card = null;
  CardDisplay selected_card = null;
  ArrayList<String> log_display = new ArrayList<String>();
  int max_log_lines = 50;
  
  DrawMetric draws = new DrawMetric();
  FlipMetric flips = new FlipMetric();

  public void init()
  {
    pane = getContentPane();
    pane.addMouseListener(this);
    pane.addMouseMotionListener(this);
    pane.addKeyListener(this);
    pane.requestFocus();
    Timer clock = new Timer(50, this); 
    clock.start();

    setUpGame();

  }



  public void setUpGame(){
    human = new HumanPlayer();
    //player = new Player[]{human, new BasicPlayer(1)};
    player = new Player[]{new BasicPlayer(0), new BasicPlayer(1)};
    Deck main_deck = Deck.getMainDeck();
    int seed = (int)(Math.random()*9999999);
    //int seed = 2491508;
    System.out.println("Game seed:" + seed);
    game = new Game(player, main_deck, seed);
    game.attachMetric(draws);
    game.attachMetric(flips);
    Thread t = new Thread(game);
    t.start();
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
    for(int k=0;k<card_displays.size();k++){
      CardDisplay display = card_displays.get(k);
      // Card zoom key can also be used to look behind choice cards covering the board.    
      if(!card_zoom || display.place != Player.CHOICE){ 
        display.draw(g);
      }
    }

    g.setColor(Color.white);

    int y = 50;
    g.drawString(player[0].getStatus(), 20, h-80);
    y+=24;

    for(int k=0;k<player.length;k++){
      g.drawString(player[k].getName() + ":" + "fame="+ player[k].getFame(), w-140, y );
      y+=14;
      g.drawString("   " + "cash=" + player[k].reserve +" hand=" + player[k].getHand().size(), w-140, y );
      y+=14;
    }




    y = 40;
    for(int k=0;k<log_display.size();k++){
      g.drawString(log_display.get(k), 20, y);
      y+=14;
    }

    if(card_zoom && mouse_over_card!=null){
      mouse_over_card.drawFullCenter(g);
    }



  }


  public synchronized void updateDisplay(){
    card_displays = new ArrayList<CardDisplay>();
    LazyImage back_image = card_image.get(BACK);
    LazyImage blank_image = card_image.get(BLANK);
    int card_width = back_image.getWidth(), card_height = back_image.getHeight();
    int x_step = card_width + 2;
    int main_deck_x = (int)(w-x_step*0.5), main_deck_y = h/2;
    int y_offset = 30;

    // Show back of Card for main deck.
    if(game.main_deck.size() > 0){
      card_displays.add(new CardDisplay(main_deck_x, main_deck_y, back_image, Player.MAIN_DECK, 0));
    }else{
      card_displays.add(new CardDisplay(main_deck_x, main_deck_y, blank_image, Player.MAIN_DECK, 0));
    }
    // Show one card of the trash.
    if(game.trash_pile.size() > 0){
      Card c = game.trash_pile.getCard(game.trash_pile.size()-1);
      card_displays.add(new CardDisplay(main_deck_x, main_deck_y + card_height+10, card_image.get(c.name), Player.TRASH, 0));
    }else{
      card_displays.add(new CardDisplay(main_deck_x, main_deck_y + card_height+10, blank_image, Player.TRASH, 0));
    }
    //Show money for reserve 
    String money_name = Money.MONEY_NAMES[player[0].getPlayerNumber()];
    if(player[0].reserve > 0){
      card_displays.add(new CardDisplay(main_deck_x, main_deck_y + card_height*2+20, card_image.get(money_name), Player.CASH_RESERVE, 0));
    }else{
      card_displays.add(new CardDisplay(main_deck_x, main_deck_y + card_height*2+20, blank_image, Player.CASH_RESERVE, 0));
    }

    // Add start-ups (places are >=0)
    for(int k = 0 ; k < game.start_ups.size(); k++){
      int y = (k*2+1)*h/ (game.start_ups.size()*2+2) + y_offset;
      StartUp s = game.start_ups.get(k);
      if(s.deck.size() > 0){// Deck pile
        card_displays.add(new CardDisplay(main_deck_x-x_step*2, y, back_image, k, Player.DECK_INDEX)); 
      }else{
        card_displays.add(new CardDisplay(main_deck_x-x_step*2, y, blank_image, k, Player.DECK_INDEX)); 
      }
      if(s.discard.size() > 0){ // Discard Pile
        Card c = s.discard.getCard(s.discard.size()-1);
        card_displays.add(new CardDisplay(main_deck_x-x_step, y, card_image.get(c.name), k, Player.DISCARD_INDEX));
      }else{
        card_displays.add(new CardDisplay(main_deck_x-x_step, y, blank_image, k, Player.DISCARD_INDEX));
      }
      for(int j=0;j<s.project.size();j++){// Project cards are >= 0
        Card c = s.project.getCard(j);
        card_displays.add(new CardDisplay(main_deck_x-x_step*(3+j), y, card_image.get(c.name), k, j));
      }
      // Add a blank spot at the end for invest clicking
      card_displays.add(new CardDisplay(main_deck_x-x_step*(3+s.project.size()), y, blank_image, k, s.project.size()));
    }

    // Add hand
    Deck hand = game.getPlayer(0).getHand();
    int y = h-card_height/2;
    for(int k=0;k<hand.size();k++){
      int x = (int)(w/2 + (k - hand.size()*.5)*x_step) ;
      Card c = hand.getCard(k);
      card_displays.add(new CardDisplay(x, y, card_image.get(c.name), Player.HAND, k));
    }

    // Add choice cards.
    if(human.choice_target != null){
      int amount = human.choice_target.size() ;
      if(human.getState()==HumanPlayer.REORDERING){
        amount -= human.discarded.size() + human.reordered.size() ;
      }
      int j = 0;
      for(int k=0;k<human.choice_target.size();k++){
        if(human.getState()!=HumanPlayer.REORDERING || (!human.discarded.contains(k) && !human.reordered.contains(k))){
          Card c = human.choice_target.getCard(k);
          CardDisplay cd = new CardDisplay((int)((j+.5)*w/(amount+1)), h/2, card_image.get(c.name), Player.CHOICE, k);
          cd.card_scale = 1;
          card_displays.add(cd);
          j++;
        }
      }
    }

    // Migrate selection
    if(selected_card != null){
      boolean found = false;
      for(int k=0;k<card_displays.size();k++){
        CardDisplay c = card_displays.get(k);
        if(c.place == selected_card.place && c.index == selected_card.index){
          c.selected = true;
          selected_card =c;
          found = true;
        }
      }
      if(!found){
        selected_card = null;
      }
    }


  }



  public void actionPerformed(ActionEvent e )//used for the timer
  {
    /*if(game!=null && !game.game_over && !game.stepping){
      game.step();
      updateDisplay();
    }*/
    if(game !=null){
      updateDisplay();
      while(log_pointer < game.text_log.size()){
        String s = game.text_log.get(log_pointer++);
        System.out.println(s);
        // Hide the computer player's draws from the human player in the GUI log.
        for(int k=1; k <=3;k++){
          if(matchFirst(s,"Player " + k + " drew")){
            s = "Player " + k + " drew [redacted].";
          }else if(matchFirst(s,"Player " + k + " spent a Capital")){
            s = "Player " + k + " spent a capital and drew [redacted].";
          }else if(matchFirst(s,"Player " + k + " completed a Capital")){
            s = "Player " + k + " completed a capital and drew [redacted].";
          }
        }
        log_display.add(s);
        if(log_display.size() > max_log_lines){
          log_display.remove(0);
        }
      }
    }


    repaint();

  }

  public static boolean matchFirst(String a, String b){
    int len = Math.min(a.length(), b.length());
    return a.substring(0,len).equals(b.substring(0,len));
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
    }

    if(t == KeyEvent.VK_DOWN){  

    }

    if(t == KeyEvent.VK_SPACE){
      card_zoom = true;
    }
 

  }
  public void keyTyped(KeyEvent e)
  {
    char t = e.getKeyChar() ;

    if(t == 'p'){ // Print anonymized game state
      System.out.println(human.last_game_instance);
    }else if(t == 'P'){ // Print raw game state
      System.out.println(game);
    }else if(t == 'd'){
      System.out.println(draws); // Print players draws (whole game)
    }else if(t == 'f'){
      System.out.println(flips); // print player flips (whole game)
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

    if(t == KeyEvent.VK_SPACE){
      card_zoom = false;
    }

  }





  public void mousePressed(MouseEvent e)
  {
    int b = e.getButton() ;
    int mx = e.getX(), my = e.getY();
    mouse_over_card = null;
    for(int k=0;k<card_displays.size();k++){
      CardDisplay c = card_displays.get(k);
      if(c.inside(mx, my)){
        mouse_over_card = c;
      }
    }
    //TODO fix bug where multiple clicks are sometimes required to complete a project as your only turn play.
    if(mouse_over_card !=null && b == MouseEvent.BUTTON1){
      // Choose flip.
      if(mouse_over_card.place >= 0 && (human.getState() == HumanPlayer.CHOOSING_FLIP || human.getState() == HumanPlayer.CHOOSING_PROJECT)){
        human.setProject(mouse_over_card.place);
        // choose draw location
      }else if( human.getState() == HumanPlayer.CHOOSING_DRAW && 
          (mouse_over_card.place == Player.CASH_RESERVE || mouse_over_card.place == Player.MAIN_DECK)){
        human.setDrawLocation(mouse_over_card.place);
        // Choosing completion of project
      }else if(human.getState() == HumanPlayer.CHOOSING_PLAY && selected_card == null &&
          mouse_over_card.place >= 0){
        human.setPlay(new CompleteProject(mouse_over_card.place));
        // Choosing invest card
      }else if(human.getState() == HumanPlayer.CHOOSING_PLAY && selected_card!=null 
          && selected_card.place == Player.HAND && mouse_over_card.place >= 0 && mouse_over_card.index == game.start_ups.get(mouse_over_card.place).project.size()){
        human.setPlay(new InvestCard(selected_card.index,  mouse_over_card.place));
        selected_card = null;
        //Choosing to spend card.
      }else if(human.getState() == HumanPlayer.CHOOSING_PLAY && selected_card !=null 
          && selected_card.place == Player.HAND && (mouse_over_card.place == Player.MAIN_DECK || mouse_over_card.place == Player.TRASH)){
        human.setPlay(new SpendCard(selected_card.index));
        selected_card = null;
        // Choosing a project card for an effect (such as sabotage).
      }else if(human.getState() == HumanPlayer.CHOOSING_PROJECT_CARD && mouse_over_card.place >= 0 && mouse_over_card.index >=0 
          && mouse_over_card.index < game.start_ups.get(mouse_over_card.place).project.size()){
        human.setProjectCard(mouse_over_card.place, mouse_over_card.index);
        // Choosing a project deck for an effect.
      }else if(human.getState() == HumanPlayer.CHOOSING_DECK && mouse_over_card.place >= 0  
          && game.start_ups.get(mouse_over_card.place).deck.size() + game.start_ups.get(mouse_over_card.place).discard.size() > 0){
        human.setDeck(mouse_over_card.place);
        // Choosing the main deck for a card effect.
      }else if(human.getState() == HumanPlayer.CHOOSING_DECK && mouse_over_card.place == Player.MAIN_DECK && game.main_deck.size() > 0 ){
        human.setDeck(mouse_over_card.place);
        // Choosing to trash a card instead of keeping it.
      }else if(human.getState() == HumanPlayer.CHOOSING_TRASH && mouse_over_card.place == Player.TRASH){
        human.setTrash(Player.TRASH);
        // CHoosing to keep a card rather than trashing it.
      }else if(human.getState() == HumanPlayer.CHOOSING_TRASH && mouse_over_card.place == human.choice_location){
        human.setTrash(Player.KEEP_CARD);
        // Choosing to discard a card from planning on a project.
      }else if(human.getState() == HumanPlayer.REORDERING && selected_card !=null && selected_card.place == Player.CHOICE 
          && human.choice_location >= 0 && mouse_over_card.place == human.choice_location && mouse_over_card.index == Player.DISCARD_INDEX){
        human.discardOne(selected_card.index);
        selected_card = null;
        // Choosing to keep a card from planning on a project.
      }else if(human.getState() == HumanPlayer.REORDERING && selected_card !=null && selected_card.place == Player.CHOICE 
          && human.choice_location >= 0 && mouse_over_card.place == human.choice_location && mouse_over_card.index == Player.DECK_INDEX){
        human.putBackOne(selected_card.index);
        selected_card = null;
        // Choosing to discard a card from planning on the main deck.
      }else if(human.getState() == HumanPlayer.REORDERING && selected_card !=null && selected_card.place == Player.CHOICE 
          && human.choice_location == Player.MAIN_DECK && mouse_over_card.place == Player.TRASH){
        human.discardOne(selected_card.index);
        selected_card = null;
        // Choosing to keep a card from planning on the main deck.
      }else if(human.getState() == HumanPlayer.REORDERING && selected_card !=null && selected_card.place == Player.CHOICE 
          && human.choice_location == Player.MAIN_DECK && mouse_over_card.place == Player.MAIN_DECK){
        human.putBackOne(selected_card.index);
        selected_card = null;
      }else{
        mouse_over_card.selected = true;
        if(selected_card != null){
          selected_card.selected=false;
        }
        selected_card = mouse_over_card;
      }
    }else{ // If clicked off a card or didn't use left mouse
      // Deselect any selected card
      if(selected_card != null){
        selected_card.selected=false;
      }
      selected_card = null;
      // if a nonleft click on a card
      if(mouse_over_card!=null){
        if(mouse_over_card.place == Player.TRASH){
          Card c = game.trash_pile.removeCard(0); // cycle trash cards for viewing.
          game.trash_pile.add(c);
        }
        if(mouse_over_card.place >=0 && mouse_over_card.index == -2){
          Card c = game.start_ups.get(mouse_over_card.place).discard.removeCard(0); // cycle discards for viewing.
          game.start_ups.get(mouse_over_card.place).discard.add(c);
        }
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
    //int b = e.getButton() ;
    int mx = e.getX(), my = e.getY();
    mouse_over_card = null;
    for(int k=0;k<card_displays.size();k++){
      CardDisplay c = card_displays.get(k);
      if(c.inside(mx, my) && !(card_zoom && c.place == Player.CHOICE)){ // Card zoom doubles as "hide choice cards"
        mouse_over_card = c;
      }
    }


  }

  public void mouseDragged(MouseEvent e)
  {
  }

  public static HashMap<String, LazyImage> loadCardImages(){
    HashMap<String, LazyImage> card_image = new HashMap<String, LazyImage>();

    card_image.put(BACK, new LazyImage(CARD_FOLDER + BACK_IMAGE));
    card_image.put(BLANK, new LazyImage(CARD_FOLDER + BLANK_IMAGE));
    for(int k=0;k<Money.MONEY_NAMES.length;k++){
      card_image.put(Money.MONEY_NAMES[k], new LazyImage(CARD_FOLDER + Money.MONEY_NAMES[k]+".PNG"));
    }
    card_image.put(ViralMarketing.VIRAL_MARKETING_NAME, new LazyImage(CARD_FOLDER + ViralMarketing.VIRAL_MARKETING_NAME +".PNG"));
    card_image.put(Capital.CAPITAL_NAME, new LazyImage(CARD_FOLDER + Capital.CAPITAL_NAME +".PNG"));
    card_image.put(Profit.PROFIT_NAME, new LazyImage(CARD_FOLDER + Profit.PROFIT_NAME +".PNG"));
    card_image.put(Sabotage.SABOTAGE_NAME, new LazyImage(CARD_FOLDER + Sabotage.SABOTAGE_NAME +".PNG"));
    card_image.put(Breakthrough.BREAKTHROUGH_NAME, new LazyImage(CARD_FOLDER + Breakthrough.BREAKTHROUGH_NAME +".PNG"));
    card_image.put(Pivot.PIVOT_NAME, new LazyImage(CARD_FOLDER + Pivot.PIVOT_NAME +".PNG"));
    card_image.put(Damages.DAMAGES_NAME, new LazyImage(CARD_FOLDER + Damages.DAMAGES_NAME +".PNG"));
    card_image.put(EpicFail.EPIC_FAIL_NAME, new LazyImage(CARD_FOLDER + EpicFail.EPIC_FAIL_NAME +".PNG"));
    card_image.put(Planning.PLANNING_NAME, new LazyImage(CARD_FOLDER + Planning.PLANNING_NAME +".PNG"));
    card_image.put(Funnel.FUNNEL_NAME, new LazyImage(CARD_FOLDER + Funnel.FUNNEL_NAME +".PNG"));
    card_image.put(Delay.DELAY_NAME, new LazyImage(CARD_FOLDER + Delay.DELAY_NAME +".PNG"));
    card_image.put(PressRelease.PRESS_RELEASE_NAME, new LazyImage(CARD_FOLDER + PressRelease.PRESS_RELEASE_NAME +".PNG"));
    card_image.put(Scandal.SCANDAL_NAME, new LazyImage(CARD_FOLDER + Scandal.SCANDAL_NAME +".PNG"));
    card_image.put(PublicityStunt.PUBLICITY_STUNT_NAME, new LazyImage(CARD_FOLDER + PublicityStunt.PUBLICITY_STUNT_NAME +".PNG"));
    card_image.put(Spinoff.SPINOFF_NAME, new LazyImage(CARD_FOLDER + Spinoff.SPINOFF_NAME +".PNG"));
    card_image.put(Poach.POACH_NAME, new LazyImage(CARD_FOLDER + Poach.POACH_NAME +".PNG"));
    card_image.put(Rush.RUSH_NAME, new LazyImage(CARD_FOLDER + Rush.RUSH_NAME +".PNG"));
    card_image.put(Lawyers.LAWYERS_NAME, new LazyImage(CARD_FOLDER + Lawyers.LAWYERS_NAME +".PNG"));
    card_image.put(Documentation.DOCUMENTATION_NAME, new LazyImage(CARD_FOLDER + Documentation.DOCUMENTATION_NAME +".PNG"));
    card_image.put(Taxes.TAXES_NAME, new LazyImage(CARD_FOLDER + Taxes.TAXES_NAME +".PNG"));
    card_image.put(OpenSource.OPEN_SOURCE_NAME, new LazyImage(CARD_FOLDER + OpenSource.OPEN_SOURCE_NAME +".PNG"));
    card_image.put(Patent.PATENT_NAME, new LazyImage(CARD_FOLDER + Patent.PATENT_NAME +".PNG"));
    card_image.put(Boom.BOOM_NAME, new LazyImage(CARD_FOLDER + Boom.BOOM_NAME +".PNG"));
    card_image.put(Bust.BUST_NAME, new LazyImage(CARD_FOLDER + Bust.BUST_NAME +".PNG"));
    card_image.put(Infamy.INFAMY_NAME, new LazyImage(CARD_FOLDER + Infamy.INFAMY_NAME +".PNG"));
    card_image.put(Underdog.UNDERDOG_NAME, new LazyImage(CARD_FOLDER + Underdog.UNDERDOG_NAME +".PNG"));
    card_image.put(Nonprofit.NONPROFIT_NAME, new LazyImage(CARD_FOLDER + Nonprofit.NONPROFIT_NAME +".PNG"));
    return card_image;
  }
  public static void main(String[] args)
  {
    card_image = loadCardImages();
    Main window = new Main();
    window.init() ;
    window.addWindowListener(new WindowAdapter()
    { public void windowClosing(WindowEvent e) { 
      window.game.endGame();
      System.exit(0);
    }});

    window.setSize(w, h);
    window.setVisible(true);
  }



  private class CardDisplay{
    public int x, y;
    public LazyImage image;
    public int place, index;
    public boolean selected = false;
    public double card_scale = .35;

    public CardDisplay(int x, int y, LazyImage img, int location, int index){
      this.x = x;
      this.y = y;
      image = img;
      this.place= location;
      this.index =index;
    }

    public void draw(Graphics g){
      BufferedImage i = image.getImage(card_scale);
      if(i!=null){
        if(selected){
          g.setColor(Color.green);
          g.fillRect(x-i.getWidth()/2-2,y-i.getHeight()/2-2, i.getWidth()+4, i.getHeight()+4);
        }
        g.drawImage(i,x-i.getWidth()/2, y-i.getHeight()/2, null);

      }
    }

    public void drawFull(Graphics g){
      BufferedImage i = image.getImage(1);
      if(i!=null){
        g.drawImage(i,x-i.getWidth()/2, y-i.getHeight()/2, null);
      }
    }

    public void drawFullCenter(Graphics g){
      BufferedImage i = image.getImage(1);
      if(i!=null){
        g.drawImage(i,w/2-i.getWidth()/2, h/2-i.getHeight()/2, null);
      }
    }

    // Returns true if a point is inside this card.
    public boolean inside(int mx, int my){
      BufferedImage i = image.getImage(card_scale);
      return x > mx-i.getWidth()/2 && x < mx+i.getWidth()/2 && y > my-i.getHeight()/2 && y < my+i.getHeight()/2;
    }

    public int getWidth(){
      BufferedImage i = image.getImage(card_scale);
      return i.getWidth();
    }

    public int getHeight(){
      BufferedImage i = image.getImage(card_scale);
      return i.getHeight();
    }

  }


}

