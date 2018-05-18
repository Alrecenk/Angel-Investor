
public class OpenSource extends Card{

  public static final String OPEN_SOURCE_NAME = "Open Source";


  public OpenSource(){
    name = OPEN_SOURCE_NAME;
  }
//TODO use special card phase to allow choosing after the flip action.
//TODO consider bloat rule
  private void executeOpenSource(Player player, Game game, Event e) {
    boolean[] flipped = new boolean[game.start_ups.size()];
    boolean all_flipped = true;
    for(int k=0;k<game.start_ups.size();k++){
      StartUp s = game.start_ups.get(k);
      flipped[k] = ((s.deck.size() + s.discard.size())  == 0 ); // Don't have to flip if there are no cards to flip.
      all_flipped &= flipped[k];
    }
    while(!all_flipped){
      int which_project = e.chooseProjectforEffect(player, this, game);
      if(!flipped[which_project]){
        flipped[which_project] = true;
        game.start_ups.get(which_project).flip(1, game);
        all_flipped = true;
        for(int k=0;k<flipped.length;k++){
          all_flipped &= flipped[k];
        }
        e.addChoice(1); // Mark successful
      }else{
        e.addChoice(0); // Mark unsuccessful
      }
    }
  }

  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    executeOpenSource(player, game, e);
  }

  //Modifies the game state when this card is flipped on the given player's turn.
  public void flip(Player player, int row, Game game, Event e){
    executeOpenSource(player, game, e);
  }

  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    String s = p.getName() + " spent Open Source.";
    int row = e.readChoice();
    boolean valid = (e.readChoice() == 1);
    boolean[] open_sourced = new boolean[game.start_ups.size()];
    while(row >= 0){
      if(!open_sourced[row] && valid){ // Skip rows that were picked more than once.
        open_sourced[row] = true;
        Deck project = game.start_ups.get(row).project;
        s+= " " + p.getName() +" flipped " + project.getCard(project.size()-1).name +" on row " + row +".";
      }
      row = e.readChoice();
      valid = (e.readChoice() == 1);
    }
    return s ;
  }



  //Prints a FlipCard Event for this card having just executed its flip command.
  public String flipPrint(Player current, int which_row, Game game, FlipCard e){
    String s = "Open Source triggered on flip.";
    int row = e.readChoice();
    boolean valid = (e.readChoice() == 1);
    boolean[] open_sourced = new boolean[game.start_ups.size()];
    while(row >= 0){
      if(!open_sourced[row] && valid){ // Skip rows that were picked more than once.
        open_sourced[row] = true;
        Deck project = game.start_ups.get(row).project;
        s+= " " + current.getName() +" flipped " + project.getCard(project.size()-1).name +" on row " + row +".";
      }
      row = e.readChoice();
      valid = (e.readChoice() == 1);
    }
    return s ;
  }



  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new OpenSource();
  }

}
