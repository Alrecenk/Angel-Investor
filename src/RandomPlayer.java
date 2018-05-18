
public class RandomPlayer extends Player{

  public int chooseFlip(Game game) {
    return (int)(Math.random()*game.start_ups.size());
  }

  public int selectDrawLocation(Game game) {
    return Math.random() < .5 ? Player.CASH_RESERVE : Player.MAIN_DECK;
  }

  public Event makePlay(Game game) {
    if(hand.size() > 0 && Math.random() < .8){
      if(Math.random() < .5){
        return new SpendCard((int)(Math.random()*hand.size()));
      }else{
        return new InvestCard((int)(Math.random()*hand.size()), (int)(Math.random()*game.start_ups.size()));
      }
    }else{
      return new CompleteProject((int)(Math.random()*game.start_ups.size()));
    }
    
  }

  public int chooseProjectforEffect(Card c, Game game) {
    return (int)(Math.random()*game.start_ups.size());
  }

  public int[] chooseProjectCardforEffect(Card c, Game game) {
    int project = (int)(Math.random()*game.start_ups.size());
    if(game.start_ups.get(project).project.size() == 0){ // Try for a project with cards in it.
      project = (int)(Math.random()*game.start_ups.size()); 
    }
    int card = (int)(Math.random()*game.start_ups.get(project).project.size());
    
    return new int[]{project, card};
  }

  public int chooseDeckforEffect(Card c, Game game) {
    if(Math.random() < .25){
      return Player.MAIN_DECK;
    }else{
      return (int)(Math.random()*game.start_ups.size());
    }
  }

  public int choosePlayerforEffect(Card c, Game game) {
    return (int)(Math.random()*game.numPlayers());
  }

  public int trashCard(Card c, int location, Game game) {
    return Math.random()<.5 ? Player.TRASH : Player.KEEP_CARD;
  }

  public int[] reorderOrDiscard(Deck c, int location, Game game) {
    return new int[]{}; // Not technically random but screw it, discard 'em all.
  }

  public String getStatus() {
    return "behaving chaotically";
  }

  public Player copy() {
    RandomPlayer r = new RandomPlayer();
    r.copyGameStateFrom(this);
    return r;
  }
  

}
