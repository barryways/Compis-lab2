import java.util.HashMap;
import java.util.ArrayList;

public class SemanticAnalyzer {

    private HashMap<String, SymbolItem> symbolsTable;

    public SemanticAnalyzer(){
        symbolsTable = new HashMap<String, SymbolItem>();

        symbolsTable.put("E", new SymbolItem("E", SymbolItem.VARIABLE, SymbolItem.INT, SymbolItem.GLOBAL, 1, null));
        symbolsTable.put("T", new SymbolItem("T", SymbolItem.VARIABLE, SymbolItem.INT, SymbolItem.GLOBAL, 2, null));
        symbolsTable.put("F", new SymbolItem("F", SymbolItem.VARIABLE, SymbolItem.INT, SymbolItem.GLOBAL, 3, null));
    }

    public void ExecuteOperation(String operation, ArrayList<GrammarSymbol> lexemas, GrammarSymbol symbol) {
        try {
            switch (operation) {
                case "sum_e_and_t_save_value":
                    executeSum(operation, lexemas, (lexemas.get(0)).getSymbol());
                    break;
                case "save_value_t_on_e":
                    String actualTValue = (String) symbolsTable.get("T").getValue();
                    saveValueTonE(actualTValue);
                    System.out.println("Llega acá con valor en E de ");
                    SymbolItem valueE = symbolsTable.get("E");
                    System.out.println(valueE.getValue());
                    break;
                case "mult_f_and_t_save_value":
                    executeMult(operation, lexemas, (lexemas.get(0)).getSymbol());
                    break;
                
                case "save_value_f_on_t":
                    String actualFValue = (String) symbolsTable.get("F").getValue();
                    saveValueFonT(actualFValue);
                    System.out.println("Llega acá con valor en T de ");
                    SymbolItem valueT = symbolsTable.get("T");
                    System.out.println(valueT.getValue());
                    break;
                case "save_value_e_on_f":
                    String actualEValue = (String) symbolsTable.get("E").getValue();
                    saveValueEonF(actualEValue);
                    System.out.println("Llega acá con valor en F de ");
                    SymbolItem valueF1 = symbolsTable.get("F");
                    System.out.println(valueF1.getValue());
                    break;
                case "save_value_num_on_f":
                    saveValueNUMonF(lexemas, (lexemas.get(0)).getSymbol());
                    System.out.println("Llega acá con valor en F de ");
                    SymbolItem valueF2 = symbolsTable.get("F");
                    System.out.println(valueF2.getValue());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // Captura la excepción y muestra un mensaje
            System.out.println("Error al ejecutar la operación: " + operation);
            System.out.println("Mensaje de la excepción: " + e.getMessage());
        }
    }



    private void executeSum(String operation, ArrayList<GrammarSymbol> lexemas, String variableType) throws Exception{
        // Valores de E y T de la tabla hash
        String valueEString = (String) symbolsTable.get("E").getValue();
        String valueTString = (String) symbolsTable.get("T").getValue();
        int valueEint = Integer.parseInt(valueEString);
        int valueTint = Integer.parseInt(valueTString);

        int sum = valueEint + valueTint;

        // Guardar el resultado de la suma en el símbolo E actualizando su valor
        symbolsTable.get("E").setValue(String.valueOf(sum));

        System.out.println("El nuevo valor de E después de sumar E y T es: " + sum);
    }
    private void executeMult(String operation, ArrayList<GrammarSymbol> lexemas, String variableType) throws Exception{
        String valueFString = (String) symbolsTable.get("F").getValue();
        String valueTString = (String) symbolsTable.get("T").getValue();
        int valueFint = Integer.parseInt(valueFString);
        int valueTint = Integer.parseInt(valueTString);

        int product = valueFint * valueTint;

        symbolsTable.get("T").setValue(String.valueOf(product));

        System.out.println("El nuevo valor de T después de multiplicar F y T es: " + product);

    }
    private void saveValueTonE( String TValue) throws Exception{
        symbolsTable.put("E", new SymbolItem("E", SymbolItem.VARIABLE, SymbolItem.INT, SymbolItem.GLOBAL, 1, TValue));
    }
    private void saveValueFonT(String FValue ) throws Exception{
        symbolsTable.put("T", new SymbolItem("T", SymbolItem.VARIABLE, SymbolItem.INT, SymbolItem.GLOBAL, 2, FValue));
    }
    private void saveValueEonF(String EValue) throws Exception{
        symbolsTable.put("F", new SymbolItem("F", SymbolItem.VARIABLE, SymbolItem.INT, SymbolItem.GLOBAL, 3, EValue));
    }
    private void saveValueNUMonF( ArrayList<GrammarSymbol> lexemas, String variableType) throws Exception{
        String valor =  ((Lexema)lexemas.get(0)).getValue();
        symbolsTable.put("F", new SymbolItem("F", SymbolItem.VARIABLE, SymbolItem.INT, SymbolItem.GLOBAL, 3, valor));
    }

}