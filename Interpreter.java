import java.util.*;
import java.util.regex.*;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Integer> variables = new HashMap<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                try {
                    interpret(line, variables);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    continue;
                }
            }
        }

        printVariables(variables);
    }

 private static void interpret(String line, Map<String, Integer> variables) {
  
    if (!line.endsWith(";")) {
        throw new RuntimeException("Syntax error: Missing semicolon at the end of the line");
    }

    line = line.substring(0, line.length() - 1).trim();

    String[] parts = line.split("=");
    if (parts.length != 2) {
        throw new RuntimeException("Syntax error");
    }

    String identifier = parts[0].trim();
    String expression = parts[1].trim();

    if (!isValidIdentifier(identifier)) {
        throw new RuntimeException("Invalid identifier: " + identifier);
    }

    try {
        int value = evaluateExpression(expression, variables);
        variables.put(identifier, value);
    } catch (Exception e) {
        throw new RuntimeException("Error evaluating expression: " + e.getMessage());
    }
}

    private static int evaluateExpression(String expression, Map<String, Integer> variables) throws Exception {
        List<String> tokens = tokenizeExpression(expression);
        return evaluateAddSub(tokens, variables);
    }

    private static List<String> tokenizeExpression(String expression) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\d+|[-+*/()]|[a-zA-Z_]\\w*").matcher(expression);

        while (matcher.find()) {
            tokens.add(matcher.group());
        }

        return tokens;
    }

    private static int evaluateAddSub(List<String> tokens, Map<String, Integer> variables) throws Exception {
        List<String> updatedTokens = new ArrayList<>();
        int openParenthesesCount = 0;
        for (String token : tokens) {
            openParenthesesCount += countOccurrences(token, '(');
            int closeParenthesesCount = countOccurrences(token, ')');
            openParenthesesCount -= closeParenthesesCount;

            if (openParenthesesCount > 0) {
                updatedTokens.add(token);
            } else {
                updatedTokens.add(token);
                updatedTokens.add(",");
            }
        }
        if (openParenthesesCount != 0) {
            throw new Exception("Mismatched parentheses");
        }

        StringBuilder expressionBuilder = new StringBuilder();
        for (String updatedToken : updatedTokens) {
            expressionBuilder.append(updatedToken);
        }

        String[] subExpressions = expressionBuilder.toString().split(",");
        int result = 0;
        for (String subExpression : subExpressions) {
            if (!subExpression.trim().isEmpty()) {
                result += evaluateMulDiv(tokenizeExpression(subExpression), variables);
            }
        }

        return result;
    }

    private static int evaluateMulDiv(List<String> tokens, Map<String, Integer> variables) throws Exception {
        int result = evaluateUnary(tokens, variables);

        for (int i = 1; i < tokens.size(); i += 2) {
            String operator = tokens.get(i);
            List<String> operandTokens = Collections.singletonList(tokens.get(i + 1));
            int operand = evaluateUnary(operandTokens, variables);

            if ("*".equals(operator)) {
                result *= operand;
            } else if ("/".equals(operator)) {
                if (operand == 0) {
                    throw new Exception("Division by zero");
                }
                result /= operand;
            } else {
                throw new Exception("Invalid operator: " + operator);
            }
        }

        return result;
    }

    private static int evaluateUnary(List<String> tokens, Map<String, Integer> variables) throws Exception {
        if (tokens.size() == 1) {
            String token = tokens.get(0);
            if (token.startsWith("-")) {
                int operand = evaluateUnary(Collections.singletonList(token.substring(1)), variables);
                return -operand;
            } else if (token.startsWith("+")) {
                return evaluateUnary(Collections.singletonList(token.substring(1)), variables);
            } else if (token.matches("[1-9]\\d*|0")) {
                return Integer.parseInt(token);
            } else if (variables.containsKey(token)) {
                return variables.get(token);
            } else {
                throw new Exception("Invalid token: " + token);
            }
        } else {
            return evaluateExpression(String.join(" ", tokens.subList(1, tokens.size() - 1)), variables);
        }
    }

    private static int countOccurrences(String input, char target) {
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == target) {
                count++;
            }
        }
        return count;
    }

    private static boolean isValidIdentifier(String identifier) {
        return identifier.matches("[a-zA-Z_]\\w*");
    }

    private static void printVariables(Map<String, Integer> variables) {
        for (Map.Entry<String, Integer> entry : variables.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
    }
}
