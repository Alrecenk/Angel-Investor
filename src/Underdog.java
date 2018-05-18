
public class Underdog extends Card {
  
  public static final String UNDERDOG_NAME = "UnderDog";
  
  public Underdog(){
    this.name = UNDERDOG_NAME;
  }

  public boolean canBeStartingCard() {
    return false;
  }

  public Card copy() {
    return new Underdog();
  }

  public double endGamePoints() {
    return 0;
  }

}