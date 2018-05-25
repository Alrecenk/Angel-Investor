import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


public class BasicPlayer extends Player{

  Random rand;
  int seed ;
  double money_draw_chance ;

  public HashMap<String, Double> card_complete_score = new HashMap<String, Double>();
  public HashMap<String, Double> card_flip_score = new HashMap<String, Double>();
  public HashMap<String, Double> card_startup_score = new HashMap<String, Double>();
  public double new_win_for_invest_multiplier ; // how good is it to invest money to turn a tie into a win
  public double new_tie_for_invest_multiplier ; // how good is it to invest money to create a tie from a loss
  public double general_investing_multiplier ; // General boost to long term investing of non-money

  public boolean logging_enabled = false;

  public BasicPlayer(int seed){
    this.seed = seed;
    rand = new Random(seed);
    initializeBasicWeights();
  }

  public void initializeBasicWeights(){
    money_draw_chance = .5 ;
    general_investing_multiplier = 5;
    new_win_for_invest_multiplier = 1;
    new_tie_for_invest_multiplier = .5;
    // Points
    card_flip_score.put(ViralMarketing.VIRAL_MARKETING_NAME, 20.0);
    card_startup_score.put(ViralMarketing.VIRAL_MARKETING_NAME, 10.0);

    card_complete_score.put(PressRelease.PRESS_RELEASE_NAME, 20.0);
    card_startup_score.put(PressRelease.PRESS_RELEASE_NAME, 8.0);

    card_complete_score.put(Infamy.INFAMY_NAME, 20.0);
    card_startup_score.put(Infamy.INFAMY_NAME, 8.0);

    card_complete_score.put(PublicityStunt.PUBLICITY_STUNT_NAME, 10.0);
    card_startup_score.put(PublicityStunt.PUBLICITY_STUNT_NAME, 5.0);

    // Common cards
    card_complete_score.put(Capital.CAPITAL_NAME, 2.0);
    card_startup_score.put(Capital.CAPITAL_NAME, 1.0);

    card_complete_score.put(Profit.PROFIT_NAME, 1.0);
    card_startup_score.put(Profit.PROFIT_NAME, .5);

    card_complete_score.put(Sabotage.SABOTAGE_NAME, 3.0);
    card_startup_score.put(Sabotage.SABOTAGE_NAME, 1.0);

    card_complete_score.put(Pivot.PIVOT_NAME, 2.0);
    card_startup_score.put(Pivot.PIVOT_NAME, 1.0);

    card_complete_score.put(Planning.PLANNING_NAME, 4.0);
    card_startup_score.put(Planning.PLANNING_NAME, 2.0);

    card_flip_score.put(EpicFail.EPIC_FAIL_NAME, -10.0);
    card_startup_score.put(EpicFail.EPIC_FAIL_NAME, -10.0);

    // Other stuff
    card_flip_score.put(Rush.RUSH_NAME, -10.0);
    card_startup_score.put(Rush.RUSH_NAME, 10.0);

    card_startup_score.put(Documentation.DOCUMENTATION_NAME, 4.0);
    card_startup_score.put(Breakthrough.BREAKTHROUGH_NAME, 3.0);
    card_startup_score.put(Nonprofit.NONPROFIT_NAME, -20.0);
    card_startup_score.put(Underdog.UNDERDOG_NAME, -10.0);
    card_startup_score.put(Lawyers.LAWYERS_NAME, 1.0);
    
    card_complete_score.put(Patent.PATENT_NAME, 8.0);
    card_startup_score.put(Patent.PATENT_NAME, 4.0);

  }

  public int chooseFlip(Game game) {
    return rand.nextInt(game.start_ups.size());
  }

  public int selectDrawLocation(Game game) {
    return rand.nextDouble() < money_draw_chance ? Player.CASH_RESERVE : Player.MAIN_DECK;
  }

