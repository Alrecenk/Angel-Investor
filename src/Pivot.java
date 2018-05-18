
public class Pivot extends Card{

  public static final String PIVOT_NAME = "Pivot";

  public Pivot(){
    name = PIVOT_NAME;
  }

  public void executePivot(Player player, Game game, Event e){
    int which_deck = e.chooseDeckforEffect(player,this, game);
    Deck deck = null;
    Card c = null;
    if(which_deck == Player.MAIN_DECK){
      deck = game.main_deck;
      c = game.drawCard();
    }else if(which_deck >= 0){
      deck = game.start_ups.get(which_deck).deck;
      Deck drawn = game.start_ups.get(which_deck).draw(1,game);
      if(drawn.size() > 0){
        c = drawn.getCard(0);
      }
    } 
    if(c == null){ // Pivot targeted an empty deck
      //System.err.println("Pivot pulled a null card from deck " + which_deck);
    }else{
      int keep_or_trash = e.chooseTrashCard(player, c, which_deck, game);
      if(keep_or_trash == Player.KEEP_CARD){
        deck.add(c); // Put it back
      }else if(keep_or_trash == Player.TRASH){
        game.queueEvent(new TrashCard(c, player.getPlayerNumber()));
        game.trash_pile.add(c); // Trash it
      }else{
        System.err.println(player.getName() + " pivot attempted to do something with a card besides keep or trash it:" + keep_or_trash);
      }
    }

  }

  // Modifies the game state when this card is spent by the given player
  public void spend(Player player, Game game, Event e){
    executePivot(player, game, e);

  }

  //Modifies the game state when this card is completed in a project. winner is the winner of that project
  public void complete(Player winner, int row, Game game, Event e){
    executePivot(winner, game, e);
  }


  //Prints a SpendCard event for the this card having just been spent.
  //Note: card will now be on top of trash and not in hand, since game is state after the event.
  public String spendPrint(Player p, Game game, SpendCard e) {
    int which_deck = e.readChoice();
    int keep_or_trash = e.readChoice();
    String s = p.getName() + " spent Pivot on ";
        if(which_deck == Player.MAIN_DECK){
          s+= "the main deck";
        }else{
          s+="start-up " + which_deck;
        }
    if(keep_or_trash == Player.KEEP_CARD){
      s+= " and chose to put the card back.";
    }else if(keep_or_trash == Player.TRASH){
      s+= " and chose to trash " + game.trash_pile.getCard(game.trash_pile.size()-2).name +"."; // Pivot is on top so trashed card is below it.
    }
    return s;
  }


  //Prints a CompleteCard event for the this card having just been completed
  //Note: card will now be on top of startup.discard, since game is state after the event.
  public String completePrint(Player winner, int which_row, Game game, CompleteCard e) {
    int which_deck = e.readChoice();
    int keep_or_trash = e.readChoice();
    String s = winner.getName() + " completed Pivot and targeted ";
        if(which_deck == Player.MAIN_DECK){
          s+= "the main deck";
        }else{
          s+="start-up " + which_deck;
        }
    if(keep_or_trash == Player.KEEP_CARD){
      s+= " and chose to put the card back.";
    }else if(keep_or_trash == Player.TRASH){
      s+= " and chose to trash " + game.trash_pile.getCard(game.trash_pile.size()-1).name +"."; // Trashed card is on top
    }
    return s;
  }

  //returns whether this card is valid for a beginning game start-up project (i.e. it has a flip or complete action on it).
  public boolean canBeStartingCard(){
    return true;
  }

  //How many points this card is worth if it's in a player's hand at the end of the game.
  public double endGamePoints(){
    return 0 ;
  }

  //Returns a non-shallow copy of this card.
  public Card copy(){
    return new Pivot();
  }

  }
