package year2024.day7;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ElephantsMath {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2024/day7/input.txt"));

        List<Equation> equations = new ArrayList<>();
        BigInteger sumOfPossibleResults = BigInteger.valueOf(0);
        for (String line : lines) {
            Equation equation = new Equation(line);
            equations.add(equation);
            //part 1
//            List<List<Operator>> allPossibleOperators = equation.generatePossibleOperators();
            //part 2
            List<List<Operator>> allPossibleOperators = equation.generatePossibleOperatorsIncludingConcatenation();
            for (List<Operator> possibleOperators : allPossibleOperators) {
                if(equation.evaluate(possibleOperators).equals(equation.result)) {
                    sumOfPossibleResults = sumOfPossibleResults.add(equation.result);
                    break;
                }
            }
        }
        System.out.println(sumOfPossibleResults);
    }

    static class Equation {
        List<BigInteger> operands;
        BigInteger result;

        public Equation(String line) {
            String[] parts = line.split(": ");
            result = BigInteger.valueOf(Long.parseLong(parts[0]));
            operands = Arrays.stream(parts[1].split(" "))
                    .map(number -> BigInteger.valueOf(Long.parseLong(number)))
                    .toList();
        }

        public BigInteger evaluate(List<Operator> operators) {
            BigInteger result = operators.get(0).evaluate(operands.get(0), operands.get(1));
            for (int i = 1; i < operators.size(); i++) {
                result = operators.get(i).evaluate(result, operands.get(i + 1));
            }
            return result;
        }

        public List<List<Operator>> generatePossibleOperators() {
            int numberOfOperators = operands.size() - 1;
            Integer numberOfOptions = (int) Math.pow(2, numberOfOperators);
            List<List<Operator>> possibleOperators = new ArrayList<>();
            for(int i = 0; i < numberOfOptions; i++) {
                String binaryString = String.format(
                        "%" + numberOfOperators + "s", Integer.toBinaryString(i)
                        ).replace(' ', '0');
                List<Operator> operators = new ArrayList<>();
                for(char c : binaryString.toCharArray()) {
                    operators.add(Operator.fromSymbol(c));
                }
                possibleOperators.add(operators);
            }
            return possibleOperators;
        }

        public List<List<Operator>> generatePossibleOperatorsIncludingConcatenation() {
            int numberOfOperators = operands.size() - 1;
            BigInteger numberOfOptions = BigInteger.valueOf((long) Math.pow(3, numberOfOperators));
            List<List<Operator>> possibleOperators = new ArrayList<>();
            for(BigInteger i = BigInteger.ZERO; i.compareTo(numberOfOptions) < 0; i = i.add(BigInteger.ONE)) {
                String ternaryString = Equation.toTernaryString(i, numberOfOperators);
                List<Operator> operators = new ArrayList<>();
                for(char c : ternaryString.toCharArray()) {
                    operators.add(Operator.fromSymbol(c));
                }
                possibleOperators.add(operators);
            }
            return possibleOperators;
        }

        private static String toTernaryString(BigInteger number, int length) {
            StringBuilder ternary = new StringBuilder();
            BigInteger base = BigInteger.valueOf(3);
            while (number.compareTo(BigInteger.ZERO) > 0) {
                ternary.append(number.mod(base));
                number = number.divide(base);
            }
            while (ternary.length() < length) {
                ternary.append('0');
            }
            return ternary.reverse().toString();
        }
    }

    enum Operator {
        PLUS, MULTIPLY, CONCATENATION;

        public static Operator fromSymbol(char op) {
            if(op == '0') return PLUS;
            else if(op == '1') return MULTIPLY;
            else if(op == '2') return CONCATENATION;
            else throw new IllegalArgumentException("Invalid operator: " + op);
        }

        public BigInteger evaluate(BigInteger leftOperand, BigInteger rightOperand) {
            if(this.equals(PLUS)) return leftOperand.add(rightOperand);
            else if(this.equals(MULTIPLY)) return leftOperand.multiply(rightOperand);
            else if(this.equals(CONCATENATION)) return leftOperand.multiply(BigInteger.valueOf((long) Math.pow(10, rightOperand.toString().length()))).add(rightOperand);
            else throw new IllegalArgumentException("Invalid operator: " + this);
        }
    }
}