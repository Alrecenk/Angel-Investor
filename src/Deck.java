import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

// A deck is a set of cards with an order that may or may not be visible to players. This may also include player hands and discards.
public class Deck implements Iterable<Card>{
  ArrayList<Card> cards;

  public Deck(){
    cards = new ArrayList<Card>();
  }


  // Adds a card to the top of the deck.
  public void add(Card c){
    cards.add(c);
  }

  public void addCopies(Card c, int copies ){
    for(int k = 0 ; k< copies;k++){
      add(c.copy());
    }
  }

  // Adds all of the cards in the given deck to the top of this deck.
  public void add(Deck d){
    cards.addAll(d.cards);
  }

  //Draws a card from the top of this deck.
  public Card draw(){
    if(cards.size() == 0){
      return null;
    }else{
      return cards.remove(cards.size()-1); // Pop
    }
  }

  // Draws a card form the bottom of the deck.
  // Used in project completion to pull oldest cards first.
  public Card removeFirst(){
    return cards.remove(0);
  }

  public boolean contains(Card c){
    return cards.contains(c);
  }

  // Use the pseudo random one for future repeatability.
  // This should be used for AI related activities that are not part of the game and don't need to be repeatable.
  public void shuffle(){
    shuffle(new Random());
  }


  public void shuffle(Random rand){
    for(int k=0;k<cards.size();k++){
      Card temp = cards.get(k);
      int swap = k + rand.nextInt(cards.size()-k); 
      cards.set(k,cards.get(swap));
      cards.set(swap, temp);
    }
  }
  public Iterator<Card> iterator() {
    return cards.iterator();
  }

  // Returns a non-shallow copy of this deck.
  public Deck copy(){
    Deck copy = new Deck();
    Iterator<Card> i = iterator();
    while(i.hasNext()){
      copy.add(i.next().copy());
    }
    return copy;
  }

  public int size(){
    return cards.size();
  }

  public Card getCard(int p){
    return cards.get(p);
  }

  // Returns the index of the first card matching the given name.
  // or -1 is that card is not in this deck.
  public int findCard(String name){
    for(int k=0; k < cards.size(); k++){
      if(cards.get(k).name == name){
        return k ;
      }
    }
    return -1;
  }

  // Returns the number of the given card that is in this deck.
  // If the deck contains unknown cards then this may return a non-whole number.
  // Works efficiently if all unknown cards have the same possible_cards deck.
  public double countCard(String name){
    int count = 0 ;
    Deck last_possible = null; // Last unknown card deck
    int amount_of_unknowns = 0 ; // Amount of copies of that deck.
    for(int k=0; k < cards.size(); k++){
      Card c = cards.get(k);
      if(c.name == name){
        count++;
      }else if(c instanceof UnknownCard){
        UnknownCard u = (UnknownCard)c;
        Deck possible = u.possible_cards;
        if(possible == last_possible){
          amount_of_unknowns++;
        }else{
          if(last_possible!=null){
            count += last_possible.countCard(name) * amount_of_unknowns / (double)last_possible.size();
          }
          last_possible = possible;
          amount_of_unknowns = 1;
        }
      }
    }
    if(last_possible!=null){
      count += last_possible.countCard(name) * amount_of_unknowns / (double)last_possible.size();
    }
    return count;
  }

  public Card removeCard(int p) {
    return cards.remove(p);
  }

  // Removes the given card from this deck (exact reference match, not equivalent card).
  // returns whether found the card to remove it.
  public boolean removeCard(Card c){
    return cards.remove(c);
  }

  // Move all of the cards out of this deck and put them into the given deck.
  public void moveAllTo(Deck deck) {
    deck.add(this);
    cards = new ArrayList<Card>();
  }

  // Prints a comma separated list with proper grammar for a range of cards in this deck.
  public String printRange(int start, int amount){
    if(amount == 0){
      return "nothing";
    }else{
      String s = cards.get(start).name;
      int t = 1 ;
      while( t < amount - 1){
        s+=", " + cards.get(start + t).name;
        t++;
      }
      if(t < amount){
        if(amount != 2){
          s+=",";
        }
        s+=" and " + cards.get(start+t).name;
      }
      return s ;
    }
  }

