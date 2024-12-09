package year2023.day8;

import lombok.Data;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DesertNavigation {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("src/main/resources/year2023/day8/input.txt"));
        List<Direction> directions = Stream.of(lines.get(0).split("")).map(Direction::parseDirection).toList();
        List<Node> nodes = new ArrayList<>();
        for(int i = 2; i < lines.size(); i++) {
            nodes.add(new Node(lines.get(i)));
        }

        Map<String, Node> nodesMap = nodes.stream().collect(Collectors.toMap(Node::getNodeName, Function.identity()));
        Node currentNode = nodesMap.get("AAA");
        BigInteger stepsCount = BigInteger.ZERO;
        for(int i = 0; i < directions.size(); i++) {
            if(currentNode.nodeName.equals("ZZZ")) {
                break;
            }
            stepsCount = stepsCount.add(BigInteger.ONE);
            if(directions.get(i).equals(Direction.L)) {
                currentNode = nodesMap.get(currentNode.leftNode);
            }
            if(directions.get(i).equals(Direction.R)) {
                currentNode = nodesMap.get(currentNode.rightNode);
            }
            if((i+1) == directions.size()) {
                i = -1;
            }
        }
        System.out.println(stepsCount);

    }

    @Data
    static class Node {
        String nodeName;
        String leftNode;
        String rightNode;

        public Node(String line) {
            String[] parts = line.split(" = ");
            this.nodeName = parts[0];
            parts[1] = parts[1].replaceAll("\\(", "").replaceAll("\\)", "");
            String[] nodes = parts[1].split(", ");
            this.leftNode = nodes[0];
            this.rightNode = nodes[1];
        }

        @Override
        public String toString() {
            return "Node{" +
                    "nodeName='" + nodeName + '\'' +
                    ", leftNode='" + leftNode + '\'' +
                    ", rightNode='" + rightNode + '\'' +
                    '}';
        }
    }

    enum Direction {
        L,
        R;

        static Direction parseDirection(String symbol) {
            if(symbol.equals("R")) return R;
            return L;
        }
    }
}