  public Event makePlay(Game game) {
    if(logging_enabled){
      System.out.println("Calculating general statistics...");
    }
    // Calculate some general stats about the board
    int winner[] = new int[game.start_ups.size()] ; // Who would win each project if it were completed right now
    int total_cards[] = new int[game.start_ups.size()] ; // Total cards in each start-up
    double current_win_chance[][] = new double[game.start_ups.size()][]; // Each Start-ups overall win chance for each player
    double project_complete_value[] = new double[game.start_ups.size()]; // Expected value of completing each project
    double project_flip_value[] = new double[game.start_ups.size()];// Expected value of the next flip 
    double start_up_card_value[] = new double[game.start_ups.size()]; // Overall expected value of non-money in start-up
    double expected_project_cards[] = new double[game.start_ups.size()] ; // Expected number of cards in a project on completion
    for(int k = 0 ; k < game.start_ups.size(); k ++){
      winner[k] = game.getProjectWinner(k);
      StartUp s = game.start_ups.get(k);
      expected_project_cards[k] = game.start_ups.size(); // Amount of flips per turn is approximation of average cards in project without breakthroughs
      total_cards[k] = s.project.size() + s.deck.size() + s.discard.size();
      for(int j = 0 ; j < s.project.size();j++){
        String card_name = s.project.getCard(j).name ;
        if(card_complete_score.containsKey(card_name)){
          project_complete_value[k] += card_complete_score.get(card_name);
        }
        if(card_startup_score.containsKey(card_name)){
          start_up_card_value[k] += card_startup_score.get(card_name);
        }
      }
      for(int j = 0 ; j < s.discard.size();j++){
        String card_name = s.discard.getCard(j).name ;
        if(card_startup_score.containsKey(card_name)){
          start_up_card_value[k] += card_startup_score.get(card_name);
        }
      }
      Deck start_up_deck = s.deck;
      if(s.deck.size() > 0 && s.deck.getCard(0) instanceof UnknownCard){ // Check for anonymized deck.
        start_up_deck = ((UnknownCard)s.deck.getCard(0)).possible_cards;
      }
      for(int j = 0 ; j < start_up_deck.size();j++){
        String card_name = start_up_deck.getCard(j).name ;
        if(card_flip_score.containsKey(card_name)){
          project_flip_value[k] += card_flip_score.get(card_name);
        }
        if(card_startup_score.containsKey(card_name)){
          start_up_card_value[k] += card_startup_score.get(card_name);
        }
        if(card_name == Breakthrough.BREAKTHROUGH_NAME){
          expected_project_cards[k] += expected_project_cards[k]*3.0/total_cards[k];
        }
      }
      project_flip_value[k] /= (start_up_deck.size()+1); 
      current_win_chance[k] = s.winProbabilities((int)expected_project_cards[k]) ; 
    }

    if(logging_enabled){
      System.out.println("Determining completion values...");
    }

    // Calculate what you would complete right now if you did.
    double best_complete_score = -99999999 ;
    int best_complete_row = 0;
    double worst_complete_score = 99999999;
    double total_project_value = 0;
    for(int k=0;k<winner.length;k++){
      if(game.start_ups.get(k).project.size() > 0){ // Only consider projects with cards for doing stuff
        double complete_score = 0 ;
        if(winner[k] == player_number){
          complete_score = project_complete_value[k] - project_flip_value[k]; // Don't want to complete things if good flips could happen for us
          total_project_value += project_complete_value[k];
        }else if(winner[k] >= 0){
          complete_score = project_flip_value[k] - project_complete_value[k]; // Reverse sore if giving to other player
          total_project_value -= project_complete_value[k];
        }
        total_project_value += complete_score;
        if(complete_score > best_complete_score){
          best_complete_score = complete_score;
          best_complete_row = k;
        }
        if(complete_score < worst_complete_score){
          worst_complete_score = complete_score;
        }
        //System.out.println("row " + k + " - " + complete_score);
      }
    }


    //Bust and boom based on total complete score
    if(hand.countCard(Bust.BUST_NAME) > 0 && total_project_value < 0){
      return new SpendCard(hand.findCard(Bust.BUST_NAME));
    }
    if(hand.countCard(Boom.BOOM_NAME) > 0 && total_project_value > 0){
      return new SpendCard(hand.findCard(Boom.BOOM_NAME));
    }
    // Spend Epic Fail if a negative row exists
    if(hand.countCard(EpicFail.EPIC_FAIL_NAME) > 0 && worst_complete_score < 0){
      return new SpendCard(hand.findCard(EpicFail.EPIC_FAIL_NAME));
    }
    
    // Spend Spinoff if a psotive row exists
    if(hand.countCard(Spinoff.SPINOFF_NAME) > 0 && best_complete_score > 0){
      return new SpendCard(hand.findCard(Spinoff.SPINOFF_NAME));
    }
    
    // Spend damages if there's money to get
    if(hand.countCard(Damages.DAMAGES_NAME) > 0 && game.trash_pile.countCard(Money.MONEY_NAMES[player_number]) > 0){
      return new SpendCard(hand.findCard(Damages.DAMAGES_NAME));
    }
    
    // Delay if a negative row (there's probably opposing money)
    if(hand.countCard(Delay.DELAY_NAME) > 0 && worst_complete_score < 0){
      return new SpendCard(hand.findCard(Delay.DELAY_NAME));
    }
    
    // Spend Scandal whenever you get it
    if(hand.countCard(Scandal.SCANDAL_NAME) > 0){
      return new SpendCard(hand.findCard(Scandal.SCANDAL_NAME));
    }
    
    // Spend Poach whenever you get it
    if(hand.countCard(Poach.POACH_NAME) > 0){
      return new SpendCard(hand.findCard(Poach.POACH_NAME));
    }
    
    // Spend Taxes if you're winning by two in visible fame
    if(hand.countCard(Taxes.TAXES_NAME) > 0){
      int my_fame = getFame();
      int max_opponent_fame = -999;
      for(int k=0;k<game.players.size();k++){
        if(k!= player_number && game.getPlayer(k).getFame() > max_opponent_fame){
          max_opponent_fame = game.getPlayer(k).getFame() ;
        }
      }
      if(my_fame - max_opponent_fame >= 2){
        return new SpendCard(hand.findCard(Taxes.TAXES_NAME));
      }
    }
    

    if(logging_enabled){
      System.out.println("Thinking about investing money...");
    }

    // If got money, invest some money probably
    if(hand.countCard(Money.MONEY_NAMES[player_number]) > 0){
      int best_invest = -1;
      double best_invest_score = 0 ;
      for(int k=0;k<winner.length;k++){
        StartUp s = game.start_ups.get(k);
        StartUp ns = s.copy();
        ns.project.add(new Money(player_number));
        int new_winner = ns.getProjectWinner();
        double invest_score = 0 ;
        // Immediate benefit of changing the winner of a project by investing money
        if(winner[k] != player_number && new_winner == player_number){ // If creates a winning_project
          invest_score += new_win_for_invest_multiplier * (project_complete_value[k] + project_flip_value[k]);
        } else if(new_winner == Game.NOONE && winner[k] >= 0 && winner[k] != player_number){ // Tie-ing a previously lost project
          invest_score += new_tie_for_invest_multiplier * (project_complete_value[k] + project_flip_value[k]);
        }
        // Future benefit of changing overall win probabilities
        double new_win_chance[] = ns.winProbabilities((int)expected_project_cards[k]);
        for(int j=0; j < new_win_chance.length;j++){
          // Change in probability of winning cards with value
          double delta = start_up_card_value[k] * (new_win_chance[j]/(double)(total_cards[k]+1) - current_win_chance[k][j]/(double)total_cards[k]);
          if(j == player_number){
            invest_score +=delta;
          }else{
            invest_score -= delta;
          }
        }
        if(invest_score > best_invest_score){
          best_invest = k;
          best_invest_score = invest_score;
        }
      }
      if(best_invest >= 0){
        return new InvestCard(hand.findCard(Money.MONEY_NAMES[player_number]), best_invest);
      }
    }

    if(logging_enabled){
      System.out.println("Thinking about investing cards...");
    }

    // Consider investing some cards that have value in start_ups.
    Iterator<String> invest_iterator = card_startup_score.keySet().iterator();
    int best_invest_card = -1;
    int best_invest_location = -1;
    double best_invest_score = 0 ;
    while(invest_iterator.hasNext()){ // For each possible card
      String card_name = invest_iterator.next();
      if(hand.countCard(card_name) > 0){ // If you've got it
        double startup_value = 0;
        if(card_startup_score.containsKey(card_name)){
          startup_value += card_startup_score.get(card_name);
        }
        double project_value = 0;
        if(card_complete_score.containsKey(card_name)){
          project_value += card_complete_score.get(card_name);
        }
        if(card_flip_score.containsKey(card_name)){
          project_value += card_flip_score.get(card_name);
        }
        for(int k=0;k<winner.length;k++){ // For each place you could invest it
          double invest_value = 0;
          // Points for project you can win
          if(winner[k] == player_number){
            invest_value += project_value;
          }else if(winner[k] >= 0){
            invest_value -= project_value  ;
          }
          // Points for general startup quality
          for(int j=0;j<current_win_chance[k].length;j++){
            double delta = general_investing_multiplier * startup_value * current_win_chance[k][j]/total_cards[k];
            if(j == player_number){
              invest_value += delta;
            }else{
              invest_value -= delta;
            }
          }
          if(invest_value > best_invest_score){
            best_invest_score = invest_value;
            best_invest_card = hand.findCard(card_name);
            best_invest_location = k;
          }
        }
      }
    }
    if(best_invest_card >= 0){
      return new InvestCard(best_invest_card, best_invest_location);
    }

    if(logging_enabled){
      System.out.println("Completing project " + best_complete_row + " scoring : " + best_complete_score + " .");
    }

    
    // Complete your best project if nothing else good to do.
    return new CompleteProject(best_complete_row);   

  }

