import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

// A Game is a snapshot of the entire state of the game at a point in time.
public class Game implements Runnable{

  //Number of start-ups at the start of the game.
  static final int STARTING_START_UPS = 3;

  //Number of starting draws from the main deck.
  static final int STARTING_CARDS = 2;

  // number of starting money.
  static final int STARTING_MONEY = 1 ;

  // Money cards used for each players based on the number of players.
  static final int[] MONEY_USED = new int[]{0, 15, 15, 13, 11};

  // Whether to re-shuffle any card drawn for starting start-ups that weren't valid.
  static final boolean RESHUFFLE_POOR_STARTS = false;

  static final boolean SKIP_FIRST_PLAYER_DRAW_PHASE = true;
  static final boolean LOSE_ONE_CARD_FOR_FIRST = false;



  Deque<Event> event_queue;
  Deck main_deck ;
  Deck trash_pile;
  ArrayList<StartUp> start_ups;
  ArrayList<Player> players;
  Random rand; // Pseudo-random number generator.

  int round;
  int current_player ;
  int phase;

  boolean[] flipped; // info for during flip phase.
  Deque<Integer> completing_project = new ArrayDeque<Integer>(), completion_winner = new ArrayDeque<Integer>(); // Info for during complete phase.
  Deque<Boolean> completion_spinoff = new ArrayDeque<Boolean>();
  Deque<Integer> last_phase = new ArrayDeque<Integer>(); // Used for returning after special phases
  static final int FLIP = 0, DRAW = 1, PLAY = 2, COMPLETE = 3, SPECIAL_COMPLETE = 4, SPECIAL_CARD_EXECUTION = 5; // Phases
  Deque<Card> special_phase_card = new ArrayDeque<Card>();
  Deque<Integer> special_phase_player = new ArrayDeque<Integer>();
  static final int NOONE = -1;


  boolean skip_next_draw; // Keeps track of whether to skip the next draw (such as on the first turn).
  boolean game_over ; // Whether the game is over.
  boolean stepping = false; // whether the game is currently stepping.

  public boolean skip_next_completion[]; // For each player whether they have lost their next completion (from documentation)
  boolean skipping_completion = false; // whether we are skipping the current completion phase.
  public int flip_after_completion[] ; // For each project, how many extra cards to flip after completion.

  // A log of all events that have occurred sufficient to reproduce the game entirely.
  ArrayList<Event> log;
  ArrayList<String> text_log;
  int seed ;
  Deck starting_main_deck ;


  public boolean logging_enabled = true; // Whether to add text to the log and call print functions.
  public boolean enforce_hidden_information = true; // Whether to pass a sanitized copy of the game to choices instead of the raw.

  private ArrayList<Metric> metrics = new ArrayList<Metric>();

  public Game(Player[] all_players, Deck non_cash_cards, int random_seed){
    seed = random_seed;
    rand = new Random(seed);
    starting_main_deck = non_cash_cards.copy();

    main_deck = non_cash_cards;
    main_deck.shuffle(rand);


    log = new ArrayList<Event>();
    text_log = new ArrayList<String>();

    players = new ArrayList<Player>(all_players.length);
    current_player = (int)(rand.nextDouble()*players.size());
    int starting_reserve = MONEY_USED[all_players.length] - STARTING_START_UPS;
    for(int k=0;k<all_players.length;k++){
      Player p = all_players[k];
      p.setPlayerNumber(k);
      p.reserve = starting_reserve;
      p.fame = 0 ;
      p.hand = new Deck();
      for(int j=0;j<STARTING_CARDS;j++){
        if(!LOSE_ONE_CARD_FOR_FIRST || k != current_player || j != 0){
          Card c = main_deck.draw();
          if(logging_enabled){
            text_log.add(p.getName() +" drew " + c.name + ".");
          }
          p.add(c);
        }
      }
      for(int j=0;j<STARTING_MONEY;j++){
        p.add(p.drawMoney());
      }
      if(logging_enabled){
        text_log.add(p.getName() +" drew " + STARTING_MONEY + " money.");
      }
      players.add(all_players[k]);
    }

    //Create starting start-ups.
    start_ups = new ArrayList<StartUp>();
    trash_pile = new Deck();
    while(start_ups.size() < STARTING_START_UPS){
      Card s = main_deck.draw();
      if(s.canBeStartingCard()){
        if(logging_enabled){
          text_log.add("Drew " + s.name +" for starting project.");
        }
        start_ups.add(new StartUp(s, players.size(), rand));
      }else{
        trash_pile.add(s);
      }
    }
    if(RESHUFFLE_POOR_STARTS){
      main_deck.add(trash_pile);
      trash_pile = new Deck();
      main_deck.shuffle(rand);
    }else if(trash_pile.size()>0){
      text_log.add("Trashed invalid starting project draws: " + trash_pile.printRange(0,trash_pile.size()) +"." );
    }




    round = 1;
    phase = FLIP;
    flipped = new boolean[start_ups.size()];
    skip_next_completion = new boolean[players.size()];

    skip_next_draw = SKIP_FIRST_PLAYER_DRAW_PHASE; 
    game_over = false;
    event_queue = new ArrayDeque<Event>();

  }

