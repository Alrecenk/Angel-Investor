
public class Bust extends Card{

  public static final String BUST_NAME = "Bust";

  public Bust(){
    name = BUST_NAME;
  }

  public void executeBust(Player p, Game game, Event e){
    int num_lawyers = 0;;
    for(int row = 0 ; row < game.start_ups.size();row++){
      StartUp s = game.start_ups.get(row);
      // Check for Lawyer interrupt.
      num_lawyers += Lawyers.getLawyersProtecting(s.project).size();
    }
    e.addChoice(num_lawyers); // Log number of lawyers for print purposes.
    if(num_lawyers > 0){ // There's a lawyer interrupt.
      for(int row = 0 ; row < game.start_ups.size();row++){
        StartUp s = game.start_ups.get(row);
        Deck lawyers = Lawyers.getLawyersProtecting(s.project);
        for(int k=0;k<lawyers.size();k++){
          Card c = lawyers.getCard(k);
          s.project.removeCard(c);
          game.queueEvent(new TrashCard(c, p.getPlayerNumber()));
          game.trash_pile.add(c);
        }
      }
    }else{ // No lawyers
      int trashed = 0 ;
      for(int row = 0 ; row < game.start_ups.size();row++){
        StartUp s = game.start_ups.get(row);
        while(s.project.size() > 0){ // Trash from oldest to newest.
          Card c = s.project.removeCard(0);
          game.queueEvent(new TrashCard(c, p.getPlayerNumber()));
          game.trash_pile.add(c);
          trashed++;
        }
      }
      e.addChoice(trashed);
    }
  //TODO Definitely consider bankruptcy rule
  }

  //Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    executeBust(player, game, e);
  }

  //Modifies the game state when this card is flipped on the given player's turn.
  public void flip(Player player, int row, Game game, Event e){
    executeBust(player, game, e);
  }

  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int lawyers = e.readChoice();
    int trashed = e.readChoice();
    if(trashed == 0){
      return p.getName() + " spent Bust, which did nothing.";
    }else if(lawyers > 0){
      return p.getName() + " spent Bust and trashed " + game.trash_pile.printRange(game.trash_pile.size()-1-lawyers, lawyers) + ".";
    }else{
      return p.getName() + " spent Bust, which trashed " + game.trash_pile.printRange( game.trash_pile.size()-1-trashed,trashed) +"." ;
    }
  }

  //Prints a FlipCard Event for this card having just executed its flip command.
  public String flipPrint(Player current, int which_row, Game game, FlipCard e){
    int lawyers = e.readChoice();
    int trashed = e.readChoice();
    if(lawyers > 0){
      return "Bust flipped on row " + which_row + " and trashed " + game.trash_pile.printRange(game.trash_pile.size()-lawyers, lawyers) + ".";
    }else{
      return "Bust flipped on row " + which_row +", which trashed " + game.trash_pile.printRange( game.trash_pile.size()-trashed,trashed) +"." ;
    }
  }


  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Bust();
  }

}
