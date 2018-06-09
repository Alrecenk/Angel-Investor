// Records and queues the choices players make when playing.
// Also calls out to sanitization to make sure player can't consider hidden information or modify their own game state.

import java.util.ArrayList;


public class PlayPhase extends Event{

  public boolean sanitize;

  public PlayPhase(boolean sanitize){
    this.sanitize = sanitize;
  }

  public void execute(Game game) {
    resetReadPointer(); // Necessary when iterating over logs.
    Player player = game.getPlayer(game.getTurn());
    Player player_backup = player;
    Game sanitized_game = game;
    if(sanitize){
      player_backup = player.copy();
      sanitized_game = game.getHiddenInfoCopy(player);
    }
    int p[] = choosePlay(player, sanitized_game);
    int card = p[0], location = p[1];
    if(card < 0){
      game.queueEvent(new CompleteProject(location));
    }else if(location == Player.TRASH){
      game.queueEvent(new SpendCard(card));
    }else{
      game.queueEvent(new InvestCard(card, location));
    }
    if(sanitize){
      player.copyGameStateFrom(player_backup); // Roll back any modifications the player made to their core game data but maintain consistent reference
    }
  }

  public String print(Game game) {
    return null ; // This is a choice logging event that doesn't  change the game state.
  }

  public Event copy() {
    return new PlayPhase(sanitize);
  }

  public ArrayList<Choice> getPossibleChoices(Game game) {
    ArrayList<Choice> possibilities = new ArrayList<Choice>();
    // Add completions
    for(int k=0;k<game.start_ups.size();k++){
      if(game.start_ups.get(k).project.size() > 0){
        Choice c = new Choice();
        c.addChoiceArray(new int[]{Player.NOWHERE, k});
        possibilities.add(c);
      }
    }
    //Add card plays
    Player player = game.getPlayer(game.getTurn());
    Deck hand = player.getHand();
    for(int k = 0; k < hand.size(); k++){
      Choice c = new Choice();
      c.addChoiceArray(new int[]{k, Player.TRASH}); // Spend
      possibilities.add(c);
      for(int j=0; j<game.start_ups.size(); j++){
        c = new Choice();
        c.addChoiceArray(new int[]{k, j}); // Invest
        possibilities.add(c);
      }
    }
    return possibilities;
  }

  public int getUnknownCardLocation(Game game) {
    return Player.NOWHERE ;
  }

  public int getChoosingPlayer(Game game) {
    return game.getTurn();
  }

}
