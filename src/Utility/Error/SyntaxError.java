package Utility.Error;

import Utility.Position;

public class SyntaxError extends Error{
    public SyntaxError(Position poss, String msgg) {
        super(poss, "Syntax Error : " + msgg);
    }
}
