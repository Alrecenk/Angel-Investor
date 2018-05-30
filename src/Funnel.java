
public class Funnel extends Card{

  public static final String FUNNEL_NAME = "Funnel";

  public Funnel(){
    name = FUNNEL_NAME;
  }

  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    int from_row = e.chooseProjectforEffect(player, this, game);
    Deck source = game.start_ups.get(from_row).project;
    Money m = null;
    for(int k=0;k<source.size();k++){
      Card c = source.getCard(k);
      if(c instanceof Money && ((Money)c).player == player.getPlayerNumber()){
        m = (Money)source.removeCard(k);
        break;
      }
    }
    int to_row = Player.NOWHERE;
    // Can't move a card that doesn't exist or if there's only one row.
    if(m==null || game.start_ups.size() < 2){
      e.addChoice(Player.NOWHERE); 
    }else{
      to_row = e.chooseProjectforEffect(player, this, game);

      while(to_row == from_row){
        //System.err.println(player.getName() +" tried to funnel a money into the same project, which is technically illegal, requesting new choice...");
        to_row = e.chooseProjectforEffect(player, this, game);
      }
      Deck destination = game.start_ups.get(to_row).project;
      destination.add(m);
    }

  }

  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int from_row = e.readChoice();
    int to_row = e.readChoice();
    String s = p.getName() + " spent Funnel and selected row " + from_row;
    if(to_row == Player.NOWHERE){
      s+= ", which did not contain any of their money so nothing happened.";
    }else{
      while(to_row == from_row){ // Cycle through any invalid choices.
        to_row = e.readChoice();
      }
      s+= " and moved a " + Money.MONEY_NAMES[p.getPlayerNumber()] + " to row " + to_row;
    }
    return s;
  }

  public boolean canBeStartingCard() {
    return false;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Funnel();
  }

}