  // Creates a non-shallow duplicate of this game with some quirks.
  // References to cards are all duplicated and not merged so exact card reference matches may fail. TODO Make sure this is ok
  // Log and starting_main_deck are a shallow copy duplicated to reduce resources.
  // Logging and enforce_hidden_information are disabled by default to prevent accidental memory explosion.
  // Attached metrics are not copied.
  public Game(Game source, int seed){

    main_deck = source.main_deck.copy();
    trash_pile = source.trash_pile.copy();
    start_ups = new ArrayList<StartUp>();
    for(int k=0;k<source.start_ups.size(); k++){
      start_ups.add(source.start_ups.get(k).copy());
    }
    players = new ArrayList<Player>();
    for(int k=0;k<source.players.size(); k++){
      players.add(source.players.get(k).copy());
    }
    rand = new Random(seed); // Pseudo-random number generator.

    round = source.round;
    current_player = source.current_player ;
    phase = source.phase;


    event_queue = new ArrayDeque<Event>();
    Iterator<Event> event_iter = source.event_queue.iterator();
    while(event_iter.hasNext()){
      event_queue.addLast(event_iter.next());
    }

    flipped = new boolean[source.flipped.length]; 
    for(int k=0;k<flipped.length;k++){
      flipped[k] = source.flipped[k];
    }

    completing_project = new ArrayDeque<Integer>();
    Iterator<Integer> cp_iter = source.completing_project.iterator();
    while(cp_iter.hasNext()){
      completing_project.addLast(cp_iter.next());
    }
    completion_winner = new ArrayDeque<Integer>();
    Iterator<Integer> cw_iter = source.completion_winner.iterator();
    while(cw_iter.hasNext()){
      completion_winner.addLast(cw_iter.next());
    }
    completion_spinoff = new ArrayDeque<Boolean>();
    Iterator<Boolean> cs_iter = source.completion_spinoff.iterator();
    while(cs_iter.hasNext()){
      completion_spinoff.addLast(cs_iter.next());
    }

    last_phase = new ArrayDeque<Integer>();
    Iterator<Integer> lp_iter = source.last_phase.iterator();
    while(lp_iter.hasNext()){
      last_phase.addLast(lp_iter.next());
    }

    special_phase_card = new ArrayDeque<Card>();
    Iterator<Card> spc_iter = source.special_phase_card.iterator();
    while(spc_iter.hasNext()){
      special_phase_card.addLast(spc_iter.next().copy());
    }

    special_phase_player = new ArrayDeque<Integer>();
    Iterator<Integer> spp_iter = source.special_phase_player.iterator();
    while(spp_iter.hasNext()){
      special_phase_player.addLast(spp_iter.next());
    }

    skip_next_draw = source.skip_next_draw;
    game_over = source.game_over;
    stepping = false;

    skip_next_completion = new boolean[source.skip_next_completion.length]; 
    for(int k=0;k<skip_next_completion.length;k++){
      skip_next_completion[k] = source.skip_next_completion[k];
    }
    skipping_completion = source.skipping_completion;

    if(flip_after_completion!=null){
      flip_after_completion = new int[source.flip_after_completion.length]; 
      for(int k=0;k<flip_after_completion.length;k++){
        flip_after_completion[k] = source.flip_after_completion[k];
      }
    }
    log = source.log; // Shallow copy log because it should be immutable, but may be needed for AI.
    this.seed = seed;
    starting_main_deck = source.starting_main_deck;
    logging_enabled = false;
    enforce_hidden_information = false;
  }

  // Adds an event at the end of the queue
  public void queueEvent(Event e){
    event_queue.addLast(e);
  }

  //Adds an event at the beginning of the queue.
  public void pushEvent(Event e){
    event_queue.addFirst(e);
  }

