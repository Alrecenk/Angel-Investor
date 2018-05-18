
public class Money extends Card{

  public int player = 0 ;
  public static final String[] MONEY_NAMES = new String[]{"Dollar", "Euro", "Yen", "Bitcoin"};

  public Money(int p){
    player = p ;
    name = Money.MONEY_NAMES[p];
  }

  public void spend(Player player, Game game, Event e) {
    Deck hand = player.getHand();
    int spent_money = 1 ;
    for(int k=0;k<hand.size();k++){
      if(hand.getCard(k) instanceof Money){
        Card c = hand.getCard(k);
        int trash_or_keep = e.chooseTrashCard(player, c, Player.HAND, game);
        if(trash_or_keep == Player.TRASH){
          game.trash_pile.add(hand.removeCard(k));
          spent_money++;
          k--;
        }else if(trash_or_keep == Player.KEEP_CARD){
          break;
        }else{
          System.err.println("Invalid response to trash or keep request:" + trash_or_keep);
        }
      }
    }
    while(spent_money > 1){ // Player gets n-1 cards for spending n money
      spent_money--;
      Card c = game.drawCard();
      if(c == null){
        game.endGame();
      } else {
        player.add(c);
      }
    }
  }

  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int spent_money = 1;
    while(e.readChoice() == Player.TRASH){
      spent_money++;
    }
    String s = p.getName() + " spent " + getName(spent_money);
    if(spent_money == 1){
      s += ", which did nothing";
    }else if(spent_money == 2){
      s += " and drew 1 card";
    }else{
      s += " and drew " + (spent_money-1) + " cards";
    }
    if(game.game_over){
      s += ", which ended the game.";
    }else{
      s += ".";
    }
    return s;
  }

  public String getName(int amount){
    if(amount == 1 || player == 1 || player == 2){ // Euro and Yen never get an s
      return amount + " " + MONEY_NAMES[player];
    }else{
      return amount + " " + MONEY_NAMES[player] + "s";
    }
  }

  public boolean canBeStartingCard() {
    return false;
  }

  public Card copy() {
    return new Money(player);
  }

  public double endGamePoints() {
    return 0;
  }

}
