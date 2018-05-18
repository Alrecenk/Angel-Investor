import java.util.Iterator;


public class ViralMarketing extends Card{

  public static final String VIRAL_MARKETING_NAME = "Viral Marketing";

  public ViralMarketing(){
    this.name = VIRAL_MARKETING_NAME;
  }

  // gives a viral marketing point to the player who currently has the most money in the project where it is.
  public void giveViralPoint(int row, Game game, Event e){
    int money_count[] = new int[game.numPlayers()];
    Iterator<Card> project_iterator = game.start_ups.get(row).project.iterator();
    while(project_iterator.hasNext()){
      Card c = project_iterator.next();
      if(c instanceof Money){
        money_count[((Money)c).player]++;
      }
    }
    int max_money = 0 ;
    int fame_winner = Game.NOONE;
    for(int k=0;k<money_count.length;k++){
      if(money_count[k] > max_money){
        max_money = money_count[k];
        fame_winner = k;
      }else if(money_count[k] == max_money){ // If tie for max no winner.
        fame_winner = Game.NOONE;
      }
    }

    if(fame_winner != Game.NOONE){
      game.getPlayer(fame_winner).giveFame();
    }
    e.addChoice(fame_winner);
  }

  public void invest(Player player, int row, Game game, Event e) {
    giveViralPoint(row, game, e);
  }

  public void flip(Player player, int row, Game game, Event e) {
    giveViralPoint(row, game, e);
  }

  public boolean canBeStartingCard() {
    return true;
  }

  public Card copy() {
    return new ViralMarketing();
  }
  public double endGamePoints() {
    return 1;
  }

  //Prints a FlipCard event for this card having just executed its flip command.
  public String flipPrint(Player current, int which_startup, Game game, FlipCard e){
    int fame_winner = e.readChoice();
    if(fame_winner == Game.NOONE){
      return "Viral Marketing triggered on flip, but did not give a point due to a tie.";
    }else{
      Player p = game.getPlayer(fame_winner);
      return "Viral Marketing triggered on flip. " + p.getName() +" recieved a fame and now has " + p.getFame() +".";
    }
  }

  //Prints an InvestCard event for this card having just been invested and executed.
  public String investPrint(Player current, int which_start_up, Game game, InvestCard e){
    int fame_winner = e.readChoice();
    if(fame_winner == Game.NOONE){
      return current.getName() + " invested Viral Marketing in row " + which_start_up +", which triggered but did not give a fame due to a tie.";
    }else{
      Player p = game.getPlayer(fame_winner);
      return current.getName() +" invested Viral Marketing in row " + which_start_up +". " + p.getName() +" recieved a fame and now has " + p.getFame() +".";
    }
  }

}