  // Begins a completion phase with the selected start up as the completing project if it is valid.
  public void beginCompletionPhase(int which_start_up){
    int project_cards = 0 ;
    for(int k=0;k< start_ups.size();k++){
      project_cards+=start_ups.get(k).project.size();
    }
    if(start_ups.get(which_start_up).project.size() > 0 || project_cards == 0){ // if a valid completion choice or there are no valid choices.
      phase = COMPLETE;
      completing_project.push(which_start_up);
      completion_winner.push(getProjectWinner(which_start_up));
      flip_after_completion = new int[start_ups.size()];
      skipping_completion = skip_next_completion[current_player];
      skip_next_completion[current_player] = false;
      if(logging_enabled){
        text_log.add(players.get(getTurn()).getName() + " selected row " + which_start_up + " to complete." );
      }
    }

  }

  public int getProjectWinner(int which_start_up){
    return start_ups.get(which_start_up).getProjectWinner() ;
  }

  // Flip a card as part of the flip phase.
  public void flipPhaseFlip(int which_project){
    if(!flipped[which_project]){
      flipped[which_project] = true;
      start_ups.get(which_project).flip(1, this);
    }
  }

  // Return the player who's turn it currently is.
  public int getTurn(){
    return current_player;
  }

  // End the game after all currently queued actions complete.
  public void endGame(){
    game_over = true;
  }

  //Draws a card from the main deck or returns null if there is none.
  public Card drawCard(){
    if(main_deck.size() > 0){
      return main_deck.draw();
    }else{
      return null;
    }
  }

  // returns a given player object.
  public Player getPlayer(int player){
    if(player == NOONE){
      return null;
    }else{
      return players.get(player);
    }
  }

  // Returns the number of players.
  public int numPlayers(){
    return players.size();
  }


  public void run() {
    while(!(game_over && event_queue.isEmpty())){
      step();
    }
  }

  public boolean step(){
    stepping = true;
    if(!event_queue.isEmpty()){
      Event current_event = event_queue.pop();
      current_event.execute(this);
      log.add(current_event);
      if(logging_enabled){
        String s = current_event.print(this);
        if(s!=null){
          text_log.add(s);
        }
      }
      for(int k=0;k<metrics.size();k++){
        metrics.get(k).measure(current_event, this);
      }
    }else{
      if(phase == FLIP){
        boolean all_flipped = true;
        for(int k=0;k<flipped.length;k++){
          StartUp s = start_ups.get(k);
          flipped[k] |=  ((s.deck.size() + s.discard.size())  == 0 ); // Note if emptied by an effect (such as open source)
          all_flipped &= flipped[k];
        }
        if(all_flipped){
          phase = DRAW;
        }else{
          queueEvent(new SelectedFlip());
        }
        // Switch to draw phase happens in flipPhaseFlip once all projects have been flipped.
      }else if(phase == DRAW){
        if(skip_next_draw){
          skip_next_draw = false;
          text_log.add("Skipping draw phase for " + players.get(getTurn()).getName() +"." );
        }else{
          queueEvent(new DrawPhase());
        }
        phase = PLAY;
      }else if(phase == PLAY){
        Player p = players.get(current_player);
        if(enforce_hidden_information){
          Player player_backup = p.copy();
          Game sanitized_game = getHiddenInfoCopy(p);
          queueEvent(p.makePlay(sanitized_game));
          p.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
        }else{
          queueEvent(p.makePlay(this));
        }
        // Switch to completion phase happens when a completion play is done and it calls beginCompletionPhase.
      }else if(phase == COMPLETE){
        if(start_ups.get(completing_project.peek()).project.size() > 0 && !skipping_completion){
          queueEvent(new CompleteCard(completing_project.peek(), completion_winner.peek()));
        }else{
          completing_project = new ArrayDeque<Integer>(); // There cannot be more completions after a non special completion.
          completion_winner = new ArrayDeque<Integer>();
          phase = FLIP;
          flipped = new boolean[start_ups.size()];
          for(int k=0;k<start_ups.size();k++){
            StartUp s = start_ups.get(k);
            if(flip_after_completion[k] > 0){
              int amount_flipped = s.flip(flip_after_completion[k], this);
              text_log.add(s.project.printRange(s.project.size()-amount_flipped, amount_flipped) + " flipped after completion in row " + k +".");
            }
            flipped[k] = ((s.deck.size() + s.discard.size())  == 0 ); // Don't have to flip if there are no cards to flip.
          }
          current_player = (current_player+1)%players.size();
          if(current_player == 0){
            round++;
          }

        }
      }else if(phase == SPECIAL_COMPLETE){
        if(start_ups.get(completing_project.peek()).project.size() > 0){
          queueEvent(new CompleteCard(completing_project.peek(), completion_winner.peek(), completion_spinoff.peek()));
        }else{
          phase = last_phase.pop();
          completing_project.pop();
          completion_winner.pop();
          completion_spinoff.pop();

        }
      }else if(phase == SPECIAL_CARD_EXECUTION){
        queueEvent(new CardEvent(special_phase_card.peek(), special_phase_player.peek()));
        phase = last_phase.pop();
        special_phase_card.pop();
        special_phase_player.pop();

      }else{
        System.err.println("Invalid phase:" + phase);
        game_over = true;
      }
    }
    if(game_over){
      String s = "Final score:";
      for(int k=0;k<players.size();k++){
        Player p = players.get(k);
        s+= ", " +p.getName() +" =" + p.finalScore();
      }
      text_log.add(s);
    }
    stepping = false;
    return game_over;
  }

