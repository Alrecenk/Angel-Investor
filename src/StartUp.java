import java.util.Random;


public class StartUp {

  public Deck deck;
  public Deck discard;
  public Deck project;
  
  // Creates Start-up at the beginning of the game with a starting card and generating on of each player's money.
  public StartUp(Card starting_card, int players, Random rand){
    project = new Deck();
    project.add(starting_card);
    deck = new Deck();
    for(int k=0;k<players;k++){
      deck.add(new Money(k));
    }
    deck.shuffle(rand);
    discard = new Deck();
  }
  
  // Creates an empty start-up (used for spin-off and other cards that may create start-ups)
  public StartUp(){
    discard = new Deck();
    deck = new Deck();
    project = new Deck();
  }
  
  // Attempts to flip a given amount, return the amount successfully flipped.
  public int flip(int amount, Game game){
    int flipped = 0 ;
    for(int k=0;k<amount;k++){
      if(deck.size() == 0 && discard.size() > 0){
        discard.moveAllTo(deck);
        deck.shuffle(game.rand);
      }
      if(deck.size() > 0){
        flipped++;
        Card c = deck.draw();
        project.add(c);
        game.queueEvent(new FlipCard(c, getPosition(game)));
      }
    }
    return flipped;
  }
  
  // Attempts to draw a given amount, returns as much as it was able to draw.
  public Deck draw(int amount, Game game){
    Deck d = new Deck();
    for(int k=0;k<amount;k++){
      if(deck.size() == 0 && discard.size() > 0){
        discard.moveAllTo(deck);
        deck.shuffle(game.rand);
      }
      if(deck.size() > 0){
        Card c = deck.draw();
        d.add(c);
      }
    }
    return d;
  }
    
    //return position on board of this start up.
  public int getPosition(Game game){
    return game.start_ups.indexOf(this);
  }
  
  
  public StartUp copy(){
    StartUp s = new StartUp();
    s.deck = deck.copy();
    s.discard = discard.copy();
    s.project = project.copy();
    return s ;
  }
  
  public String toString(){
    return "Project: " + project.toString() + ", Discard:" + discard.toString() +", Deck:" +  deck.toString();
  }
}
