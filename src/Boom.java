
public class Boom extends Card{

  public static final String BOOM_NAME = "Boom";

  boolean[] completed;
  
  public Boom(){
    name = BOOM_NAME;
  }

  // Returns true if every project has either been completed or has no cards in it.
  private boolean allCompleted(Game game){
    boolean all_completed = true;
    for(int k=0;k<game.start_ups.size();k++){
      StartUp s = game.start_ups.get(k);
      all_completed &= completed[k] || (s.project.size() == 0 );// Don't have to complete if there are no cards.
    }
    return all_completed;
  }


  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    completed = new boolean[game.start_ups.size()];
    game.pushSpecialCardPhase(this, player);
    
  }

  //Modifies the game state when this card is completed in a project. winner is the winner of that project
  public void complete(Player winner, int row, Game game, Event e){
    completed = new boolean[game.start_ups.size()];
    completed[row] = true;
    game.pushSpecialCardPhase(this, winner);
    // Finish completing the row before executing Boom for other rows (that's how it's worded).
    game.beginSpecialCompletion(row, winner.getPlayerNumber(), false); 
  } 
  
  // Executes the boom choice calls between completion phase.
  public void special(Player player, Game game, Event e){
    if(!allCompleted(game)){
      int which_project = e.chooseProjectforEffect(player, this, game);
      if(!completed[which_project] && game.start_ups.get(which_project).project.size() > 0 ){ // Valid selection
        completed[which_project] = true;
        e.addChoice(1); // Mark successful
        int winner = game.getProjectWinner(which_project);
        e.addChoice(winner);
        game.pushSpecialCardPhase(this, player);
        game.beginSpecialCompletion(which_project, winner, false);
      }else{ // Invalid selection
        e.addChoice(0); // Mark unsuccessful
        game.pushSpecialCardPhase(this, player);
      }
    }
    
  }

  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    return p.getName() + " spent Boom.";
  }

  //Prints a CompleteCard event for the this card having just been completed
  //Note: card will now be on top of startup.discard, since game is state after the event.
  public String completePrint(Player winner, int which_row, Game game, CompleteCard e) {
    return winner.getName() + " completed boom, which will activate after this row.";
  }


  public String specialPrint(Player p, Game game, CardEvent e) {
    int which_project = e.readChoice();
    boolean success = e.readChoice() == 1;
    if(success){
      int winner = e.readChoice();
      String s = p.getName() +" used Boom to complete row " + which_project +" with ";
      if(winner == Game.NOONE){
        s += "no" ;
      }else{
        s += game.getPlayer(winner).getName() + " as the";
      }
      s+= " winner.";
      return s ;
    }else{
      return null;
    }
  }
  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Boom();
  }

}