  // Begins a special completion phase, returning to previous phase when done executing the cards in the completion.
  public void beginSpecialCompletion(int source_row, int winner, boolean spinoff) {
    last_phase.push(phase);
    phase = SPECIAL_COMPLETE;
    completing_project.push(source_row);
    completion_winner.push(winner);
    completion_spinoff.push(spinoff);
  }

  // Places a card call back phase onto the stack. The card's callback function will be called before resuming normal play order.
  // This allows special phases to be run "during" a card execution, such as Boom which triggers multiple special complete phases with choices in between.
  public void pushSpecialCardPhase(Card c, Player p){
    last_phase.push(phase);
    phase = SPECIAL_CARD_EXECUTION;
    special_phase_card.push(c);
    special_phase_player.push(p.getPlayerNumber()) ;
  }

  // returns a shuffled deck of copies of all cards whose location is not known to the given player.
  public Deck getAllUnknownCards(Player p){
    Deck unknown_cards = main_deck.copy();
    for(int k=0;k<players.size();k++){
      if(k != p.getPlayerNumber()){
        Deck hand = players.get(k).hand;
        for(int j=0;j<hand.size();j++){
          Card c = hand.getCard(j);
          if(!(c instanceof Money)){
            unknown_cards.add(c.copy());
          }
        }
      }
    }
    return unknown_cards ;
  }

  // Returns a copy of the game hiding all deck, hand, and player info that would not be visible from Player p.
  // Game will be a non-shallow copy of everything using UnknownPlayer and UnknownCard for the removed data.
  // If the game is stepped it will call to the given player if any of the unknown items are encountered.
  public Game getHiddenInfoCopy(Player p){
    Deck all_unknown_cards = getAllUnknownCards(p);
    Game sanitized_game = new Game(this, seed);

    for(int k=0;k<sanitized_game.players.size();k++){
      if(k != p.getPlayerNumber()){// Change all other players to sanitized versions that hide hands and AI.
        sanitized_game.players.set(k, new UnknownPlayer(sanitized_game.getPlayer(k), all_unknown_cards, p));
      }
    }
    // Sanitize unknown decks on the board.
    sanitized_game.main_deck = sanitized_game.main_deck.getUnknown(Player.MAIN_DECK, sanitized_game.getPlayer(p.getPlayerNumber()));
    for(int k=0;k<sanitized_game.start_ups.size();k++){
      StartUp s = sanitized_game.start_ups.get(k);
      s.deck = s.deck.getUnknown(k, p);
    }
    return sanitized_game;
  }

  public void attachMetric(Metric m){
    metrics.add(m);
  }

  public String toString(){
    String s = "Round:" + round + ", Turn: " + current_player+ ", Phase: " + phase +"\n" ;
    s+=" Logging Enabled:" + logging_enabled +", Enforce Hidden Info:" + enforce_hidden_information +"\n";
    s += "Players:\n" ;
    for(int k=0;k<players.size();k++){
      s+= "  " + players.get(k).toString() +"\n";
    }
    s+="Start-ups:\n";
    for(int k=0;k<start_ups.size();k++){
      s+= "  " + start_ups.get(k).toString() +"\n";
    }
    s+= "Trash:" + trash_pile.toString() +"\n";
    s+= "Main Deck:" + main_deck.toString() +"\n";
    return s ;
  }

}