  // Returns a copy of this deck where each card is replaced with an unknown card that's possible from these cards.
  // The possible cards will be a copy and shuffled so no order can be inferred.
  // The position should be the location of this Deck, so the cards can be marked for the call_back.
  public Deck getUnknown(int position){
    Deck possible = copy();
    possible.shuffle();
    Deck unknown = new Deck();
    for(int k=0;k<possible.size();k++){
      unknown.add(new UnknownCard(possible,position,k));
    }
    return unknown;
  }


  public String toString(){
    return printRange(0,size());
  }

  
  public static Deck getMainDeck(){
    Deck main_deck = new Deck();
    main_deck.addCopies(new ViralMarketing(), 13);
    main_deck.addCopies(new Capital(), 10);
    main_deck.addCopies(new Profit(), 10);
    main_deck.addCopies(new Sabotage(), 8);
    main_deck.addCopies(new Breakthrough(), 7);
    main_deck.addCopies(new Pivot(), 5);
    main_deck.addCopies(new Damages(), 4);
    main_deck.addCopies(new EpicFail(), 4);
    main_deck.addCopies(new Planning(), 4);
    main_deck.addCopies(new Funnel(), 3);
    main_deck.addCopies(new Delay(), 3);
    main_deck.addCopies(new PressRelease(), 3);
    main_deck.addCopies(new Scandal(), 3);
    main_deck.addCopies(new PublicityStunt(), 3);
    main_deck.addCopies(new Spinoff(), 3);
    main_deck.addCopies(new Poach(), 3);
    main_deck.addCopies(new Documentation(), 2);
    main_deck.addCopies(new Lawyers(), 2);
    main_deck.addCopies(new Taxes(), 2);
    main_deck.addCopies(new Rush(), 1);
    main_deck.addCopies(new OpenSource(), 1);
    main_deck.addCopies(new Patent(), 1);
    main_deck.addCopies(new Bust(), 1);
    main_deck.addCopies(new Boom(), 1);
    main_deck.addCopies(new Infamy(), 1);
    main_deck.addCopies(new Nonprofit(), 1);
    main_deck.addCopies(new Underdog(), 1);
    return main_deck;
  }
  
  public static Deck getCompetitiveMainDeck(int players){
    Deck main_deck = new Deck();
    main_deck.addCopies(new ViralMarketing(), 13 - 3*players);
    main_deck.addCopies(new Capital(), 10 - 2*players);
    main_deck.addCopies(new Profit(), 10 - 2*players);
    main_deck.addCopies(new Sabotage(), 8 - 2*players);
    main_deck.addCopies(new Breakthrough(), 7 - players);
    main_deck.addCopies(new Pivot(), 5 - players);
    main_deck.addCopies(new Damages(), 4 - players);
    main_deck.addCopies(new EpicFail(), 4 - players);
    main_deck.addCopies(new Planning(), 4 - players);
    main_deck.addCopies(new Funnel(), 3);
    main_deck.addCopies(new Delay(), 3);
    main_deck.addCopies(new PressRelease(), 3);
    main_deck.addCopies(new Scandal(), 3);
    main_deck.addCopies(new PublicityStunt(), 3);
    main_deck.addCopies(new Spinoff(), 3);
    main_deck.addCopies(new Poach(), 3);
    main_deck.addCopies(new Documentation(), 2);
    main_deck.addCopies(new Lawyers(), 2);
    main_deck.addCopies(new Taxes(), 2);
    main_deck.addCopies(new Rush(), 1);
    main_deck.addCopies(new OpenSource(), 1);
    main_deck.addCopies(new Patent(), 1);
    main_deck.addCopies(new Bust(), 1);
    main_deck.addCopies(new Boom(), 1);
    main_deck.addCopies(new Infamy(), 1);
    main_deck.addCopies(new Nonprofit(), 1);
    main_deck.addCopies(new Underdog(), 1);
    return main_deck;
    
  }
  
  public static Deck getCompetitivePlayerDeck(){
    Deck deck = new Deck();
    deck.addCopies(new ViralMarketing(), 3);
    deck.addCopies(new Capital(), 2);
    deck.addCopies(new Profit(), 2);
    deck.addCopies(new Sabotage(), 3);
    deck.addCopies(new Breakthrough(), 1);
    deck.addCopies(new Pivot(), 1);
    deck.addCopies(new Damages(), 1);
    deck.addCopies(new EpicFail(), 1);
    deck.addCopies(new Planning(), 1);
    return deck;
    
  }

}
