
public class Poach extends Card{

public static final String POACH_NAME = "Poach";
  
  public Poach(){
    name = POACH_NAME;
  }
  
  //Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    int target = e.choosePlayerforEffect(player, this, game);
    Deck hand =  game.getPlayer(target).getHand();
    if(hand.size() >0){
    int which_card = game.rand.nextInt(hand.size());
    e.addChoice(which_card);
      Card c = hand.removeCard(which_card);
      if(c instanceof Money){
        game.trash_pile.add(c);
        e.addChoice(1);
      }else{
        player.add(c);
        e.addChoice(0);
      }
    }else{
      e.addChoice(-1); // If impossible log that in place of position.
      
    }
  }


  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int target = e.readChoice();
    int which_card = e.readChoice();
    boolean was_money = (e.readChoice() == 1) ;
    String s = p.getName() + " spent Poach";
    if(which_card >= 0){
      if(was_money){
        s+= ", got a " + Money.MONEY_NAMES[target] + " from " + game.getPlayer(target).getName() +", and trashed it.";
      }else{
        s+= ", got a non-money card from " + game.getPlayer(target).getName() +", put it in their hand.";
      }
    }else{
      s += " and targeted " + game.getPlayer(target).getName() +", but they had no cards in hand.";
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
    return new Poach();
  }

}
