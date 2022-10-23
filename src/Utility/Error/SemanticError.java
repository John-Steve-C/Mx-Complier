package Utility.Error;

import Utility.Position;

public class SemanticError extends Error{
    public SemanticError(Position poss, String msgg) {
        super(poss, "Semantic Error : " + msgg);
    }
}
