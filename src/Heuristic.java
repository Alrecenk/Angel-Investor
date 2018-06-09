// A heuristic for scoring game states.

public abstract class Heuristic {

  // Returns the score of this game for the given player.
  public abstract double score(Game g, int player_number);
}
