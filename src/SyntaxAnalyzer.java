import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class SyntaxAnalyzer {

    public static final int RESULT_ACCEPT = 0;
    public static final int RESULT_FAILED = 1;
    
    private HashMap<Integer, HashMap<String, Operation>> parsingTable;
    private HashMap<Integer, Production> productions;
    private Stack<GrammarSymbol> parsingStack;
    public SemanticAnalyzer semanticAnalyzer;

    public SyntaxAnalyzer(){
        semanticAnalyzer = new SemanticAnalyzer();
        createProductions();
        createParsingTable();        
        parsingStack = new Stack<GrammarSymbol>();
    } 

    private void createProductions(){
        productions = new HashMap<Integer, Production>();
        productions.put(1, new Production("<E>", "<E>","+","<T>"));
        productions.get(1).getActions().add("sum_e_and_t_save_value");
        productions.put(2, new Production("<E>", "<T>"));
        productions.get(2).getActions().add("save_value_t_on_e");
        productions.put(3, new Production("<T>", "<T>", "*", "<F>"));
        productions.get(3).getActions().add("mult_f_and_t_save_value");
        productions.put(4, new Production("<T>", "<F>"));
        productions.get(4).getActions().add("save_value_f_on_t");
        productions.put(5, new Production("<F>", "(","<E>", ")"));
        productions.get(5).getActions().add("save_value_e_on_f");
        productions.put(6, new Production("<F>", "num"));
        productions.get(6).getActions().add("save_value_num_on_f");
    }

    private void createParsingTable(){
        parsingTable = new HashMap<Integer, HashMap<String, Operation>>();

        //Adding states
        for (int i = 0; i <= 11; i++ ){
            parsingTable.put(i, new HashMap<String, Operation>());
        }
        
        //Adding 0
        parsingTable.get(0).put("num", new Operation(Operation.SHIFT, 5));
        parsingTable.get(0).put("(", new Operation(Operation.SHIFT, 4)); 
        parsingTable.get(0).put("<E>", new Operation(Operation.GOTO, 1));
        parsingTable.get(0).put("<T>", new Operation(Operation.GOTO, 2));
        parsingTable.get(0).put("<F>", new Operation(Operation.GOTO, 3));

        //Adding 1
        parsingTable.get(1).put("+", new Operation(Operation.SHIFT, 6));
        parsingTable.get(1).put("$", new Operation(Operation.ACCEPT, 1));
        //Adding 2
        parsingTable.get(2).put(")", new Operation(Operation.REDUCE, 2));
        parsingTable.get(2).put("+", new Operation(Operation.REDUCE, 2));
        parsingTable.get(2).put("*", new Operation(Operation.SHIFT, 7));
        parsingTable.get(2).put("$", new Operation(Operation.REDUCE, 2));
        //Adding 3
        parsingTable.get(3).put(")", new Operation(Operation.REDUCE, 4));
        parsingTable.get(3).put("+", new Operation(Operation.REDUCE, 4));
        parsingTable.get(3).put("*", new Operation(Operation.REDUCE, 4));
        parsingTable.get(3).put("$", new Operation(Operation.REDUCE, 4));
        //Adding 4
        parsingTable.get(4).put("num", new Operation(Operation.SHIFT, 5));
        parsingTable.get(4).put("(", new Operation(Operation.SHIFT, 4));
        parsingTable.get(4).put("<E>", new Operation(Operation.GOTO, 8));
        parsingTable.get(4).put("<T>", new Operation(Operation.GOTO, 2));
        parsingTable.get(4).put("<F>", new Operation(Operation.GOTO, 3));
        //Adding 5
        parsingTable.get(5).put(")", new Operation(Operation.REDUCE, 6));
        parsingTable.get(5).put("+", new Operation(Operation.REDUCE, 6));
        parsingTable.get(5).put("*", new Operation(Operation.REDUCE, 6));
        parsingTable.get(5).put("$", new Operation(Operation.REDUCE, 6));
        //Adding 6
        parsingTable.get(6).put("num", new Operation(Operation.SHIFT, 5));
        parsingTable.get(6).put("(", new Operation(Operation.SHIFT, 4));
        parsingTable.get(6).put("<T>", new Operation(Operation.GOTO, 9));
        parsingTable.get(6).put("<F>", new Operation(Operation.GOTO, 3));
        //Adding 7
        parsingTable.get(7).put("num", new Operation(Operation.SHIFT, 5));
        parsingTable.get(7).put("(", new Operation(Operation.SHIFT, 4));
        parsingTable.get(7).put("<F>", new Operation(Operation.GOTO, 10));
        //Adding 8
        parsingTable.get(8).put(")", new Operation(Operation.SHIFT, 11));
        parsingTable.get(8).put("+", new Operation(Operation.SHIFT, 6));
        //Adding 9
        parsingTable.get(9).put(")", new Operation(Operation.REDUCE, 1));
        parsingTable.get(9).put("+", new Operation(Operation.REDUCE, 1));
        parsingTable.get(9).put("*", new Operation(Operation.SHIFT, 7));
        parsingTable.get(9).put("$", new Operation(Operation.REDUCE, 1));
        //Adding 10
        parsingTable.get(10).put(")", new Operation(Operation.REDUCE, 3));
        parsingTable.get(10).put("+", new Operation(Operation.REDUCE, 3));
        parsingTable.get(10).put("*", new Operation(Operation.REDUCE, 3));
        parsingTable.get(10).put("$", new Operation(Operation.REDUCE, 3));
        //Adding 11
        parsingTable.get(11).put("+", new Operation(Operation.REDUCE, 5));
        parsingTable.get(11).put("*", new Operation(Operation.REDUCE, 5));
        parsingTable.get(11).put("$", new Operation(Operation.REDUCE, 5));

    }
    
    public int parse(ArrayList<Lexema> tokens){
        //Starting with the stack
        GrammarSymbol state0 = new GrammarSymbol(GrammarSymbol.STATE, "0");
        parsingStack.push(state0);

        //for (Lexema token : tokens){
        for (int i = 0; i < tokens.size(); i++){
        
            Lexema token = tokens.get(i);
  
            //top of stack is state
            if (parsingStack.peek().getType() == GrammarSymbol.STATE){
                Operation toDo = parsingTable.get(Integer.parseInt(parsingStack.peek().getSymbol().toString())).get(token.getSymbol());
                if (toDo != null){
                    if (toDo.getType() == Operation.SHIFT){
                        parsingStack.push( token );
                        parsingStack.push( new GrammarSymbol(GrammarSymbol.STATE, "" + toDo.getState() ) );
                    } else if (toDo.getType() == Operation.REDUCE){
                        Production prod = productions.get(toDo.getProduction());
                        int index = prod.getSymbols().length - 1;
                        i--; //Input symbol no consumido
                        ArrayList<GrammarSymbol> inputforSemanticActions = new ArrayList<GrammarSymbol>();

                        while (index >= 0) { //remove the symbols from the stack

                            if (prod.getSymbols()[index].equals("ε")){
                                index--;
                            } else {
                                parsingStack.pop(); //remove the state
                                GrammarSymbol symbol = parsingStack.pop(); //removes the symbol
                                
                                if (symbol.getType() == GrammarSymbol.TERMINAL){ //Is a Lexema
                                    if (symbol.getSymbol().equals(prod.getSymbols()[index])){
                                        GrammarSymbol semanticItem;
                                        semanticItem = new Lexema(
                                            ((Lexema)symbol).getSymbol()
                                            , ((Lexema)symbol).getValue()
                                            );
                                        
                                        inputforSemanticActions.add(semanticItem);
                                        index--;
                                    } else {
                                        return RESULT_FAILED;
                                    }
                                } else if (symbol.getType() == GrammarSymbol.NONTERMINAL){
                                    if (symbol.getSymbol().equals(prod.getSymbols()[index])){
                                        inputforSemanticActions.add(symbol);
                                        index--;
                                    } else {
                                        return RESULT_FAILED;
                                    }
                                }
                            }
                        }

                        GrammarSymbol nonTerminalKeyProd = new NonTerminalSymbol(prod.getNonTerminalKey());

                        if (prod.getActions().size() > 0){
                            for (String action : prod.getActions()){
                                try {
                                    semanticAnalyzer.ExecuteOperation(action, inputforSemanticActions, nonTerminalKeyProd); //Execute the specific action
                                } catch (Exception e){
                                    return RESULT_FAILED;
                                }
                                
                            }
                        }

                        parsingStack.push(nonTerminalKeyProd);

                    } else if (toDo.getType() == Operation.ACCEPT){
                        return RESULT_ACCEPT;
                    } else {
                        return RESULT_FAILED;
                    }

                } else {
                    return RESULT_FAILED;
                }

            } else if (parsingStack.peek().getType() == GrammarSymbol.NONTERMINAL){

                //Need to find a GoTo
                GrammarSymbol nonTerminal = parsingStack.pop();
                GrammarSymbol lastState = parsingStack.pop();
                i--;
                Operation toDo = parsingTable.get(Integer.parseInt(lastState.getSymbol().toString())).get(nonTerminal.getSymbol());

                if (toDo != null){
                    if (toDo.getType() == Operation.GOTO){
                        parsingStack.push(lastState);
                        parsingStack.push(nonTerminal);
                        parsingStack.push(new GrammarSymbol(GrammarSymbol.STATE, "" + toDo.getState()));
                    } else {
                        return RESULT_FAILED;
                    }

                } else {
                    return RESULT_FAILED;
                }

            } else {
                return RESULT_FAILED;
            }
            //top of stack is non-terminal

        }

        return RESULT_ACCEPT;
    }

}
