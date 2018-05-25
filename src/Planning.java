
public class Planning extends Card{

  public static final String PLANNING_NAME = "Planning";

  public Planning(){
    name = PLANNING_NAME;
  }

  public void executePlanning(Player player, Game game, Event e){
    int which_deck = e.chooseDeckforEffect(player, this, game);
    Deck drawn = new Deck();
    Deck source, discard;
    boolean main = which_deck == Player.MAIN_DECK;
    if(main){
      source = game.main_deck;
      discard = game.trash_pile;
      while(drawn.size() < 3 && source.size() > 0){
        drawn.add(source.draw());
      }
    }else{
      source = game.start_ups.get(which_deck).deck;
      discard = game.start_ups.get(which_deck).discard;
      drawn.add(game.start_ups.get(which_deck).draw(3, game));
    }
    
    if(drawn.size() > 0){
      int[] reorder = e.reorderOrDiscard(player, drawn, which_deck, game);
      boolean kept[] = new boolean[drawn.size()];
      for(int k=0 ; k < reorder.length;k++){
        int i = reorder[k];
        kept[i] = true;
        source.add(drawn.getCard(i));
      }
      int discarded = 0 ;
      for(int k=0;k<kept.length;k++){
        if(!kept[k]){
          Card c = drawn.getCard(k);
          discard.add(c);
          discarded++;
          if(main){
            game.queueEvent(new TrashCard(c, player.getPlayerNumber()));
          }
        }
      }
      e.addChoice(discarded);
    }else{
      e.addChoiceArray(new int[0]);
      e.addChoice(0);
    }

  }

  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    executePlanning(player, game, e);
  }

  //Modifies the game state when this card is completed in a project. winner is the winner of that project
  public void complete(Player winner, int row, Game game, Event e){
    executePlanning(winner, game, e);
  }

  
  public String print(Player p, Game game, Event e, boolean spent){
    //TODO Fix bug: often prints itself instead of trashed card when used on main deck.
    int which_deck = e.readChoice();
    int[] picked = e.readChoiceArray();
    int discarded = e.readChoice();
    String s = p.getName() + " " + (spent ? "spent" : "completed") + " Planning and selected" ;
    if(which_deck == Player.MAIN_DECK){
      s+= " the main deck. Then they" ;
      if(picked.length + discarded == 0){
        s+= " did nothing because it was empty";
      }
      if(picked.length == 1){
        s+= " put 1 card back";
      }else if(picked.length >= 2){
        s+= " put " + picked.length + " cards back";
      }
      if(discarded > 0){
        if(picked.length >0){
          s+= " and";
        }
        s+= " trashed " + game.trash_pile.printRange(game.trash_pile.size()-(spent?1:0)-discarded, discarded);
      }
    }else{
      s+= " row " + which_deck +". Then they" ;
      if(picked.length + discarded == 0){
        s+= " did nothing because it was empty";
      }
      if(picked.length == 1){
        s+= " put 1 card back";
      }else if(picked.length >= 2){
        s+= " put " + picked.length + " cards back";
      }
      if(discarded > 0){
        if(picked.length >0){
          s+= " and";
        }
        StartUp r = game.start_ups.get(which_deck);
        s+= " discarded " + r.discard.printRange(r.discard.size()-discarded, discarded)  ;
      }
    }
    s += ".";
    return s;
  }
  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    return print(p, game, e, true);
  }

  //Prints a CompleteCard event for the this card having just been completed
  //Note: card will now be on top of startup.discard, since game is state after the event.
  public String completePrint(Player winner, int which_row, Game game, CompleteCard e) {
    return print(winner, game, e, false);
  }

  public boolean canBeStartingCard() {
    return true;
  }

  public double endGamePoints() {
    return 0;
  }

  public Card copy() {
    return new Planning();
  }

}
