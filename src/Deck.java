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
    return cards.remove(cards.size()-1); // Pop
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
  public Deck getUnknown(int position, Player call_back){
    Deck possible = copy();
    possible.shuffle();
    Deck unknown = new Deck();
    for(int k=0;k<possible.size();k++){
      unknown.add(new UnknownCard(possible,position,k,call_back));
    }
    return unknown;
  }

  
  public String toString(){
    return printRange(0,size());
  }

}
