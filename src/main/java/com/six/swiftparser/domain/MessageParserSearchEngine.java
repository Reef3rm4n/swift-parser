package com.six.swiftparser.domain;

import java.util.*;

public class MessageParserSearchEngine {

    private Map<String, List<MessageParser>> graph = new HashMap<>();
    public void addParser(MessageParser parser) {
        String sourceFormat = parser.getSourceFormat();
        if (!graph.containsKey(sourceFormat)) {
            graph.put(sourceFormat, new ArrayList<>());
        }
        graph.get(sourceFormat).add(parser);
    }

    // BFS method to find the shortest path of parsers
    public List<MessageParser> findConversionPath(String source, String target) {
        if (!graph.containsKey(source)) {
            return Collections.EMPTY_LIST; // No available parsers from the source
        }

        Queue<List<MessageParser>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        visited.add(source);

        // Initial paths from the source format
        for (MessageParser parser : graph.get(source)) {
            List<MessageParser> initialPath = new ArrayList<>();
            initialPath.add(parser);
            queue.add(initialPath);
        }

        while (!queue.isEmpty()) {
            List<MessageParser> currentPath = queue.poll();
            MessageParser lastParser = currentPath.get(currentPath.size() - 1);
            String lastFormat = lastParser.getTargetFormat();

            if (lastFormat.equals(target)) {
                return currentPath; // Found the conversion path
            }

            // Explore neighbors from the last format in the current path
            if (graph.containsKey(lastFormat)) {
                for (MessageParser nextParser : graph.get(lastFormat)) {
                    if (!visited.contains(nextParser.getTargetFormat())) {
                        visited.add(nextParser.getTargetFormat());
                        List<MessageParser> newPath = new ArrayList<>(currentPath);
                        newPath.add(nextParser);
                        queue.add(newPath);
                    }
                }
            }
        }
        return Collections.EMPTY_LIST; // No path found if queue is exhausted
    }
}
