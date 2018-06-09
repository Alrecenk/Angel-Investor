import java.util.ArrayList;

// An event is any action that can happen in the game.
// The actual actions will be overrridden by extending classes.
// The shared class logs the choices made by players, and provides pass-through functions for player choices (to allow replaying from logs wit hthe same code).
public abstract class Event {

  protected Choice selection = new Choice();

  // Shared static switch for all events tells them to run from logs instead of asking players for choices.
  public static boolean execute_from_log = false;

  // Execute an event from the event queue. Reqeusting any choices from the players as they come up.
  public abstract void execute(Game game);


  // Print the event assuming it was just executed (i.e. game is the game state right after execution).
  public abstract String print(Game game);

  //Select which start-up to flip next during the flip phase.
  public int chooseFlip(Player p, Game game){
    if(execute_from_log){
      return selection.readChoice();
    }else{
      if(game.enforce_hidden_information){
        Player player_backup = p.copy();
        Game sanitized_game = game.getHiddenInfoCopy(p);
        int choice = p.chooseFlip(sanitized_game);
        p.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
        selection.addChoice(choice);
        return choice ;
      }else{
        int choice = p.chooseFlip(game);
        selection.addChoice(choice);
        return choice ;
      }
    }
  }

  // Select second draw. Should return either MAIN_DECK or CASH_RESERVE
  public int selectDrawLocation(Player p, Game game){
    if(execute_from_log){
      return selection.readChoice();
    }else{
      if(game.enforce_hidden_information){
        Player player_backup = p.copy();
        Game sanitized_game = game.getHiddenInfoCopy(p); 
        int choice = p.selectDrawLocation(sanitized_game);
        p.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
        selection.addChoice(choice);
        return choice ;
      }else{
        int choice = p.selectDrawLocation(game);
        selection.addChoice(choice);
        return choice ;
      }
    }
  }


  // Returns the index of a project to select for a given card's effect that targets a project(such as Epic Fail or Patent).
  public int chooseProjectforEffect(Player p, Card c, Game game){
    if(execute_from_log){
      return selection.readChoice();
    }else{
      if(game.enforce_hidden_information){
        Player player_backup = p.copy();
        Game sanitized_game = game.getHiddenInfoCopy(p);
        int choice = p.chooseProjectforEffect(c.copy(), sanitized_game);
        p.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
        selection.addChoice(choice);
        return choice ;
      }else{
        int choice = p.chooseProjectforEffect(c, game);
        selection.addChoice(choice);
        return choice ;
      }
    }
  }

  // Returns the {project index, card index} for a given card's effect that targets a card in a project (such as sabotage or delay).
  public int[] chooseProjectCardforEffect(Player p, Card c, Game game){
    if(execute_from_log){
      return selection.readChoiceArray();
    }else{
      if(game.enforce_hidden_information){
        Player player_backup = p.copy();
        Game sanitized_game = game.getHiddenInfoCopy(p); 
        int[] choice = p.chooseProjectCardforEffect(c.copy(), sanitized_game);
        p.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
        selection.addChoiceArray(choice);
        return choice ;
      }else{
        int[] choice = p.chooseProjectCardforEffect(c, game);
        selection.addChoiceArray(choice);
        return choice ;
      }
    }
  }

  // returns the project index or MAIN_DECK for a given card's effect that target a deck (such as pivot or planning).
  public int chooseDeckforEffect(Player p, Card c, Game game){
    if(execute_from_log){
      return selection.readChoice();
    }else{
      if(game.enforce_hidden_information){
        Player player_backup = p.copy();
        Game sanitized_game = game.getHiddenInfoCopy(p); 
        int choice = p.chooseDeckforEffect(c.copy(), sanitized_game);
        p.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
        selection.addChoice(choice);
        return choice ;
      }else{
        int choice = p.chooseDeckforEffect(c, game);
        selection.addChoice(choice);
        return choice ;
      }
    }
  }

