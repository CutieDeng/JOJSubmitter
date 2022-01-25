package codeparse;

public interface Parser {
    /**
     * Evaluate whether there exists a token or not to read. <br>
     * <p/>
     *
     * @return true if there are tokens left.
     */
    boolean hasNextToken();

    /**
     * Get the next token. <br>
     *
     * @return The next token.
     */
    TokenRecord nextToken();
}
