import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


public class BasicPlayer extends Player implements Comparable {

  Random rand;
  int seed ;
  double money_draw_chance ;

  public HashMap<String, Double> card_complete_score = new HashMap<String, Double>();
  public HashMap<String, Double> card_flip_score = new HashMap<String, Double>();
  public HashMap<String, Double> card_startup_score = new HashMap<String, Double>();
  public double new_win_for_invest_multiplier ; // how good is it to invest money to turn a tie into a win
  public double new_tie_for_invest_multiplier ; // how good is it to invest money to create a tie from a loss
  public double general_investing_multiplier ; // General boost to long term investing of non-money
  public double max_epic_fail_score ; // Maximum row score for an Epic Fail to be spent
  public double max_bust_score ; // Maximum score for a bust to be used.
  public double min_boom_score ; // Minimum score for a boom to be spent
  public double min_spinoff_score ; // Minimum score to spend a spinoff
  
  
  public boolean logging_enabled = false;
  public int wins = 0 ;

  public BasicPlayer(int seed){
    this.seed = seed;
    rand = new Random(seed);
    initializeBasicWeights();
  }
  
  public void setToVector(double v[]){
    int k = 8;
    money_draw_chance = Math.max(0, Math.min(v[0],1));
    general_investing_multiplier = v[1];
    new_win_for_invest_multiplier = v[2];
    new_tie_for_invest_multiplier = v[3];
    max_epic_fail_score = v[4];
    max_bust_score = v[5] ;
    min_boom_score = v[6] ;
    min_spinoff_score = v[7];
    
    
    Iterator<String> i = card_flip_score.keySet().iterator();
    while(i.hasNext()){
      String key = i.next();
      card_flip_score.put(key,v[k]);
      k++;
    }
    
    i = card_complete_score.keySet().iterator();
    while(i.hasNext()){
      String key = i.next();
      card_complete_score.put(key,v[k]);
      k++;
    }
    
    i = card_startup_score.keySet().iterator();
    while(i.hasNext()){
      String key = i.next();
      card_startup_score.put(key,v[k]);
      k++;
    }
  }
  
  public double[] getVector(){
    int k = 8;
    double v[] = new double[k + card_flip_score.size() + card_complete_score.size() + card_startup_score.size()];
    v[0] = money_draw_chance;
    v[1] = general_investing_multiplier;
    v[2] = new_win_for_invest_multiplier ;
    v[3] = new_tie_for_invest_multiplier ;
    v[4] = max_epic_fail_score ;
    v[5] = max_bust_score  ;
    v[6] = min_boom_score  ;
    v[7] = min_spinoff_score ;
    
    
    Iterator<String> i = card_flip_score.keySet().iterator();
    while(i.hasNext()){
      String key = i.next();
      v[k] = card_flip_score.get(key);
      k++;
    }
    
    i = card_complete_score.keySet().iterator();
    while(i.hasNext()){
      String key = i.next();
      v[k] = card_complete_score.get(key);
      k++;
    }
    
    i = card_startup_score.keySet().iterator();
    while(i.hasNext()){
      String key = i.next();
      v[k] = card_startup_score.get(key);
      k++;
    }
    return v;
  }