  //returns the player index for a given card's effect that targets a player (such as poach or scandal).
  public int choosePlayerforEffect(Player p, Card c, Game game){
    if(execute_from_log){
      return selection.readChoice();
    }else{
      if(game.enforce_hidden_information){
        Player player_backup = p.copy();
        Game sanitized_game = game.getHiddenInfoCopy(p); 
        int choice = p.choosePlayerforEffect(c.copy(), sanitized_game);
        p.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
        selection.addChoice(choice);
        return choice ;
      }else{
        int choice = p.choosePlayerforEffect(c, game);
        selection.addChoice(choice);
        return choice ;
      }
    }
  }

  // Returns whether to trash a card (TRASH_CARD or KEEP_CARD) when given the option (such as in pivot or publicity stunt).
  public int chooseTrashCard(Player p, Card c, int location, Game game){
    if(execute_from_log){
      return selection.readChoice();
    }else{
      if(game.enforce_hidden_information){
        Player player_backup = p.copy();
        Game sanitized_game = game.getHiddenInfoCopy(p); 
        int choice = p.trashCard(c.copy(), location, sanitized_game);
        p.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
        selection.addChoice(choice);
        return choice ;
      }else{
        int choice = p.trashCard(c, location, game);
        selection.addChoice(choice);
        return choice ;
      }
    }
  }
  
  public int[] choosePlay(Player p, Game game){
    if(execute_from_log){
      return selection.readChoiceArray();
    }else{
      if(game.enforce_hidden_information){
        Player player_backup = p.copy();
        Game sanitized_game = game.getHiddenInfoCopy(p); 
        int[] choice = p.choosePlay(sanitized_game);
        p.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
        selection.addChoiceArray(choice);
        return choice ;
      }else{
        int[] choice = p.choosePlay(game);
        selection.addChoiceArray(choice);
        return choice ;
      }
    }
  }

  // Returns the indexes into the given cards for the ordering from bottom to top of cards that are being put back in Planning. Cards left out will be discarded.
  public int[] reorderOrDiscard(Player p, Deck c, int location, Game game){
    if(execute_from_log){
      return selection.readChoiceArray();
    }else{
      if(game.enforce_hidden_information){
        Player player_backup = p.copy();
        Game sanitized_game = game.getHiddenInfoCopy(p); 
        int[] choice = p.reorderOrDiscard(c.copy(), location, sanitized_game);
        p.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
        selection.addChoiceArray(choice);
        return choice ;
      }else{
        int[] choice = p.reorderOrDiscard(c, location, game);
        selection.addChoiceArray(choice);
        return choice ;
      }
    }
  }

  public void addChoice(int a){
    if(!execute_from_log){
      selection.addChoice(a);
    }
  }

  public void addChoiceArray(int[] a){
    if(!execute_from_log){
      selection.addChoiceArray(a);
    }
  }

  public int readChoice(){
    return selection.readChoice();
  }

  public int[] readChoiceArray(){
    return selection.readChoiceArray();
  }

  public void resetReadPointer(){
    selection.resetReadPointer();
  }

  // Copies an event for entire game duplication.
  public abstract Event copy();


  // Returns a list of all possible choices that could result on this event.
  // Should be called while the event is on the head of the event queue about to be executed.
  //Used for players that rely on the game engine for predicting future states.
  public abstract ArrayList<Choice> getPossibleChoices(Game game);

  // Returns where this Event will access an UnknownCard or Player.NOWHERE if it won't.
  //Should be called while the event is on the head of the event queue about to be executed.
  //Used for players that rely on the game engine for predicting future states.
  public abstract int getUnknownCardLocation(Game game);

  
  // Returns the player number of the player making the choice available in the getPossibleChoices() set.
  // Will only be called after getPossibleChoices if it returns non-null
  public abstract int getChoosingPlayer(Game game);

}
