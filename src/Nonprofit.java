
public class Nonprofit extends Card {
  
  public static final String NONPROFIT_NAME = "Nonprofit";
  
  public Nonprofit(){
    this.name = NONPROFIT_NAME;
  }

  public boolean canBeStartingCard() {
    return false;
  }

  public Card copy() {
    return new Nonprofit();
  }

  public double endGamePoints() {
    return 0;
  }

}