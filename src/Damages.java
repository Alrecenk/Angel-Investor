
public class Damages extends Card{
  
  public static final String DAMAGES_NAME = "Damages";
  
  
  
  public Damages(){
    name = DAMAGES_NAME;
  }
  
//Modifies the game state when this card is spent by the given player
public void spend(Player player, Game game, Event e){
  Card retrieved = null;
  String target = Money.MONEY_NAMES[player.getPlayerNumber()];
  for(int k=0; k<game.trash_pile.size(); k++){
    Card c = game.trash_pile.getCard(k);
    if(c.name == target){
      retrieved = game.trash_pile.removeCard(k);
      break;
    }
  }
  if(retrieved == null){
    e.addChoice(0); // Fail
  }else{
    e.addChoice(1); // Succeed
    player.add(retrieved);
  }
}

//Prints a SpendCard event for the this card having just been spent.
//Note: card will now be on top of trash and not in hand, since game is state after the event.
public String spendPrint(Player p, Game game, SpendCard e) {
  int success = e.readChoice();
  if(success == 1){
    return p.getName() + " spent Damages and recovered a " + Money.MONEY_NAMES[p.getPlayerNumber()] + " from the trash.";
  }else{
    return p.getName() + " spent Damages, which did nothing.";
  }
}

  public boolean canBeStartingCard() {
    return false;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Damages();
  }

}
