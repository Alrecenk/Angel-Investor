import java.util.Random;


public class RandomPlayer extends Player{

  Random rand;
  int seed ;
  public RandomPlayer(int seed){
    this.seed = seed;
    rand = new Random(seed);
  }
  
  public int chooseFlip(Game game) {
    return rand.nextInt(game.start_ups.size());
  }

  public int selectDrawLocation(Game game) {
    return rand.nextDouble() < .5 ? Player.CASH_RESERVE : Player.MAIN_DECK;
  }

  public int[] choosePlay(Game game) {
    if(hand.size() > 0 && rand.nextDouble()  < .8){
      if(rand.nextDouble()  < .5){
        return new int[]{(int)(rand.nextDouble() *hand.size()), Player.TRASH};
      }else{
        return new int[]{(int)(rand.nextDouble() *hand.size()), (int)(rand.nextDouble() *game.start_ups.size())};
      }
    }else{
      return new int[]{Player.CHOICE, (int)(rand.nextDouble() *game.start_ups.size())};
    }
    
  }

  public int chooseProjectforEffect(Card c, Game game) {
    return (int)(rand.nextDouble() * game.start_ups.size());
  }

  public int[] chooseProjectCardforEffect(Card c, Game game) {
    int project = (int)(rand.nextDouble() *game.start_ups.size());
    while(game.start_ups.get(project).project.size() == 0){ // Try for a project with cards in it.
      project = (int)(rand.nextDouble() *game.start_ups.size()); 
    }
    int card = (int)(rand.nextDouble() *game.start_ups.get(project).project.size());
    
    return new int[]{project, card};
  }

  public int chooseDeckforEffect(Card c, Game game) {
    if(rand.nextDouble() < .25){
      return Player.MAIN_DECK;
    }else{
      return (int)(rand.nextDouble() *game.start_ups.size());
    }
  }

  public int choosePlayerforEffect(Card c, Game game) {
    return (int)(rand.nextDouble() *game.numPlayers());
  }

  public int trashCard(Card c, int location, Game game) {
    return rand.nextDouble() <.5 ? Player.TRASH : Player.KEEP_CARD;
  }

  public int[] reorderOrDiscard(Deck c, int location, Game game) {
    return new int[]{}; // Not technically random but screw it, discard 'em all.
  }

  public String getStatus() {
    return "behaving chaotically";
  }

  public Player copy() {
    RandomPlayer r = new RandomPlayer(seed);
    r.copyGameStateFrom(this);
    return r;
  }
  

}