  public int chooseProjectforEffect(Card c, Game game) {

    // 10% chance of doing something random 'cause this thing is stupid
    // It tries to do invalid things, and this prevents and infinite loop as random will always eventually work.
    if(Math.random() < .1){
      return (int)(rand.nextDouble() * game.start_ups.size());
    }
    
 // Calculate some general stats about the board
    int winner[] = new int[game.start_ups.size()] ; // Who would win each project if it were completed right now
    int total_cards[] = new int[game.start_ups.size()] ; // Total cards in each start-up
    double project_complete_value[] = new double[game.start_ups.size()]; // Expected value of completing each project
    for(int k = 0 ; k < game.start_ups.size(); k ++){
      winner[k] = game.getProjectWinner(k);
      StartUp s = game.start_ups.get(k);
      total_cards[k] = s.project.size() + s.deck.size() + s.discard.size();
      for(int j = 0 ; j < s.project.size();j++){
        String card_name = s.project.getCard(j).name ;
        if(card_complete_score.containsKey(card_name)){
          project_complete_value[k] += card_complete_score.get(card_name);
        }
      }
    }


    // Calculate best to trash right now
    int best_complete_row = 0 ;
    double best_complete_score = -99999;
    int worst_complete_row = 0 ;
    double worst_complete_score = 99999;
    for(int k=0;k<winner.length;k++){
      double complete_score = 0 ;
      if(winner[k] == player_number){
        complete_score = project_complete_value[k] ; 
      }else if(winner[k] >= 0){
        complete_score = - project_complete_value[k]; 
      }
      if(complete_score < worst_complete_score){
        worst_complete_score = complete_score;
        worst_complete_row = k;
      }
      if(complete_score > best_complete_score){
        best_complete_score = complete_score;
        best_complete_row = k;
      }
    }
    
    if(c.name == EpicFail.EPIC_FAIL_NAME || c.name == Patent.PATENT_NAME){
      return worst_complete_row;
    }

    if(c.name == Spinoff.SPINOFF_NAME){
      return best_complete_row;
    }

    return (int)(rand.nextDouble() * game.start_ups.size());
  }

