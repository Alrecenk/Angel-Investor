
public class Scandal extends Card{

  public static final String SCANDAL_NAME = "Scandal";
  
  public Scandal(){
    name = SCANDAL_NAME;
  }
  
  //Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    int t = e.choosePlayerforEffect(player, this, game);
    Deck hand =  game.getPlayer(t).getHand();
    e.addChoice(hand.size());
    while(hand.size() > 0){
      Card c = hand.draw();
      game.trash_pile.add(c);
      game.queueEvent(new TrashCard(c, player.getPlayerNumber()));
      
    }
  }


  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int t = e.readChoice();
    int trashed = e.readChoice();
    String s = p.getName() + " spent Scandal and targeted " + game.getPlayer(t).getName() +". " + game.getPlayer(t).getName();
    if(trashed == 0){
      s += " had no cards in hand.";
    }else{
      s+= " trashed " + game.trash_pile.printRange(game.trash_pile.size()-1-trashed,trashed) +"." ; // Scandl spent is on top so -1
    }
    return s ;
  }

  public boolean canBeStartingCard() {
    return false;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Scandal();
  }

}