  public void initializeBasicWeights(){
    money_draw_chance = .5 ;
    general_investing_multiplier = 5;
    new_win_for_invest_multiplier = 1;
    new_tie_for_invest_multiplier = .5;
    max_epic_fail_score = -10;
    max_bust_score = -10;
    min_boom_score = 10 ;
    min_spinoff_score = 10;
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
  
  public void initializeRandomWeights(Random rand){
    money_draw_chance = rand.nextDouble() ;
    general_investing_multiplier = rand.nextDouble()*40-20;
    new_win_for_invest_multiplier = rand.nextDouble()*10-5;
    new_tie_for_invest_multiplier = rand.nextDouble()*10-5;
    max_epic_fail_score = rand.nextDouble()*20-10;
    max_bust_score = rand.nextDouble()*20-10;
    min_boom_score = rand.nextDouble()*20-10 ;
    // Points
    card_flip_score.put(ViralMarketing.VIRAL_MARKETING_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(ViralMarketing.VIRAL_MARKETING_NAME, rand.nextDouble()*40-20);

    card_complete_score.put(PressRelease.PRESS_RELEASE_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(PressRelease.PRESS_RELEASE_NAME, rand.nextDouble()*40-20);

    card_complete_score.put(Infamy.INFAMY_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Infamy.INFAMY_NAME, rand.nextDouble()*40-20);

    card_complete_score.put(PublicityStunt.PUBLICITY_STUNT_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(PublicityStunt.PUBLICITY_STUNT_NAME, rand.nextDouble()*40-20);

    // Common cards
    card_complete_score.put(Capital.CAPITAL_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Capital.CAPITAL_NAME, rand.nextDouble()*40-20);

    card_complete_score.put(Profit.PROFIT_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Profit.PROFIT_NAME, rand.nextDouble()*40-20);

    card_complete_score.put(Sabotage.SABOTAGE_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Sabotage.SABOTAGE_NAME, rand.nextDouble()*40-20);

    card_complete_score.put(Pivot.PIVOT_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Pivot.PIVOT_NAME, rand.nextDouble()*40-20);

    card_complete_score.put(Planning.PLANNING_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Planning.PLANNING_NAME, rand.nextDouble()*40-20);

    card_flip_score.put(EpicFail.EPIC_FAIL_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(EpicFail.EPIC_FAIL_NAME, rand.nextDouble()*40-20);

    // Other stuff
    card_flip_score.put(Rush.RUSH_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Rush.RUSH_NAME, rand.nextDouble()*40-20);

    card_startup_score.put(Documentation.DOCUMENTATION_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Breakthrough.BREAKTHROUGH_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Nonprofit.NONPROFIT_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Underdog.UNDERDOG_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Lawyers.LAWYERS_NAME, rand.nextDouble()*40-20);
    
    card_complete_score.put(Patent.PATENT_NAME, rand.nextDouble()*40-20);
    card_startup_score.put(Patent.PATENT_NAME, rand.nextDouble()*40-20);

  }
  
  public void initializeTrainedWeights(){
    
    
    double v[] = new double[]{0.6688093051729413, 2.2747678808284872, 0.43750637690031624, 0.264411794714825, -3.5669829609223545, 5.272931333537395, 2.3671053974084284,
        1.2427303336394244, 21.965105601469237, -3.0303030303030303, -0.8080808080808088, 24.242424242424242, -0.6060606060606064, 2.828282828282828, 22.82828282828283, 
        0.898989898989899, 1.2929292929292928, 1.393939393939394, 0.04040404040404055, 5.030303030303031, 5.97979797979798, 1.1717171717171717, 0.3232323232323231, 0.8181818181818181,
        0.404040404040404, -0.2222222222222222, 0.31313131313131315, 1.616161616161616, -20.606060606060606, 6.969696969696969, 2.4646464646464645, 2.7272727272727275, -0.04040404040404039, 
        1.0000000000000002, -0.6262626262626263, -3.737373737373738, 0.10101010101010077};
    
    setToVector(v);
  }

  public int chooseFlip(Game game) {
    return rand.nextInt(game.start_ups.size());
  }

  public int selectDrawLocation(Game game) {
    return rand.nextDouble() < money_draw_chance ? Player.CASH_RESERVE : Player.MAIN_DECK;
  }

  public int[] choosePlay(Game game) {
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
    if(hand.countCard(Bust.BUST_NAME) > 0 && total_project_value < max_bust_score){
      return new int[]{hand.findCard(Bust.BUST_NAME), Player.TRASH};
    }
    if(hand.countCard(Boom.BOOM_NAME) > 0 && total_project_value > min_boom_score){
      return new int[]{hand.findCard(Boom.BOOM_NAME), Player.TRASH};
    }
    // Spend Epic Fail if a negative row exists
    if(hand.countCard(EpicFail.EPIC_FAIL_NAME) > 0 && worst_complete_score < max_epic_fail_score){
      return new int[]{hand.findCard(EpicFail.EPIC_FAIL_NAME), Player.TRASH};
    }
    
    // Spend Spinoff if a psotive row exists
    if(hand.countCard(Spinoff.SPINOFF_NAME) > 0 && best_complete_score > min_spinoff_score){
      return new int[]{hand.findCard(Spinoff.SPINOFF_NAME), Player.TRASH};
    }
    
    // Spend damages if there's money to get
    if(hand.countCard(Damages.DAMAGES_NAME) > 0 && game.trash_pile.countCard(Money.MONEY_NAMES[player_number]) > 0){
      return new int[]{hand.findCard(Damages.DAMAGES_NAME), Player.TRASH};
    }
    
    // Delay if a negative row (there's probably opposing money)
    if(hand.countCard(Delay.DELAY_NAME) > 0 && worst_complete_score < 0){
      return new int[]{hand.findCard(Delay.DELAY_NAME), Player.TRASH};
    }
    
    // Spend Scandal whenever you get it
    if(hand.countCard(Scandal.SCANDAL_NAME) > 0){
      return new int[]{hand.findCard(Scandal.SCANDAL_NAME), Player.TRASH};
    }
    
    // Spend Poach whenever you get it
    if(hand.countCard(Poach.POACH_NAME) > 0){
      return new int[]{hand.findCard(Poach.POACH_NAME), Player.TRASH};
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
        return new int[]{hand.findCard(Taxes.TAXES_NAME), Player.TRASH};
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
        return new int[]{hand.findCard(Money.MONEY_NAMES[player_number]), best_invest};
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
      return new int[]{best_invest_card, best_invest_location};
    }

    if(logging_enabled){
      System.out.println("Completing project " + best_complete_row + " scoring : " + best_complete_score + " .");
    }

    
    // Complete your best project if nothing else good to do.
    return new int[]{Player.CHOICE, best_complete_row};   

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

  
  public int compareTo(Object other) {
    return ((BasicPlayer)other).wins - wins;
  }
  
  // Crosses two weight vectors to create a child by selecting each value from a random parent.
  public static double[] cross(double a[], double b[], Random rand){
    double c[] = new double[a.length];
    for(int k=0;k<a.length;k++){
      c[k] = rand.nextDouble() < .5 ? a[k] : b[k];
    }
    return c ;
  }
  
  // Mutates a weight vector by adding -s to size to mutations weights.
  public static double[] mutate(double a[], int mutations, double size, Random rand){
    double c[] = new double[a.length];
    for(int k=0;k<a.length;k++){
      c[k] = a[k];
    }
    for(int j=0;j<mutations;j++){
      int i = rand.nextInt(a.length);
      c[i] += (rand.nextDouble()-.5)*2*size;
    }
    return c ;
  }
  
  // Plays every AI in the array against every other AI, accumulates the wins on the BasicPlayer wins variable
  public static void playAllMatches(BasicPlayer[] population, Random rand){
    for(int k=1;k<population.length;k++){
      for(int j=0;j<k;j++){
        Game g = new Game(new Player[]{population[k], population[j]}, Deck.getMainDeck(), rand.nextInt());
        g.run();
        if(population[k].finalScore() > population[j].finalScore()){
          population[k].wins++;
        }else if(population[j].finalScore() > population[k].finalScore()){
          population[j].wins++;
        }
      }
    }
  }
  
  public static int weightedRandomSelect(int weights[], Random rand){
    int r = rand.nextInt(weights[weights.length-1]);
    int min_k= 0, max_k = weights.length - 1;
    while(max_k - min_k  > 1){
      int mid_k = (min_k+max_k)/2;
      if(weights[mid_k] <= r){
        min_k = mid_k;
      }else{
        max_k = mid_k;
      }
    }
    return min_k;
  }
    
  public static void print(double[] v){
    String s = "{" + v[0] ;
    for(int k=1; k < v.length;k++){
      s+=", " +v[k];
    }
    s+= "}";
    System.out.println(s);
  }
  
  public static void main(String args[]){
    axisSearch();
  }
  
  public static BasicPlayer[] rangeMutate(BasicPlayer bp, int index, int amount){
    double v[] = bp.getVector();
    BasicPlayer population[] = new BasicPlayer[amount];
    for(int k=0;k<amount;k++){
      population[k] = new BasicPlayer(k*index*amount);
      double nv[] = new double[v.length];
      for(int j=0;j<v.length;j++){
        nv[j] = v[j];
      }
      nv[index] = -v[index] + (4*v[index]*k) / (amount-1);
      population[k].setToVector(nv);
    }
    return population;
  }
    
  public static void axisSearch(){
    int generations = 100;
    int population_size = 100;
    BasicPlayer winner = new BasicPlayer(0);
    int weights = winner.getVector().length;
    Random rand = new Random();
    for(int g = 0 ; g < generations; g++){
      for(int i = 0 ;i < weights;i++){
        BasicPlayer population[] = rangeMutate(winner, i, population_size);
        //System.out.println("Generation " + g +" playing games...");
        playAllMatches(population, rand);
        Arrays.sort(population); // Sort by wins descending.
        double v[] = population[0].getVector();
        v[i] = (population[0].getVector()[i] + population[1].getVector()[i] + population[2].getVector()[i]+ population[3].getVector()[i])/4.0;
        winner = new BasicPlayer(1);
        winner.setToVector(v);
        System.out.println("Generation " + g + " index " + i + " Best So Far:");
        print(winner.getVector());
        System.out.println ("Top 3:" + population[0].getVector()[i] +", " + population[1].getVector()[i] +", " + population[2].getVector()[i]+", " + population[3].getVector()[i]);
      }
    }
  }
  
  
  
  public static void geneticAlgorithm(){
    int population_size = 20;
    int keep = 5;
    int random = 15;
    int generations = 1000;
    double mutations = 0;
    double mutation_size = 1;
    
    BasicPlayer[] population = new BasicPlayer[population_size];
    Random rand = new Random();
    population[0] = new BasicPlayer(0); // Add in default unmodified
    population[1] = new BasicPlayer(1);
    population[1].initializeTrainedWeights();// Add in last trained
    for(int k=2;k<population_size;k++){
      population[k] = new BasicPlayer(k);
      double v[] = population[k].getVector();
      v = mutate(v, (int)mutations, mutation_size, rand);
      population[k].setToVector(v);
    }
    
    boolean extra_logging = false;
    
    for(int g = 0 ; g < generations;g++){
      System.out.println("Generation " + g +" playing games...");
      playAllMatches(population, rand);
      Arrays.sort(population); // Sort by wins descending.
      int[] sum_wins = new int[population_size/2];
      int total_wins = 0 ;
      for(int k=0;k<sum_wins.length;k++){
        total_wins += population[k].wins;
        sum_wins[k] = total_wins;
        if(extra_logging){
          System.out.println("AI " + k + " wins: " + population[k].wins);
        }
      }
      System.out.println("Best So Far:");
      print(population[0].getVector());
      
      
      BasicPlayer[] new_population = new BasicPlayer[population_size];
      new_population[0] = new BasicPlayer(-g);
      for(int k=1;k<keep;k++){
        new_population[k] = population[k];
        new_population[k].wins = 0 ;
      }
      for(int k=keep; k < keep + random;k++){
        new_population[k] = new BasicPlayer(k*g);
        new_population[k].initializeRandomWeights(rand);
      }
      
      for(int k=keep+random;k<population_size;k++){
        int p1 = weightedRandomSelect(sum_wins, rand);
        int p2 = weightedRandomSelect(sum_wins, rand);
        if(extra_logging){
          System.out.println( "Breeding: " + p1 + " and " + p2);
        }
        double v[] = cross(population[p1].getVector(), population[p2].getVector(), rand);
        v = mutate(v, (int)mutations, mutation_size, rand);
        new_population[k] = new BasicPlayer(k*g);
        new_population[k].setToVector(v);
      }
      population = new_population;
      mutation_size *= .9;
    }
    
  }
    
    
  
  

}

  
