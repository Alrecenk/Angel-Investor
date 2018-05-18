
public class PublicityStunt extends Card{

  public static final String PUBLICITY_STUNT_NAME = "Publicity Stunt";



  public PublicityStunt(){
    name = PUBLICITY_STUNT_NAME;
  }

  public void executeStunt(Player p, int this_location, Game game, Event e){
    int money_index = -1;
    Deck hand = p.getHand();
    for(int k=0;k<hand.size();k++){
      if(hand.getCard(k) instanceof Money){
        money_index = k;
        break;
      }
    }
    e.addChoice(money_index); // Log the index so print can know if you didn't have it.
    int trash_or_keep = Player.KEEP_CARD ;
    if(money_index >= 0){
      Card c = hand.getCard(money_index);
      trash_or_keep = e.chooseTrashCard(p, c, Player.HAND, game);
    }
    if(trash_or_keep == Player.TRASH){
      game.trash_pile.add(hand.removeCard(money_index));
      p.giveFame();
    }else if(trash_or_keep == Player.KEEP_CARD){
      if(this_location != Player.TRASH){ // If it was spent this card will already end up in trash.
        game.start_ups.get(this_location).project.removeCard(this);
        game.trash_pile.add(this);
      }
    }else{
      System.err.println("Invalid response to trash or keep request:" + trash_or_keep);
    }

  }

  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    executeStunt(player, Player.TRASH, game, e);
  }

  //Modifies the game state when this card is invested by the given player.
  public void invest(Player player, int row, Game game, Event e){
    executeStunt(player, row, game, e);
  }

  //Modifies the game state when this card is completed in a project. winner is the winner of that project
  public void complete(Player winner, int row, Game game, Event e){
    executeStunt(winner, row, game, e);
  }

  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    String s = p.getName() + " spent Publicity Stunt";
    int money_index = e.readChoice();
    if(money_index < 0){
      s+= ", but didn't trash a money, so nothing happened.";
    }else{
      int keep_or_trash = e.readChoice();
      if(keep_or_trash == Player.KEEP_CARD){
        s+= ", but didn't trash a money, so nothing happened.";
      }else{
        s+= ", and trashed a money to gain a fame (now has " + p.getFame() + ").";
      }
    }
    return s ;
  }

  //Prints an InvestCard event for this card having just been invested and executed.
  //Note: card will now be at end of project.
  public String investPrint(Player p, int which_row, Game game, InvestCard e) {
    String s = p.getName() + " invested Publicity Stunt in row " + which_row;
    int money_index = e.readChoice();
    if(money_index < 0){
      s+= ", but didn't trash a money, so it was trashed immediately.";
    }else{
      int keep_or_trash = e.readChoice();
      if(keep_or_trash == Player.KEEP_CARD){
        s+= ", but didn't trash a money, so it was trashed immediately.";
      }else{
        s+= ", and trashed a money to gain a fame (now has " + p.getFame() + ").";
      }
    }
    return s ;
  }

  //Prints a CompleteCard event for the this card having just been completed
  //Note: card will now be on top of startup.discard, since game is state after the event.
  public String completePrint(Player winner, int which_row, Game game, CompleteCard e) {
    String s = winner.getName() + " completed Publicity Stunt";
    int money_index = e.readChoice();
    if(money_index < 0){
      s+= ", but didn't trash a money, so Publicity Stunt was trashed.";
    }else{
      int keep_or_trash = e.readChoice();
      if(keep_or_trash == Player.KEEP_CARD){
        s+= ", but didn't trash a money, so Publicity Stunt was trashed.";
      }else{
        s+= ", and trashed a money to gain a fame (now has " + winner.getFame() + ").";
      }
    }
    return s ;
  }


  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 1;
  }

  public Card copy() {
    return new PublicityStunt();
  }

}
