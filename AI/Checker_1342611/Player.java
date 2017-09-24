import java.util.*;

public class Player {

    public int alpha_val = Integer.MAX_VALUE;
    public int beta_val = Integer.MIN_VALUE;

    public int me;
    int max_depth = 8;
    String temp;
    int hitten;


    public GameState play(final GameState pState, final Deadline pDue) {

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }
        me = pState.getNextPlayer();

        GameState Final_move = null;
        GameState state;
        int maximum_score = Integer.MIN_VALUE;

        for (int i = 0; i < lNextStates.size(); i++) {

            state = lNextStates.elementAt(i);
            int score = alphabeta(beta_val, alpha_val, max_depth, state, false);

            if (score > maximum_score) {
                maximum_score = score;
                Final_move = state;
            } else if (score == maximum_score) {
                if (Math.random() < 0.5) {
                    maximum_score = score;
                    Final_move = state;
                }
            }
        }
        return Final_move;

    }

    public int evaluate(GameState gameState, int team) {
        
        int Red_king_count = 0;
        int White_king_count = 0;
        int Red_normal_count = 0;
        int White_normal_count = 0;
        int total_count_score = 0;

        if (gameState.isEOG()) {
            if (team == 1 && gameState.isRedWin()) {
                total_count_score = 1000;
                return total_count_score;
            } else if (team == 1 && gameState.isWhiteWin()) {
                total_count_score = -1000;
                return total_count_score;
            } else if (team == 2 && gameState.isRedWin()) {
                total_count_score = -1000;
                return total_count_score;
            } else if (team == 2 && gameState.isWhiteWin()) {
                total_count_score = 1000;
                return total_count_score;
            } else {
                total_count_score = -1000;
                return total_count_score;
            }
        }
        else
        {
            // Check if we capture an opponent checker
            temp = "" + gameState.toMessage().charAt(33);
            hitten = Integer.parseInt(temp);
            if (hitten > 0) {
                total_count_score += 3 * hitten;
            }

            for (int i = 0; i < gameState.NUMBER_OF_SQUARES; i++) {
                if (0 != (gameState.get(i) & Constants.CELL_RED)) {
                    if (team == 1) {
                        if (gameState.cellToCol(i) == 0 || gameState.cellToCol(i) == 7
                                || gameState.cellToRow(i) == 0 || gameState.cellToRow(i) == 7) {
                            total_count_score += 2;
                        } else if (gameState.cellToCol(i) == 1 || gameState.cellToCol(i) == 6) {
                            total_count_score += 1;
                        }
                    } else {
                        if (gameState.cellToCol(i) == 0 || gameState.cellToCol(i) == 7
                                || gameState.cellToRow(i) == 0 || gameState.cellToRow(i) == 7) {
                            total_count_score -= 2;
                        } else if (gameState.cellToCol(i) == 1 || gameState.cellToCol(i) == 6) {
                            total_count_score -= 1;
                        }
                    }
                    Red_normal_count += 4;
                    if (0 != (gameState.get(i) & Constants.CELL_KING)) {
                        Red_king_count += 3;
                    } else {
                        if (team == 1) {
                            if (gameState.cellToRow(i) == 6 || gameState.cellToRow(i) == 5) {
                                total_count_score += 1;
                            }
                        } else {
                            if (gameState.cellToRow(i) == 6 || gameState.cellToRow(i) == 5) {
                                total_count_score -= 1;
                            }
                        }
                    }
                } else if (0 != (gameState.get(i) & Constants.CELL_WHITE)) {
                    if (team == 2) {
                        if (gameState.cellToCol(i) == 0 || gameState.cellToCol(i) == 7
                                || gameState.cellToRow(i) == 0 || gameState.cellToRow(i) == 7) {
                            total_count_score += 2;
                        } else if (gameState.cellToCol(i) == 1 || gameState.cellToCol(i) == 6) {
                            total_count_score += 1;
                        }
                    } else {
                        if (gameState.cellToCol(i) == 0 || gameState.cellToCol(i) == 7
                                || gameState.cellToRow(i) == 0 || gameState.cellToRow(i) == 7) {
                            total_count_score -= 2;
                        } else if (gameState.cellToCol(i) == 1 || gameState.cellToCol(i) == 6) {
                            total_count_score -= 1;
                        }
                    }
                    White_normal_count += 4;
                    if (0 != (gameState.get(i) & Constants.CELL_KING)) {
                        White_king_count += 3;
                    } else {
                        if (team == 2) {
                            if (gameState.cellToRow(i) == 1 || gameState.cellToRow(i) == 2) {
                                total_count_score += 1;
                            }
                        } else {
                            if (gameState.cellToRow(i) == 1 || gameState.cellToRow(i) == 2) {
                                total_count_score -= 1;
                            }
                        }
                    }
                }
            }
            if (team == 1) {
                total_count_score += Red_king_count;
                total_count_score += Red_normal_count;
                total_count_score -= White_king_count;
                total_count_score -= White_normal_count;
                return total_count_score;
            } else {
                total_count_score += White_king_count;
                total_count_score += White_normal_count;
                total_count_score -= Red_king_count;
                total_count_score -= Red_normal_count;

                return total_count_score;
            }
        }

    }

    public int alphabeta(int alpha, int beta, int depth, GameState gameState,
            Boolean maxplayer) {

        if ((depth <= 0) || gameState.isEOG()) {
            return evaluate(gameState, me);
        }

        Vector<GameState> Possible_next_states = new Vector<GameState>();
        gameState.findPossibleMoves(Possible_next_states);

        int alphabeta_return_value;

        // Maximizing player
        if (maxplayer) {
            alphabeta_return_value = beta_val;

            for (GameState i : Possible_next_states) {

                alphabeta_return_value = Math.max(alphabeta_return_value,
                        alphabeta(alpha, beta, depth - 1, i, false));
                alpha = Math.max(alpha, alphabeta_return_value);
                if (beta <= alpha) {
                    break;
                }
            }
            return alphabeta_return_value;
        }
        // Minimizing player
        else {
            alphabeta_return_value = alpha_val;
            for (GameState i : Possible_next_states) {
                alphabeta_return_value = Math.min(alphabeta_return_value,
                        (alphabeta(alpha, beta, depth - 1, i, true)));
                beta = Math.min(beta, alphabeta_return_value);
                if (beta <= alpha) {
                    break;
                }
            }
            return alphabeta_return_value;
        }
    }
}