
public class EpicFail extends Card{

  public static final String EPIC_FAIL_NAME = "Epic Fail";

  public EpicFail(){
    name = EPIC_FAIL_NAME;
  }

  public void executeEpicFail(Player p, int row, Game game, Event e){
    StartUp s = game.start_ups.get(row);
    int trashed = 0;
    // Check for Lawyer interrupt.
    Deck lawyers = Lawyers.getLawyersProtecting(s.project);
    e.addChoice(lawyers.size()); // Log number of lawyers for print purposes.
    if(lawyers.size() > 0){ // There's a lawyer interrupt.
      for(int k=0;k<lawyers.size();k++){
        Card c = lawyers.getCard(k);
        s.project.removeCard(c);
        game.queueEvent(new TrashCard(c, p.getPlayerNumber()));
        game.trash_pile.add(c);
      }
    }else{ // No lawyers
      while(s.project.size() > 0){ // Trash from oldest to newest.
        Card c = s.project.removeCard(0);
        game.queueEvent(new TrashCard(c, p.getPlayerNumber()));
        game.trash_pile.add(c);
        trashed++;
      }
      e.addChoice(trashed);
    }
  //TODO consider bankruptcy rule
  }

  //Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    int row = e.chooseProjectforEffect(player, this, game);
    executeEpicFail(player, row, game, e);
  }

  //Modifies the game state when this card is flipped on the given player's turn.
  public void flip(Player player, int row, Game game, Event e){
    e.addChoice(row);
    executeEpicFail(player, row, game, e);
  }



  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int row = e.readChoice();
    int lawyers = e.readChoice();
    int trashed = e.readChoice();
    if(trashed == 0){
      return p.getName() + " spent Epic Fail on an empty project("+row+"), which did nothing.";
    }else if(lawyers > 0){
      return p.getName() + " spent Epic Fail and trashed " + game.trash_pile.printRange(game.trash_pile.size()-lawyers, lawyers) + " in row " + row +".";
    }else{
      return p.getName() + " spent Epic Fail on row " + row +", which trashed " + game.trash_pile.printRange( game.trash_pile.size()-trashed,trashed) +"." ;
    }
  }

  //Prints a FlipCard Event for this card having just executed its flip command.
  public String flipPrint(Player current, int which_row, Game game, FlipCard e){
    int row = e.readChoice();
    int lawyers = e.readChoice();
    int trashed = e.readChoice();
    if(lawyers > 0){
      return "Epic Fail flipped on row " + row + " and trashed " + game.trash_pile.printRange(game.trash_pile.size()-lawyers, lawyers) + ".";
    }else{
      return "Epic Fail flipped on row " + row +", which trashed " + game.trash_pile.printRange( game.trash_pile.size()-trashed,trashed) +"." ;
    }
  }


  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    // TODO Auto-generated method stub
    return new EpicFail();
  }

}