  public int[] chooseProjectCardforEffect(Card c, Game game) {
    // if it's a sabotageor delay then try to select an opponent's money
    if(c.name == Sabotage.SABOTAGE_NAME || c.name == Delay.DELAY_NAME){
      for(int k=0;k<game.start_ups.size();k++){
        for(int j=0;j<game.start_ups.get(k).project.size();j++){
          Card t = game.start_ups.get(k).project.getCard(j);
          if(t instanceof Money && ((Money)t).player != player_number){
            return new int[]{k,j};
          }
        }
      }
    }

    //Fall back to random if you don't know what to do.
    int project = (int)(rand.nextDouble() *game.start_ups.size());
    while(game.start_ups.get(project).project.size() == 0){ // Try for a project with cards in it.
      project = (int)(rand.nextDouble() *game.start_ups.size()); 
    }
    int card = (int)(rand.nextDouble() *game.start_ups.get(project).project.size());

    return new int[]{project, card};
  }

  public int chooseDeckforEffect(Card c, Game game) {
    // Always select a start_up deck to try to get rid of opponent's money
    return (int)(rand.nextDouble() *game.start_ups.size());
  }

  public int choosePlayerforEffect(Card c, Game game) {
    // Usually attacks, so select player with most cards and points
    int best_player = 0;
    double best_score = -1;
    for(int k = 0; k < game.numPlayers();k++){
      double score = game.getPlayer(k).getHand().size() + .4 *game.getPlayer(k).getFame() ;
      if(k!= player_number && score > best_score){
        best_score = score;
        best_player = k;
      }
    }
    return best_player;
  }

  public int trashCard(Card c, int location, Game game) {
    if(c instanceof Money){
      int player = ((Money)c).player;
      // Always trash other players' money
      // If the location is your hand then it's probably a publicity stunt which you want to do if possible
      if(player != player_number || location == Player.HAND){
        return Player.TRASH;
      }
    }
    return Player.KEEP_CARD; // Other stuff just keep I guess?
  }

  public int[] reorderOrDiscard(Deck d, int location, Game game) {
    ArrayList<Integer> keep  = new ArrayList<Integer>();
    for(int k = 0 ; k < d.size();k++){
      Card c = d.getCard(k);
      if(c instanceof Money){ // Drop money not mine
        if(((Money)c).player == player_number){ // keep if mine
          keep.add(k);
        }
      }else{
        keep.add(k); // keep other stuff
      }
    }    
    int kp[] = new int[keep.size()];
    for(int k=0;k<kp.length;k++){
      kp[k] = keep.get(k);
    }

    return kp; 
  }

  public String getStatus() {
    return "Wingin' it.";
  }

  public Player copy() {
    BasicPlayer r = new BasicPlayer(seed);
    r.copyGameStateFrom(this);
    return r;
  }


}
