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

  // The probability that each player will win a project of "drawn" cards from this startup
  // Any remaining probability is the chance of a tie.
  // Careful with this. It's O(drawn ^ (players+1)). Keep drawn low.
  public double[] winProbabilities(int drawn){
    
    int cards = deck.size() + discard.size() + project.size();
    drawn = Math.min(drawn, cards);
    double money[] = new double[Money.MONEY_NAMES.length];
    int nonmoney = cards;
    for(int k=0;k<Money.MONEY_NAMES.length;k++){
      String m = Money.MONEY_NAMES[k];
      money[k] = deck.countCard(m) + discard.countCard(m) + project.countCard(m);
      nonmoney -= money[k];
    }
    double winChance[] = new double[Money.MONEY_NAMES.length];
    int amount[] = new int[Money.MONEY_NAMES.length];
    
    long cards_drawn_coeff = binomialCoefficient(cards,drawn);
    
    while(true){

      // First just compute just whether permutation is valid .This all we do most of the time. TODO iterate over only valid numbers?
      int total = 0 ;
      boolean valid = true;
      for(int k=0;k<amount.length;k++){ 
        total += amount[k];
        valid &= amount[k] <= money[k];
      }
      valid &= total <= drawn && drawn-total <= nonmoney;
      if(valid){
        //System.out.println("valid:" + printArray(amount));
        // Determine winner.
        int winner = Game.NOONE;
        int win_amount = 0;
        for(int k=0;k<amount.length;k++){
          if(amount[k] > win_amount){
            winner = k;
            win_amount = amount[k];
          }else if(amount[k] == win_amount){
            winner = Game.NOONE;
          }
        }
        //System.out.println("Winner:" + winner);
        if(winner != Game.NOONE){ // If there's a winner than that player's win chance is increased by hypergeometric probability.
          double chance = binomialCoefficient(nonmoney, drawn-total)/(double)cards_drawn_coeff;
          for(int k=0;k<amount.length;k++){
            chance *= binomialCoefficient((int)money[k], amount[k]);
          }
          //System.out.println("Chance:" +chance);
            winChance[winner] += chance;
        }
      }

      int i = 0 ;
      while(amount[i] == drawn){
        amount[i] = 0 ;
        i++;
        if(i == money.length){ // If we've iterated them all to max
          return winChance;
        }
      }
      amount[i]++;
    }


  }

    
  // Calculate an n choose k type binomial coefficient.
  public static long binomialCoefficient(int total, int choose){
    long res = 1;
    // Since C(n, k) = C(n, n-k)
    if ( choose > total - choose ){
      choose = total - choose;
    }
    for (int i = 0; i < choose; i++){
      res *= (total - i);
      res /= (i + 1);
    }
    return res;
  }
  public static String printArray(double a[]){
    String s = ""+a[0];
    for(int k=1;k<a.length;k++){
      s+=", " + a[k];
    }
    return s ;
  }
  
  public static String printArray(int a[]){
    String s = ""+a[0];
    for(int k=1;k<a.length;k++){
      s+=", " + a[k];
    }
    return s ;
  }
  
  public static void main(String agrs[]){
    StartUp s = new StartUp();
    s.deck.add(new Money(0));
    System.out.println(printArray(s.winProbabilities(1)));
    s.discard.add(new Money(1));
    System.out.println(printArray(s.winProbabilities(1)));
    System.out.println(printArray(s.winProbabilities(2)));
    s.project.add(new Capital());
    System.out.println(printArray(s.winProbabilities(2)));
    s.deck.add(new Money(0));
    System.out.println(printArray(s.winProbabilities(2)));
    s.discard.add(new Money(1));
    System.out.println(printArray(s.winProbabilities(3)));
    s.discard.add(new Money(2));
    s.discard.add(new Money(0));
    System.out.println(printArray(s.winProbabilities(3)));
    s.project.add(new Capital());
    s.discard.add(new Money(0));
    s.discard.add(new Money(0));
    System.out.println(printArray(s.winProbabilities(3)));
    System.out.println(printArray(s.winProbabilities(5)));
  }
    

}
