import java.util.ArrayList;


public class HumanPlayer extends Player{

  boolean made_selection = false;
  private int selected_draw;
  private int selected_project;
  private int selected_hand_card;
  private int selected_player;
  private int selected_project_card;
  private int selected_deck;
  private int selected_trash;
  private int[] selected_reorder;
  private Event selected_play;
  
  public Game last_game_instance;

  public static final int WAITING = 0, 
      CHOOSING_FLIP = 1,// TODO maybe merge this with choose project
      CHOOSING_DRAW = 2,
      CHOOSING_PLAY = 3,
      SPENDING_CARD = 4,
      INVESTING_CARD = 5,
      COMPLETING_PROJECT = 6,
      CHOOSING_PROJECT = 7,
      CHOOSING_PROJECT_CARD = 8,
      CHOOSING_DECK = 9,
      CHOOSING_PLAYER = 10,
      CHOOSING_TRASH = 11,
      REORDERING = 12;

  public static int SLEEP_INCREMENT = 100;

  private int state = WAITING;


  private String status = "Waiting";

  public Deck choice_target;
  public int choice_location;

  // Store intermediate Planning actions. It's only 1 action in the game, but for a human it seems like 3 choices.
  public ArrayList<Integer> reordered;
  public ArrayList<Integer> discarded;

  public int getState(){
    return state;
  }

  public int chooseFlip(Game game) {
    last_game_instance = game;
    status = "Choose Project to flip";
    state = CHOOSING_FLIP;
    waitForStateChange(CHOOSING_FLIP);
    return selected_project;
  }

  public int selectDrawLocation(Game game) {
    last_game_instance = game;
    status = "Choose Draw Location";
    state = CHOOSING_DRAW;
    waitForStateChange(CHOOSING_DRAW);
    status = "Waiting";
    return selected_draw;
  }

  public Event makePlay(Game game) {
    last_game_instance = game;
    status = "Make play.";
    state = CHOOSING_PLAY;
    waitForStateChange(CHOOSING_PLAY);
    status = "Waiting";
    return selected_play;
  }

  public int chooseProjectforEffect(Card c, Game game) {
    last_game_instance = game;
    status = "Choose project for " + c.name;
    state = CHOOSING_PROJECT;
    waitForStateChange(CHOOSING_PROJECT);
    status = "Waiting";
    return selected_project;
  }

  //Returns the {project index, card index} for a given card's effect that targets a card in a project (such as sabotage or delay).
  public int[] chooseProjectCardforEffect(Card c, Game game) {
    last_game_instance = game;
    status = "Choose project card for " + c.name;
    state = CHOOSING_PROJECT_CARD;
    waitForStateChange(CHOOSING_PROJECT_CARD);
    status = "Waiting";
    return new int[]{selected_project, selected_project_card};
  }

  public int chooseDeckforEffect(Card c, Game game) {
    last_game_instance = game;
    status = "Choose deck for " + c.name;
    state = CHOOSING_DECK;
    waitForStateChange(CHOOSING_DECK);
    status = "Waiting";
    return selected_deck;
  }

  public int choosePlayerforEffect(Card c, Game game) {
    last_game_instance = game;
    //TODO implement UI for selecting player for more than 2 player games.
    /*
    status = "Choose player for " + c.name;
    state = CHOOSING_PLAYER;
    waitForStateChange(CHOOSING_PLAYER);
    status = "Waiting";
    return selected_player;
    */
    return 1;
  }

  public int trashCard(Card c, int location, Game game) {
    last_game_instance = game;
    status = "Choose whether to trash or return " + c.name +".";
    state = CHOOSING_TRASH;
    choice_target= new Deck();
    choice_target.add(c);
    choice_location = location;
    waitForStateChange(CHOOSING_TRASH);
    status = "Waiting";
    choice_target = null;
    return selected_trash;
  }

  public int[] reorderOrDiscard(Deck d, int location, Game game) {
    last_game_instance = game;
    status = "Discard or return " + d.printRange(0,d.size()) +".";
    choice_target = d;
    choice_location = location;
    reordered = new ArrayList<Integer>();
    discarded = new ArrayList<Integer>();
    state = REORDERING;
    waitForStateChange(REORDERING);
    status = "Waiting";
    choice_target = null;
    return selected_reorder;
  }


  public void waitForStateChange(int old_state){
    while(state == old_state){
      try{
        Thread.sleep(SLEEP_INCREMENT);
      }catch(Exception e){}
    }
  }

  public String getStatus() {
    return status;
  }

  public void setProject(int project) {
    selected_project = project;
    state = WAITING;  
  }

  public void setDrawLocation(int place) {
    selected_draw = place;
    state = WAITING;
  }

  public void setPlay(Event play) {
    selected_play = play;
    state = WAITING;

  }

  public void setProjectCard(int project, int index) {
    selected_project = project;
    selected_project_card = index;
    state = WAITING;
  }

  public void setDeck(int which_deck) {
    selected_deck = which_deck;
    state = WAITING;
  }

  public void setTrash(int keep_or_trash) {
    selected_trash = keep_or_trash;
    state = WAITING;
  }

  public void setReorderOrDiscard(ArrayList<Integer> order){
    selected_reorder = new int[order.size()];
    for(int k=0;k<selected_reorder.length;k++){
      selected_reorder[k] = order.get(k);
    }
    
    state = WAITING;
  }

  // Discard the given index card in the choice list (for planning)
  public void discardOne(int index){
    discarded.add(index);
    if(discarded.size() + reordered.size() == choice_target.size() ){
      setReorderOrDiscard(reordered);
    }
  }

  //Put back the given index card in the choice list (for planning)
  public void putBackOne(int index){
    reordered.add(index);
    if(discarded.size() + reordered.size() == choice_target.size() ){
      setReorderOrDiscard(reordered);
    }
  }

  public Player copy() {
    HumanPlayer h = new HumanPlayer();
    h.copyGameStateFrom(this);
    return h;
  }

}
